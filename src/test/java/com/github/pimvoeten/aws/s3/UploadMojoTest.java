package com.github.pimvoeten.aws.s3;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.junit.Ignore;

import java.io.File;

// TODO: java.lang.NoSuchMethodError: org.apache.maven.artifact.versioning.DefaultArtifactVersion.compareTo(Lorg/apache/maven/artifact/versioning/ArtifactVersion;)I
@Ignore
public class UploadMojoTest extends AbstractMojoTestCase {

    private UploadMojo s3UploadMojo;

    protected void setUp() throws Exception {
        super.setUp();
    }

    /**
     * @throws Exception
     */
    public void testMojoGoal() throws Exception {
        File testPom = new File(getBasedir(),
                "src/test/resources/empty-pom.xml");

        UploadMojo mojo = (UploadMojo) lookupMojo("upload", testPom);

        assertNotNull(mojo);
    }

}
