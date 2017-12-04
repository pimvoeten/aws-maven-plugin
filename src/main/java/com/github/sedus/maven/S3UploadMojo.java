package com.github.sedus.maven;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSCredentialsProviderChain;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.model.fileset.FileSet;
import org.apache.maven.shared.model.fileset.util.FileSetManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Maven Mojo for uploading artifacts and files to S3.
 */
@Mojo(name = "upload")
public class S3UploadMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    /**
     * <code>true</code> if the projects artifact is included for upload or not.
     */
    @Parameter(property = "upload.includeArtifact", defaultValue = "true")
    private boolean includeArtifact;

    /**
     * Target bucketname.
     */
    @Parameter(property = "upload.bucket", required = true)
    private String bucket;

    /**
     * Upload to the given path inside the bucket.
     */
    @Parameter(property = "upload.path")
    private String path;

    /**
     * AWS Access key.
     * Required in combination with secretKey and region.
     */
    @Parameter(property = "upload.accessKey")
    private String accessKey;

    /**
     * AWS Secret key.
     * Required in combination with accessKey and region.
     */
    @Parameter(property = "upload.secretKey")
    private String secretKey;

    /**
     * Used for authentication on S3 client.
     * Required in combination with secretKey and accessKey.
     */
    @Parameter(property = "upload.region")
    private String region;

    /**
     * Profile name used for authentication using aws-cli credentials.
     * Not required when ~/.aws/credentials profile name is default.
     */
    @Parameter(property = "upload.awsProfile")
    private String awsProfile;

    /**
     * Extra or specific file to include in the upload.
     */
    @Parameter
    private FileSet[] filesets;

    private AmazonS3 s3Client;

    /**
     * Execute method.
     *
     * @throws MojoExecutionException
     * @throws MojoFailureException
     */
    public void execute() throws MojoExecutionException, MojoFailureException {
        validateParams();
        uploadArtifact();
        uploadFiles();
    }

    private void validateParams() throws MojoExecutionException {
        // Check files to upload
        if ((filesets == null || filesets.length == 0) && !includeArtifact) {
            throw new MojoExecutionException("No artifacts to upload. No filesets configured and includeArtifact is false");
        }

        // Check S3 credentials params
        if (accessKey == null ^ secretKey == null) {
            throw new MojoExecutionException("Set both accessKey and secretKey params");
        }
    }

    private void uploadArtifact() {
        if (includeArtifact) {
            File artifact = project.getArtifact().getFile();
            if (artifact != null) {
                uploadFileToBucket(artifact);
            }
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
                    uploadFileToBucket(new File(fileSet.getDirectory(), file));
                });
            });
        }
    }

    private void uploadFileToBucket(File file) {
        s3Client = getS3Client();
        String key = path != null ? path + "/" : "";
        key += file.getName();

        getLog().info("Uploading: " + file.getAbsolutePath());
        s3Client.putObject(bucket, key, file);
    }

    private AmazonS3 getS3Client() {
        if (s3Client == null) {
            List<AWSCredentialsProvider> providers = new ArrayList<>();
            if (accessKey != null && secretKey != null) {
                AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
                providers.add(new AWSStaticCredentialsProvider(credentials));
            }
            if (awsProfile != null) {
                providers.add(new ProfileCredentialsProvider(awsProfile));
            }
            // Always add "default" profile
            providers.add(new ProfileCredentialsProvider());

            AWSCredentialsProviderChain credentialsProviderChain = new AWSCredentialsProviderChain(providers);

            AmazonS3ClientBuilder s3ClientBuilder = AmazonS3ClientBuilder.standard()
                    .withRegion(region)
                    .withCredentials(credentialsProviderChain);
            s3Client = s3ClientBuilder.build();
        }
        return s3Client;
    }

}
