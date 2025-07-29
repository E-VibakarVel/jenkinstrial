pipeline {
    // Specifies that the pipeline can run on any available agent
    agent any 

    // Configure tools like Maven and JDK 
    tools {
        maven 'Maven_3_9_3' //  Ensure you've configured Maven in Jenkins under "Manage Jenkins" -> "Global Tool Configuration"
        jdk 'JDK_17'       // Ensure you've configured JDK in Jenkins under "Manage Jenkins" -> "Global Tool Configuration"
    }
    environment{
         // Define the base name of your application JAR
        APP_NAME = "nextgen-mock" 
        // Get the Jenkins build number for versioning
        BUILD_NUMBER_VAR = "${env.BUILD_NUMBER}" 
        // Get the current timestamp (you might need the Build Timestamp plugin for more robust options)
        TIMESTAMP = sh(script: "date +%Y%m%d%H%M%S", returnStdout: true).trim()
        // Construct the versioned JAR name
        VERSIONED_JAR_NAME = "${APP_NAME}-${BUILD_NUMBER_VAR}-${TIMESTAMP}.war"
    }

    parameters{
        gitParameter{
            name:'BRANCH',
            type:'PT_BRANCH',
            branchFilter:'refs/heads/.*'
        } 
    }

    // Define the sequence of stages in the pipeline
    stages {
        // Stage for building the project
        stage('debug branches'){
            steps{
                echo "git branch param ${params.BRANCH}"
                sh ''' ls-remote --heads https://github.com/E-VibakarVel/jenkinstrial.git'''
            }
        }
        stage('checkout'){
            steps{
                git branch:"${params.BRANCH}",url:'https://github.com/E-VibakarVel/jenkinstrial.git'
            }
        }
        stage('Build') {
            steps {
                // Execute Maven clean and package goals, skipping tests
                bat 'mvn -B -DskipTests clean package' //
                echo 'Maven build completed successfully'
            }
        }

        // Stage for running tests
        stage('Test') {
            steps {
                // Execute Maven test goal
                bat 'mvn test' //
                echo 'Maven tests executed'
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
                bat 'WAR generated'

                // Archive the generated JAR for later use
                archiveArtifacts artifacts: 'target/*.war', fingerprint: true //
            }
        }
         stage('Upload to S3 with Versioning and Error Handling') {
            steps {
                script {
                    try {
                        // Stash the JAR file
                        stash includes: "**/target/*.war", name: 'nextgen'
                        unstash 'nextgen' // Unstash the JAR file

                         // Debug: list files in the target directory
                         sh "ls -l $WORKSPACE/target/"

                        // Rename the JAR with versioning and upload to S3
                        sh "mv $WORKSPACE/target/${APP_NAME}.jar $WORKSPACE/target/${VERSIONED_JAR_NAME}" 
                        // sh "aws s3 cp $WORKSPACE/target/${VERSIONED_JAR_NAME} s3://your-s3-bucket-name/${env.BRANCH_NAME}/" 

                        bat "Successfully uploaded ${VERSIONED_JAR_NAME} to S3 bucket: your-s3-bucket-name/${env.BRANCH_NAME}/"
                    } catch (Exception e) {
                        bat "Error uploading JAR to S3: ${e.message}"
                        currentBuild.result = 'FAILURE' // Mark the build as failed
                        // You can add additional actions here, like sending a notification
                    }
                }
            }
        }
    }

    // Post-build actions, regardless of pipeline success or failure
    post { 
        always {
            bat 'Pipeline finished' 
        }
        success {
            bat 'Pipeline succeeded!' 
        }
        failure {
            bat 'Pipeline failed!' 
        }
    }
}
