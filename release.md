## Deploy a new version to Maven Central

Set the version to a release version (without the -SNAPSHHOT suffix)

    mvn versions:set
    git add 
    mvn clean deploy -P release
    mvn nexus-staging:release -P release

If something went wrong, you can do:

    mvn nexus-staging:drop

## Troubleshooting

Sometimes the nexus-staging-maven-plugin shows errors and warnings:

    An illegal reflective access operation has occurred

It seems to go away when executed a second time.
