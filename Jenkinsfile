pipeline {
    agent any

    tools {
        // Optional: Only use if Maven and JDK17 are configured in Jenkins
        maven 'Maven'
        jdk 'JDK17'
    }

    stages {
        stage('Checkout') {
            steps {
                echo "📦 Cloning repository..."
                git branch: 'main', url: 'https://github.com/Arsalanstriker/Student_Course_Registration_System.git'
            }
        }

        stage('Build') {
            steps {
                script {
                    echo "🔨 Cleaning and compiling..."
                    if (isUnix()) {
                        sh 'mvn clean compile'
                    } else {
                        bat 'mvn clean compile'
                    }
                }
            }
        }

        stage('Test') {
            steps {
                script {
                    echo "🧪 Running tests..."
                    if (isUnix()) {
                        sh 'mvn test'
                    } else {
                        bat 'mvn test'
                    }
                }
            }
        }

        stage('Package') {
            steps {
                script {
                    echo "📦 Packaging..."
                    if (isUnix()) {
                        sh 'mvn package'
                    } else {
                        bat 'mvn package'
                    }
                }
            }
        }

        stage('Archive Artifacts') {
            steps {
                echo "💾 Archiving JAR..."
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }

        stage('Debug Info') {
            steps {
                script {
                    echo "📋 Showing environment info..."
                    if (isUnix()) {
                        sh 'echo $JAVA_HOME && java -version && mvn -v'
                    } else {
                        bat 'echo %JAVA_HOME% && java -version && mvn -v'
                    }
                }
            }
        }
    }

    post {
        success {
            echo "✅ Build succeeded. JAR is available in Jenkins artifacts."
        }
        failure {
            echo "❌ Build failed. Check the logs above."
        }
    }
}
