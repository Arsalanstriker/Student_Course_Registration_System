pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/Arsalanstriker/Student_Course_Registration_System.git'
            }
        }

        stage('Build') {
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
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }

        stage('Docker Build & Run') {
            steps {
                script {
                    sh 'docker build -t student-course-registration:1.0 .'
                    sh 'docker rm -f scrs-app || true'
                    sh 'docker run --name scrs-app -d student-course-registration:1.0'
                }
            }
        }
    post {
        success {
            echo "✅ Build & Deployment Successful"
        }
        failure {
            echo "❌ Build or Deployment Failed"
        }
    }
}
