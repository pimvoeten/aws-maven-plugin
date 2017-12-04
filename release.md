## Deploy to snapshot version to staging nexus:
Leave the -SNAPSHOT suffix in version.

    mvn clean deploy -P release

## Do the actual release    
Set the version to be released:

    mvn versions:set -DnewVersion=1.2.3

    mvn clean deploy -P release
    
    mvn versions:set -DnewVersion=1.2.4-SNAPSHOT

Commit everything to github.

