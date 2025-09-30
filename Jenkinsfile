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

        stage('Deploy Locally') {
            steps {
                powershell '''
                $appDir = "C:\\app"
                if (!(Test-Path $appDir)) { New-Item -Path $appDir -ItemType Directory }

                Copy-Item -Path target\\student-course-registration-1.0-SNAPSHOT.jar -Destination $appDir -Force

                # Kill any existing process
                Get-Process java -ErrorAction SilentlyContinue | Where-Object { $_.Path -like "*student-course-registration-1.0-SNAPSHOT.jar*" } | Stop-Process -Force

                # Start new one
                Start-Process "java" -ArgumentList "-jar $appDir\\student-course-registration-1.0-SNAPSHOT.jar" -RedirectStandardOutput "$appDir\\app.log" -RedirectStandardError "$appDir\\app-error.log"
                '''
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
