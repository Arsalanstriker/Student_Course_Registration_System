pipeline {
    agent any

    tools {
        maven 'Maven'   // Make sure Maven is installed and named 'Maven' in Jenkins global tools
        jdk 'JDK17'     // Make sure JDK 17 is configured in Jenkins global tools
    }

    stages {
        stage('Checkout') {
            steps {
                echo "Cloning repository..."
                git branch: 'main', url: 'https://github.com/Arsalanstriker/Student_Course_Registration_System.git'
            }
        }

        stage('Build') {
            steps {
                echo " Cleaning and compiling project..."
                bat 'mvn clean compile'
            }
        }

        stage('Run Tests') {
            steps {
                echo " Running all unit and integration tests..."
                bat 'mvn test'
            }
        }

        stage('Package') {
            steps {
                echo " Building JAR package..."
                bat 'mvn clean package'
            }
        }

        stage('Archive Artifacts') {
            steps {
                echo "Archiving generated JAR file..."
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }
    }

    post {
        success {
            echo "✅ Build completed successfully! JAR is available in Jenkins artifacts."
        }
        failure {
            echo "❌ Build failed! Check the logs above for errors."
        }
    }
}
