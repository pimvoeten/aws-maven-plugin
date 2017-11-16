S3-maven-plugin
===

Maven plugin for uploading files to S3 buckets.

# Goals
``s3:upload``

# Usage
AWS authentication can be done in 3 ways:
1. set the accessKey and secretKey in the pom
2. set a profile in the pom. The profile must be configured in aws-cli (~/.aws/credentials)
3. default profile from aws-cli (no xml config needed)

# Configuration

```           <plugin>
                   <groupId>com.sedus</groupId>
                   <artifactId>s3-maven-plugin</artifactId>
                   <version>1.0-SNAPSHOT</version>
                   <configuration>
                       <!-- keys -->
                       <accessKey>ACCESS-KEY</accessKey>
                       <secretKey>SECRET-KEY</secretKey>
                       <region>AWS-REGION</region>
   
                       <!-- ~/.aws/credentials profile -->
                       <awsProfile>foo</awsProfile>
   
                       <!-- Target bucket and path-->
                       <bucket>my-bucket</bucket>
                       <path>some-dir</path>
   
                       <!-- Whether to include the current projects artifact -->
                       <includeArtifact>true</includeArtifact>
                   </configuration>
                   <executions>
                       <execution>
                           <phase>deploy</phase>
                           <goals>
                               <goal>upload</goal>
                           </goals>
                       </execution>
                   </executions>
               </plugin>
    ```
