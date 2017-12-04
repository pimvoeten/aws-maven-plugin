package com.github.sedus.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

@RunWith(MockitoJUnitRunner.class)
public class S3UploadMojoTest {

    @InjectMocks
    private S3UploadMojo s3UploadMojo;

    @Mock
    private MavenProject project;

    @Test
    public void testWithNoParams() {
        try {
            s3UploadMojo.execute();
        } catch (MojoExecutionException e) {
            assertThat(e.getMessage(), CoreMatchers.equalTo("No artifacts to upload. No filesets configured and includeArtifact is false"));
        } catch (MojoFailureException e) {
            fail("No MojoFailureException was expected");
        }
    }

}
