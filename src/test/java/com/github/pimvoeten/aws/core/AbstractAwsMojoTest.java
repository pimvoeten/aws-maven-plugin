package com.github.pimvoeten.aws.core;

import com.github.pimvoeten.aws.s3.UploadMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.hamcrest.CoreMatchers;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertThat;

public class AbstractAwsMojoTest extends AbstractMojoTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testNoConfiguration() throws Exception {
        File pom = getTestFile("src/test/resources/core/empty.xml");
        assertNotNull(pom);
        assertTrue(pom.exists());

        UploadMojo mojo = (UploadMojo) lookupMojo("S3-putObject", pom);

        assertNotNull(mojo);
        try {
            mojo.execute();
        } catch (MojoExecutionException e) {
            assertEquals("No credentials configured", e.getMessage());
        }
    }

    public void testProfileAndCredentials() throws Exception {
        File pom = getTestFile("src/test/resources/core/profileAndCredentials.xml");
        assertNotNull(pom);
        assertTrue(pom.exists());

        UploadMojo mojo = (UploadMojo) lookupMojo("S3-putObject", pom);

        assertNotNull(mojo);
        try {
            mojo.execute();
        } catch (MojoExecutionException e) {
            assertEquals("Set credentials or use profile, not both", e.getMessage());
        }
    }

    public void testInvalidCredentials() throws Exception {
        File pom = getTestFile("src/test/resources/core/invalidCredentials.xml");
        assertNotNull(pom);
        assertTrue(pom.exists());

        UploadMojo mojo = (UploadMojo) lookupMojo("S3-putObject", pom);

        assertNotNull(mojo);
        try {
            mojo.execute();
        } catch (MojoExecutionException e) {
            assertEquals("Invalid credentials", e.getMessage());
        }
    }

    @Ignore
    public void testInvalidProfile() throws Exception {
        File pom = getTestFile("src/test/resources/core/invalidProfile.xml");
        assertNotNull(pom);
        assertTrue(pom.exists());

        UploadMojo mojo = (UploadMojo) lookupMojo("S3-putObject", pom);

        assertNotNull(mojo);
        try {
            mojo.execute();
        } catch (MojoExecutionException e) {
            assertThat(e.getMessage(), CoreMatchers.containsString("Profile file contained no credentials for profile 'test-unknown-profile'"));
        }
    }

    @Test
    public void getClient() {
    }

    @Test
    public void doExecute() {
    }

    @Test
    public void execute() {
    }

    @Test
    public void getRegion() {
    }

    @Test
    public void getAwsCredentialsProviderChain() {
    }
}
