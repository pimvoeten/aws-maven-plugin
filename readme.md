# aws-maven-plugin

This project is a Maven plugin to integrate AWS into the Maven lifecycle.

## Credentials
AWS needs authentication which can be done in 3 ways:
1. set the accessKey and secretKey in the pom
2. set a profile in the pom. The profile can be configured using aws-cli ```aws configure``` (profiles and keys are stored in ~/.aws/credentials)
3. default profile from aws-cli (no xml config needed)

### Use of assumedRoles
Sometimes users can be given temporal permissions on a different account. This can be done by using assumedRoles. 
Configuration in the credentials files can look like this:

    [usual-profile-name]
    aws_access_key_id=...
    aws_secret_access_key=...
    
    [assumedrole-profile-name]
    role_arn = <arn of role on different account>
    source_profile = usual-profile-name

What will happen is that the assumed role is linked to the usual profile. The usual credentials are used to get a session token from STS, which is used to
perform actions on that different account.
In this setup use ***[assumedrole-profile-name]*** in the configuration.

## General configuration

```
  <plugin>
    <groupId>com.github.pimvoeten</groupId>
    <artifactId>aws-maven-plugin</artifactId>
    <version>LATEST</version>
    <configuration>
      <!-- 1) Authenticate using keys -->
      <accessKey>ACCESS-KEY</accessKey>
      <secretKey>SECRET-KEY</secretKey>
      <region>AWS-REGION</region>

      <!-- 2) Authenticate by ~/.aws/credentials profile -->
      <profile>PROFILE-NAME</profile>

    </configuration>
  </plugin>
```

| property        |                                          |
| --------------- | ---------------------------------------- |
| accessKey       | your AWS accessKey. **required in combination with secretKey and region** |
| secretKey       | your AWS secretKey **required in combination with accesKey and region** |
| region          | your AWS region **required in combination with accessKey and secretKey** |
| awsProfile      | The name of the profile from  ~/.aws/credentials. Optional if you already logged in using `aws configure` |

## Goals
### s3
#### putObject

```
  <plugin>
    <groupId>com.github.pimvoeten</groupId>
    <artifactId>aws-maven-plugin</artifactId>
    <version>LATEST</version>
    <executions>
        <execution>
            <id>upload to S3</id>
            <phase>install</phase>
            <goals>
                <goal>S3-putObject</goal>
            </goals>
            <configuration>
                <bucket>BUCKET-NAME</bucket>
                <path>PATH-IN-BUCKET</path>
                <createBucket>true</createBucket>
                <includeArtifact>false</includeArtifact>
                <filesets>
                    <fileset>
                        <directory>relative-path</directory>
                        <includes>
                            <include>**.*</include>
                        </includes>
                    </fileset>
                </filesets>
            </configuration>
        </execution>
    </executions>
  </plugin>
```

| property        |                                          |
| --------------- | ---------------------------------------- |
| bucket          | The name of the AWS bucket to upload to. **required** |
| createBucket    | Create the bucket if it doesn't exist *optional* |
| path            | The path to upload to. *Optional*        |
| includeArtifact | Include the current projects artifact *Optional* |
| filesets        | Add extra files to upload by setting filesets *Optional* |

### Beanstalk
#### CreateApplicationVersion

Create a new application version in the Beanstalk environment.

```
  <plugin>
    <groupId>com.github.pimvoeten</groupId>
    <artifactId>aws-maven-plugin</artifactId>
    <version>LATEST</version>
    <executions>
        <execution>
            <id>Create new app version</id>
            <phase>deploy</phase>
            <goals>
                <goal>EB-CreateApplicationVersion</goal>
            </goals>
            <configuration>
                <bucket>BUCKET-NAME</bucket>
                <key>SOURCE-BUNDLE-KEY</key>
                <applicationName>APPLICATION-NAME</applicationName>
                <versionLabel>VERSION-LABEL</versionLabel>
                <description>DESCRIPTION</description>
            </configuration>
        </execution>
    </executions>
  </plugin>
```

| property        |                                          |
| --------------- | ---------------------------------------- |
| bucket          | The name of the AWS bucket where the source bundle resides. **required** |
| key            | The key of the source bundle. **required**        |
| applicationName | The name of the application that is deployed **required** |
| versionLabel    | The version that is deployed **required** |
| description     | Extra description that can be added *Optional* |

#### UpdateEnvironment

Deploy an application version to Beanstalk managed resources.

```
  <plugin>
    <groupId>com.github.pimvoeten</groupId>
    <artifactId>aws-maven-plugin</artifactId>
    <version>LATEST</version>
    <executions>
        <execution>
            <id>Update environment with new version</id>
            <phase>deploy</phase>
            <goals>
                <goal>EB-UpdateEnvironment</goal>
            </goals>
            <configuration>
                <applicationName>APPLICATION-NAME</applicationName>
                <versionLabel>VERSION-LABEL</versionLabel>
                <environmentName>ENVIRONMENT-NAME</environmentName>
            </configuration>
        </execution>
    </executions>
  </plugin>
```

| property        |                                          |
| --------------- | ---------------------------------------- |
| applicationName | The name of the application that is deployed **required** |
| versionLabel    | The version that is deployed **required** |
| environmentName | Extra description that can be added *Optional* |

