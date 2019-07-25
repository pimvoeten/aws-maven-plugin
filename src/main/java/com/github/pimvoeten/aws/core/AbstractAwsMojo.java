package com.github.pimvoeten.aws.core;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProviderChain;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.SdkClient;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.exception.SdkServiceException;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.regions.providers.DefaultAwsRegionProviderChain;

public abstract class AbstractAwsMojo<S extends SdkClient> extends AbstractMojo {

    @Parameter(defaultValue = "${project}",
               readonly = true,
               required = true)
    protected MavenProject project;

    /**
     * AWS Access key.
     * Required in combination with secretKey and region.
     */
    @Parameter(property = "accessKey")
    protected String accessKey;

    /**
     * AWS Secret key.
     * Required in combination with accessKey and region.
     */
    @Parameter(property = "secretKey")
    protected String secretKey;

    /**
     * Used for authentication on clients.
     * Required in combination with secretKey and accessKey.
     */
    @Parameter(property = "region")
    protected String regionName;

    /**
     * Profile name used for authentication using aws-cli credentials.
     */
    @Parameter(property = "profile")
    protected String profile;

    protected abstract S getClient();

    protected abstract void doExecute() throws MojoExecutionException;

    /**
     * Execute method.
     *
     * @throws MojoExecutionException
     * @throws MojoFailureException
     */
    public void execute() throws MojoExecutionException {
        try {
            this.validateCredentials();
            this.doExecute();
        } catch (SdkServiceException e) {
            if (e.statusCode() == 403) {
                throw new MojoExecutionException("Invalid credentials");
            }
        } catch (
            SdkClientException e) {
            throw new MojoExecutionException(e.getMessage());
        }

    }

    private void validateCredentials() throws MojoExecutionException {
        boolean profileUsed = profile != null;

        if (profile == null && accessKey == null && secretKey == null) {
            throw new MojoExecutionException("No credentials configured");
        }

        if (profileUsed && (accessKey != null | secretKey != null)) {
            throw new MojoExecutionException("Set credentials or use profile, not both");
        }

        if (!profileUsed && (accessKey == null ^ secretKey == null)) {
            throw new MojoExecutionException("Set both accessKey and secretKey params");
        }
    }

    /**
     * Returns the region to use in any of the AWS client builders.
     *
     * @return
     */
    protected Region getRegion() {
        // Set the Region
        Region region;
        if (regionName == null) {
            DefaultAwsRegionProviderChain regionProviderChain = new DefaultAwsRegionProviderChain();
            region = regionProviderChain.getRegion();
        } else {
            region = Region.of(this.regionName);
        }
        return region;
    }

    /**
     * Creates an AWS CredentialsProviderChain for use with any AWS client builders.
     *
     * @return
     */
    protected AwsCredentialsProviderChain getAwsCredentialsProviderChain() {
        AwsCredentialsProviderChain.Builder credentialsProviderChainBuilder = AwsCredentialsProviderChain.builder();

        // use given profile
        if (this.profile != null) {
            credentialsProviderChainBuilder.addCredentialsProvider(
                ProfileCredentialsProvider.builder()
                    .profileName(this.profile)
                    .build()
            );
        }

        // use given access- and secretKey
        if (accessKey != null && secretKey != null) {
            AwsBasicCredentials awsCreds = AwsBasicCredentials.create(
                accessKey,
                secretKey);
            credentialsProviderChainBuilder.addCredentialsProvider(StaticCredentialsProvider.create(awsCreds));
        }

        return credentialsProviderChainBuilder.build();
    }

}
