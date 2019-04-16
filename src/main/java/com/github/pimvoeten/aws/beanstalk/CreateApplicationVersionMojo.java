package com.github.pimvoeten.aws.beanstalk;

import com.github.pimvoeten.aws.core.AbstractAwsMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import software.amazon.awssdk.services.elasticbeanstalk.ElasticBeanstalkClient;
import software.amazon.awssdk.services.elasticbeanstalk.model.CreateApplicationVersionRequest;
import software.amazon.awssdk.services.elasticbeanstalk.model.S3Location;

@Mojo(name = "EB-CreateApplicationVersion")
public class CreateApplicationVersionMojo extends AbstractAwsMojo<ElasticBeanstalkClient> {

    private ElasticBeanstalkClient elasticBeanstalkClient;

    /**
     * Application versionLabel.
     */
    @Parameter(property = "versionLabel",
               required = true)
    private String versionLabel;

    /**
     * Bucket where the sourcebundle resides.
     */
    @Parameter(property = "bucket",
               required = true)
    private String bucket;

    /**
     * Key of the sourcebundle.
     */
    @Parameter(property = "key",
               required = true)
    private String key;

    /**
     * Name of the application.
     */
    @Parameter(property = "applicationName",
               required = true)
    private String applicationName;

    /**
     * Description
     */
    @Parameter(property = "description")
    private String description;

    @Override
    public void doExecute() throws MojoExecutionException {
        ElasticBeanstalkClient client = getClient();
        getLog().info("Creating new application version: [" + applicationName + ", " + versionLabel + "]");
        client.createApplicationVersion(
            CreateApplicationVersionRequest.builder()
                .versionLabel(versionLabel)
                .autoCreateApplication(false)
                .sourceBundle(S3Location.builder().s3Bucket(bucket).s3Key(key).build())
                .applicationName(applicationName)
                .description(description)
                .build());
        getLog().info("Creating new application version finished");
    }

    @Override
    protected ElasticBeanstalkClient getClient() {
        if (elasticBeanstalkClient == null) {
            this.elasticBeanstalkClient = ElasticBeanstalkClient.builder()
                .credentialsProvider(getAwsCredentialsProviderChain())
                .region(getRegion())
                .build();
        }
        return elasticBeanstalkClient;
    }
}
