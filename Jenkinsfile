pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/Arsalanstriker/Student_Course_Registration_System.git'
            }
        }

        stage('Build with Maven') {
            steps {
                bat 'mvn clean package -DskipTests'
            }
        }

        stage('Test') {
            steps {
                bat 'mvn test'
            }
        }

        stage('Archive JAR') {
            steps {
                archiveArtifacts artifacts: 'target/student-course-registration-1.0-SNAPSHOT.jar', fingerprint: true
                echo "✅ JAR file archived successfully."
            }
        }
    }

    post {
        success {
            echo "✅ Build & Test completed successfully!"
        }
        failure {
            echo "❌ Build failed. Please check console logs."
        }
    }
}
