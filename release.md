## Deploy a new version to Maven Central

    mvn versions:set -DnewVersion=1.2.3
    mvn clean deploy -P release
    mvn nexus-staging:release -P release

If something went wrong, you can do:

    mvn nexus-staging:drop
