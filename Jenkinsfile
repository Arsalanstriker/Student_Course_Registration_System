pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                echo " Checking out source code..."
                git branch: 'main', url: 'https://github.com/Arsalanstriker/Student_Course_Registration_System.git'
            }
        }

        stage('Build JAR') {
            steps {
                echo " Building JAR with dependencies..."
                bat 'mvn clean package -DskipTests'
            }
        }

        stage('Docker Build & Deploy') {
            steps {
                echo " Building and deploying Docker containers..."
                powershell '''
                docker-compose down || Write-Host "No containers running"
                docker-compose build --no-cache
                docker-compose up -d
                '''
            }
        }
    }

    post {
        success {
            echo " Build & Deployment successful!"
        }
        failure {
            echo " Build/Deployment failed!"
        }
    }
}
