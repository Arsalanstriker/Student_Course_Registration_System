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
                echo "🐳 Building Docker Image..."
                powershell 'docker build -t scrs:1.0 .'
            }
        }

        stage('Docker Run') {
            steps {
                echo "🚀 Running Docker Container..."
                powershell '''
                docker stop scrs-app -ErrorAction SilentlyContinue
                docker rm scrs-app -ErrorAction SilentlyContinue
                docker run -d --name scrs-app -p 8080:8080 scrs:1.0
                '''
            }
        }
    }

    post {
        success {
            echo "✅ Dockerized CI/CD pipeline completed successfully!"
        }
        failure {
            echo "❌ Build/Deployment failed!"
        }
    }
}
