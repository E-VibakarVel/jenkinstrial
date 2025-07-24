pipeline {
    // Specifies that the pipeline can run on any available agent
    agent any 

    // Configure tools like Maven and JDK 
    tools {
        maven 'Maven_3_9_3' //  Ensure you've configured Maven in Jenkins under "Manage Jenkins" -> "Global Tool Configuration"
        jdk 'JDK_17'       // Ensure you've configured JDK in Jenkins under "Manage Jenkins" -> "Global Tool Configuration"
    }

    // Define the sequence of stages in the pipeline
    stages {
        // Stage for building the project
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
        stage('Generate JAR') { 
            steps {
                // Execute Maven package goal to build the JAR
                bat 'mvn package' //
                echo 'WAR generated'

                // Archive the generated JAR for later use
                archiveArtifacts artifacts: 'target/*.war', fingerprint: true //
            }
        }
    }

    // Post-build actions, regardless of pipeline success or failure
    post { 
        always {
            echo 'Pipeline finished' 
        }
        success {
            echo 'Pipeline succeeded!' 
        }
        failure {
            echo 'Pipeline failed!' 
        }
    }
}
