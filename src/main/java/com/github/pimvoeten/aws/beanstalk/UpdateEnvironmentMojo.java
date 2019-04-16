package com.github.pimvoeten.aws.beanstalk;

import com.github.pimvoeten.aws.core.AbstractAwsMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import software.amazon.awssdk.services.elasticbeanstalk.ElasticBeanstalkClient;
import software.amazon.awssdk.services.elasticbeanstalk.model.CreateApplicationVersionRequest;
import software.amazon.awssdk.services.elasticbeanstalk.model.S3Location;
import software.amazon.awssdk.services.elasticbeanstalk.model.UpdateEnvironmentRequest;

@Mojo(name = "EB-UpdateEnvironment")
public class UpdateEnvironmentMojo extends AbstractAwsMojo<ElasticBeanstalkClient> {

    private ElasticBeanstalkClient elasticBeanstalkClient;

    /**
     * Application versionLabel.
     */
    @Parameter(property = "versionLabel",
               required = true)
    private String versionLabel;

    /**
     * Name of the application.
     */
    @Parameter(property = "applicationName",
               required = true)
    private String applicationName;

    /**
     * Environment name
     */
    @Parameter(property = "environmentName")
    private String environmentName;

    @Override
    public void doExecute() throws MojoExecutionException {
        ElasticBeanstalkClient client = getClient();
        getLog().info("Updating environment to: [" + applicationName + ", " + versionLabel + "]");
        client.updateEnvironment(
            UpdateEnvironmentRequest.builder()
                .environmentName(environmentName)
                .applicationName(applicationName)
                .versionLabel(versionLabel)
                .build());
        getLog().info("Updating environment finished");
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
