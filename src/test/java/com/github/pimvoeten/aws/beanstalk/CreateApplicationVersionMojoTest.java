package com.github.pimvoeten.aws.beanstalk;

import com.github.pimvoeten.aws.s3.UploadMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;

import java.io.File;

public class CreateApplicationVersionMojoTest extends AbstractMojoTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testProfileAndCredentials() throws Exception {
        File pom = getTestFile("src/test/resources/bothCredentialsPom.xml");
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
}
