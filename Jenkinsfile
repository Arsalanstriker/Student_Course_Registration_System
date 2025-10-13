pipeline {
    agent any

    tools {
        maven 'Maven'   // Must match name in Global Tool Configuration
        jdk 'JDK17'     // Must match name in Global Tool Configuration
    }

    environment {
        // Helps detect OS
        IS_WINDOWS = isUnix() ? 'false' : 'true'
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
                    echo "🔨 Cleaning and compiling project..."
                    if (isUnix()) {
                        sh 'mvn clean compile'
                    } else {
                        bat 'mvn clean compile'
                    }
                }
            }
        }

        stage('Run Tests') {
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
                    echo "📦 Packaging the JAR..."
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
                echo "💾 Archiving built JAR..."
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }
    }

    post {
        success {
            echo "✅ Build completed successfully! Download JAR from 'Build Artifacts'."
        }
        failure {
            echo "❌ Build failed! Check logs for errors."
        }
    }
}
