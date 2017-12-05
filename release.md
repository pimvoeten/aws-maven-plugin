## Deploy to snapshot version to staging nexus:
Leave the -SNAPSHOT suffix in version.

    mvn clean deploy -P release

## Do the actual release    
Set the version to be released:

    mvn nexus-staging:release -P release
    
If the staging releaase was not good enough:

    mvn nexus-staging:drop

After releasing, set the new version

    mvn versions:set -DnewVersion=1.2.4-SNAPSHOT

Commit everything to github.

