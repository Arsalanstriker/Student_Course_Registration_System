pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                echo "📦 Cloning project from GitHub..."
                git branch: 'main', url: 'https://github.com/Arsalanstriker/Student_Course_Registration_System.git'
            }
        }

        stage('Build with Maven') {
            steps {
                echo "⚙️ Running Maven build..."
                bat 'mvn clean package -DskipTests'
            }
        }

        stage('Test') {
            steps {
                echo "🧪 Running unit tests..."
                bat 'mvn test'
            }
        }
    }

    post {
        success {
            echo "✅ Build Successful!"
            archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            echo "📥 You can now download the JAR file from Jenkins → Build Artifacts section."
        }
        failure {
            echo "❌ Build failed! Check logs above."
        }
    }
}
