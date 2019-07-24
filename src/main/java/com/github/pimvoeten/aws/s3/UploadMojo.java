package com.github.pimvoeten.aws.s3;

import com.github.pimvoeten.aws.core.AbstractAwsMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.shared.model.fileset.FileSet;
import org.apache.maven.shared.model.fileset.util.FileSetManager;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketConfiguration;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.CreateBucketResponse;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketResponse;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * Maven Mojo for uploading artifacts and files to S3.
 */
@Mojo(name = "S3-putObject")
public class UploadMojo extends AbstractAwsMojo<S3Client> {

    /**
     * <code>true</code> if the projects artifact is included for upload or not.
     */
    @Parameter(property = "includeArtifact",
               defaultValue = "true")
    private boolean includeArtifact;

    /**
     * Target bucketname.
     */
    @Parameter(property = "bucket",
               required = true)
    private String bucket;

    /**
     * Should the bucket be created if it doesn't exists yet
     * default <code>false</code>
     */
    @Parameter(property = "createBucket",
               defaultValue = "false")
    private boolean createBucket;

    /**
     * Upload to the given path inside the bucket (this is not the key).
     */
    @Parameter(property = "path")
    private String path;

    /**
     * Extra or specific file to include in the upload.
     */
    @Parameter
    private FileSet[] filesets;

    private S3Client s3Client;

    /**
     * Execute method.
     *
     * @throws MojoExecutionException
     * @throws MojoFailureException
     */
    protected void doExecute() throws MojoExecutionException {
        validateParams();
        boolean bucketExists = doesBucketExist();
        if (!bucketExists && !createBucket) {
            throw new MojoExecutionException(
                "Bucket [" + this.bucket + "] does not exists. Create the bucket or configure this plugin to create the bucket.");
        }
        if (!bucketExists && createBucket) {
            createBucket();
        }
        if (includeArtifact) {
            uploadArtifact();
        }
        uploadFiles();
    }

    private void validateParams() throws MojoExecutionException {
        // Check files to upload
        if ((filesets == null || filesets.length == 0) && !includeArtifact) {
            throw new MojoExecutionException("No artifacts to upload. No filesets configured and includeArtifact is false");
        }
    }

    private boolean doesBucketExist() {
        S3Client s3 = this.getClient();
        try {
            HeadBucketResponse headBucketResponse = s3.headBucket(HeadBucketRequest.builder().bucket(this.bucket).build());
            return 200 == headBucketResponse.sdkHttpResponse().statusCode();
        } catch (NoSuchBucketException e) {
            return false;
        }
    }

    private void createBucket() {
        getLog().info("Creating bucket [" + this.bucket + "]");
        S3Client s3 = this.getClient();
        CreateBucketRequest createBucketRequest = CreateBucketRequest
            .builder()
            .bucket(bucket)
            .createBucketConfiguration(
                CreateBucketConfiguration.builder().build())
            .build();
        CreateBucketResponse response = s3.createBucket(createBucketRequest);

        getLog().info("Bucket created: " + response.toString());
    }

    private void uploadArtifact() {
        File artifact = project.getArtifact().getFile();
        if (artifact != null) {
            Path file = artifact.toPath();
            uploadFileToBucket(file);
        }
    }

    private void uploadFiles() {
        FileSetManager fileSetManager = new FileSetManager();
        if (filesets != null && filesets.length > 0) {
            Stream.of(filesets).forEach(fileSet -> {
                File dir = new File(project.getBasedir(), fileSet.getDirectory());
                fileSet.setDirectory(dir.getAbsolutePath());
                String[] includedFiles = fileSetManager.getIncludedFiles(fileSet);

                Stream.of(includedFiles).forEach(file -> {
                    uploadFileToBucket(new File(fileSet.getDirectory(), file).toPath());
                });
            });
        }
    }

    private void uploadFileToBucket(Path file) {
        S3Client s3 = getClient();
        String key = path != null ? path + "/" : "";
        key += file.getFileName().toString();

        getLog().info("Uploading: " + file.toString());
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
            .bucket(bucket)
            .key(key)
            .build();
        s3.putObject(putObjectRequest, file);
    }

    /**
     * Create S3Client
     *
     * @return
     */
    @Override
    protected S3Client getClient() {
        if (s3Client == null) {
            this.s3Client = S3Client.builder()
                .credentialsProvider(getAwsCredentialsProviderChain())
                .region(getRegion())
                .build();
        }
        return s3Client;
    }

}
