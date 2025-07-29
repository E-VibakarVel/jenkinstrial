pipeline {
    // Specifies that the pipeline can run on any available agent
    agent any 

    // Configure tools like Maven and JDK 
    tools {
        maven 'Maven_3_9_3' //  Ensure you've configured Maven in Jenkins under "Manage Jenkins" -> "Global Tool Configuration"
        jdk 'JDK_17'       // Ensure you've configured JDK in Jenkins under "Manage Jenkins" -> "Global Tool Configuration"
    }

    parameters{
        string(name:'BRANCH') 
    }
    environment{
         // Define the base name of your application JAR
        APP_NAME = "jenkinstrial-0.0.1-SNAPSHOT" 
        RENAMED_WARNAME = "MOCK-JENKINS-PIPELINE"
        // Get the Jenkins build number for versioning
        BUILD_NUMBER_VAR = "${env.BUILD_NUMBER}" 
        // Get the current timestamp (you might need the Build Timestamp plugin for more robust options)
        TIMESTAMP = powershell (script: "Get-Date -Format 'yyyyMMddHHmmss'", returnStdout: true).trim()
        // Construct the versioned JAR name
        VERSIONED_WAR_NAME = "${RENAMED_WARNAME}-${BUILD_NUMBER_VAR}-${TIMESTAMP}.war"
    }


    // Define the sequence of stages in the pipeline
    stages {
        // Stage for building the project

        stage('checkout'){
            steps{
                git branch:"${params.BRANCH}",url:'https://github.com/E-VibakarVel/jenkinstrial.git'
            }
        }
        stage('Build') {
            steps {
                // Execute Maven clean and package goals, skipping tests
                bat 'mvn -B -DskipTests clean package' //
                bat "echo Maven build completed successfully"
            }
        }

        // Stage for running tests
        stage('Test') {
            steps {
                // Execute Maven test goal
                bat 'mvn test' //
                bat "echo Maven tests executed"
            }
            post {
                // Archive JUnit test results regardless of build status
                always {
                    junit 'target/surefire-reports/*.xml' //
                }
            }
        }

        // Stage for generating the JAR artifact and archiving it
        stage('Generate WAR') { 
            steps {
                // Execute Maven package goal to build the JAR
                bat 'mvn package' //
                bat "echo WAR generated"

                // Archive the generated JAR for later use
                archiveArtifacts artifacts: 'target/*.war', fingerprint: true //
            }
        }
        //  stage('Upload to S3 with Versioning and Error Handling') {
        //     steps {
        //         bat "echo inside s3stage"
        //         script {
        //             try {
        //                 // Stash the JAR file
        //                 stash includes: "**/target/*.war", name: 'nextgen'
        //                 unstash 'nextgen' // Unstash the JAR file
        //                 bat "echo after unstash"
        //                  // Debug: list files in the target directory
        //                  bat "dir %WORKSPACE%\\target"
        //                  bat "echo check"
        //                 // Rename the JAR with versioning and upload to S3
        //                  bat  "move \"%WORKSPACE%\\target\\${APP_NAME}.war\" \"%WORKSPACE%\\target\\${VERSIONED_WAR_NAME}\""
        //                 bat "aws s3 cp $WORKSPACE/target/${VERSIONED_WAR_NAME} s3://ngs-testing-system-tcs/vibakarvel/jenkins//${env.BRANCH_NAME}/" 

        //                 bat "echo Successfully uploaded ${VERSIONED_WAR_NAME} to S3 bucket: your-s3-bucket-name/${env.BRANCH_NAME}/"
        //             } catch (Exception e) {
        //                 bat "Error uploading JAR to S3: ${e.message}"
        //                 currentBuild.result = 'FAILURE' // Mark the build as failed
        //                 // You can add additional actions here, like sending a notification
        //             }
        //         }
        //     }
        // }
        stage('Upload to S3') {
        steps {
        withAWS(credentials: 'awscredentials', region: 'ap-northeast-1') { // Replace with your credentials ID and region
            bat "aws s3 cp $WORKSPACE/target/${VERSIONED_WAR_NAME} s3://ngs-testing-system-tcs/vibakarvel/jenkins//${env.BRANCH_NAME}/" 
        }
         }
        }
    }

    // Post-build actions, regardless of pipeline success or failure
    post { 
        always {
            bat "echo Pipeline finished" 
        }
        success {
            bat "echo Pipeline succeeded!" 
        }
        failure {
            bat "echo Pipeline failed!" 
        }
    }
}
