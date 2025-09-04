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
//         TIMESTAMP = powershell (script: "Get-Date -Format 'yyyyMMddHHmmss'", returnStdout: true).trim()
        // Construct the versioned JAR name
        VERSIONED_WAR_NAME = "${RENAMED_WARNAME}-${BUILD_NUMBER_VAR}.war"
        SONARQUBE_SERVER ="sonarqubelocalserver"
        SONARQUBE_SCANNER = "sonar-scanner"
    }


    // Define the sequence of stages in the pipeline
    stages {
        // Stage for building the project

        stage('Clone Repository'){
            steps{
                git branch:"${params.BRANCH}",url:'https://github.com/E-VibakarVel/jenkinstrial.git'
            }
        }
        stage('user Check'){
        steps{
        sh 'whoami'
        sh 'id'
        }
        }
        stage('Compile Project') {
            steps {
                // Execute Maven clean and package goals, skipping tests
                sh 'mvn compile' //
                sh "echo Maven compile completed successfully"
            }
        }

        // Stage for running tests
        stage('Test') {
            steps {
                // Execute Maven test goal
                sh 'mvn test' //
                sh "echo Maven tests executed"
            }
//             post {
//                 // Archive JUnit test results regardless of build status
//                 always {
//                     junit 'target/surefire-reports/*.xml' //
//                 }
//             }
        }

         //sonarqube

                stage('Sonarqube Analysis'){
                    steps{
                        withSonarQubeEnv(SONARQUBE_SERVER){
                            sh 'mvn clean verify sonar:sonar'
                        }
                    }
                }

        // Stage for generating the JAR artifact and archiving it
        stage('Build Project') {
            steps {
                // Execute Maven package goal to build the JAR
                sh 'mvn package' //
                sh "echo WAR generated"

                // Archive the generated JAR for later use
                archiveArtifacts artifacts: 'target/*.war', fingerprint: true //
            }
        }
         stage('War Versioning') {
            steps {
                sh "echo inside s3stage"
                script {
                    try {
                        // Stash the JAR file
                        stash includes: "**/target/*.war", name: 'nextgen'
                        unstash 'nextgen' // Unstash the JAR file
                        sh "echo after unstash"
                         // Debug: list files in the target directory
                        //  bat "dir %WORKSPACE%\\target"

                        // Rename the JAR with versioning and upload to S3
                         sh  "mv ${WORKSPACE}/target/${APP_NAME}.war ${WORKSPACE}/target/${VERSIONED_WAR_NAME}"
                        // bat "aws s3 cp $WORKSPACE/target/${VERSIONED_WAR_NAME} s3://ngs-testing-system-tcs/vibakarvel/jenkins//${env.BRANCH_NAME}/"
                    } catch (Exception e) {
                        sh "Error uploading JAR to S3: ${e.message}"
                        currentBuild.result = 'FAILURE' // Mark the build as failed
                        // You can add additional actions here, like sending a notification
                    }
                }
            }
        }
        stage('Upload to S3') {
        steps {
        withAWS(credentials: 'awscredentials', region: 'ap-northeast-1') { // Replace with your credentials ID and region
            sh "aws s3 cp $WORKSPACE/target/${VERSIONED_WAR_NAME} s3://ngs-testing-system-tcs/vibakarvel/jenkins/${env.GIT_BRANCH}/"
        }
           sh "echo Successfully uploaded ${VERSIONED_WAR_NAME} to S3 bucket: ngs-testing-system-tcs/vibakarvel/jenkins/${env.GIT_BRANCH}/"
         }
        }

        stage('Check tomcat folder access'){
        steps{
        sh 'ls -la /opt/tomcat/webapps'
        }
        }

        stage('war Deployment'){
            steps{
            withCredentials([string(credentialsId:'SUDO_PASS',variable:'SUDO_PASS')]){

                script{
                    def tomcatService = 'tomcat'
                    def webappsDir = '/opt/tomcat/webapps'
                    def s3Bucket = "s3://ngs-testing-system-tcs/vibakarvel/jenkins/${env.GIT_BRANCH}/"
                    sh "sudo /opt/tomcat/bin/./shutdown.sh"
                    sh"""

                    existing_war=\$(ls ${webappsDir}/jenkinstrial-*.war 2>/dev/null || true)
                    if [ -n "\$existing_war" ]: then
                    echo "Deleting existing WAR : \$existing_war"
                    rm -f \$existing_war
                    else
                    echo "NO existing war found"
                    fi

                    """

                    //download new war from s3

                    sh "/opt/tomcat/webapps/ aws s3 cp ${s3Bucket}/${VERSIONED_WAR_NAME} ."

                //start tomcat
                sh  "/opt/tomcat/webapps/./startup.sh"
                }
                }
            }
        }


    }

    // Post-build actions, regardless of pipeline success or failure
    post {
        always {
            sh "echo Pipeline finished"
        }
        success {
            sh "echo Pipeline succeeded!"
        }
        failure {
            sh "echo Pipeline failed!"
        }
    }
}