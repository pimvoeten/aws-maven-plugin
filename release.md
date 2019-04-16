## Deploy a new version to Maven Central

    mvn clean deploy -P release
    mvn nexus-staging:release -P release

If something went wrong, you can do:

    mvn nexus-staging:drop
