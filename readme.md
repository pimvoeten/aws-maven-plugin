# aws-maven-plugin

This project is a Maven plugin to integrate AWS into the Maven lifecycle.

## Credentials
AWS needs authentication which can be done in 3 ways:
1. set the accessKey and secretKey in the pom
2. set a profile in the pom. The profile can be configured using aws-cli ```aws configure``` (profiles and keys are stored in ~/.aws/credentials)
3. default profile from aws-cli (no xml config needed)

## Configuration

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

      <!-- Target bucket and path-->
      <bucket>BUCKET-NAME</bucket>
      <path>PATH-IN-BUCKET</path>
      <createBucket>true</createBucket>

      <!-- Include the current projects artifact -->
      <includeArtifact>true</includeArtifact>

      <!-- Add extra files -->
      <filesets>
        <fileset>
          <directory>relative-path</directory>
          <includes>
 	         <include>**.*</include>
          </includes>
      	</fileset>
      </filesets>
    </configuration>
  </plugin>

| property        |                                          |
| --------------- | ---------------------------------------- |
| accessKey       | your AWS accessKey. **required in combination with secretKey and region** |
| secretKey       | your AWS secretKey **required in combination with accesKey and region** |
| region          | your AWS region **required in combination with accessKey and secretKey** |
| awsProfile      | The name of the profile from  ~/.aws/credentials. Optional if you already logged in using `aws configure` |
| bucket          | The name of the AWS bucket to upload to. **required** |
| createBucket    | Create the bucket if it doesn't exist *optional* |
| path            | The path to upload to. *Optional*        |
| includeArtifact | Include the current projects artifact *Optional* |
| filesets        | Add extra files to upload by setting filesets *Optional* |

## Goals
### s3
#### upload
