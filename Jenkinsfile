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

        stage('Docker Build') {
            steps {
                powershell 'docker build -t student-course-registration:1.0 .'
            }
        }

        stage('Docker Compose Up') {
            steps {
                powershell '''
                try {
                    docker-compose ps
                    docker-compose down
                } catch {
                    Write-Output "No containers to stop"
                }
                docker-compose up -d --build
                '''
            }
        }
    }

    post {
        success {
            echo "✅ CI/CD & Deployment Successful (Dockerized)"
        }
        failure {
            echo "❌ Build/Deployment Failed"
        }
    }
}
