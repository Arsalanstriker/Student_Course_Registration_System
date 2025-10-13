pipeline {
    agent any

    environment {
        JAR_URL = 'https://github.com/Arsalanstriker/Student_Course_Registration_System/releases/download/v1.0/student-course-registration-1.0-SNAPSHOT-jar-with-dependencies.jar'
        JAR_NAME = 'student-course-registration.jar'
    }

    stages {
        stage('Download JAR') {
            steps {
                echo "⬇️ Downloading JAR file..."
                powershell '''
                if (Test-Path $env:JAR_NAME) { Remove-Item $env:JAR_NAME -Force }
                Invoke-WebRequest -Uri $env:JAR_URL -OutFile $env:JAR_NAME
                '''
            }
        }

        stage('Deploy JAR') {
            steps {
                echo "🚀 Deploying JAR..."
                powershell '''
                # Stop any old app instance
                $process = Get-Process java -ErrorAction SilentlyContinue
                if ($process) {
                    Write-Host "Stopping old Java app..."
                    Stop-Process -Name java -Force
                }

                # Start new instance
                Write-Host "Starting new Java app..."
                Start-Process "java" "-jar $env:JAR_NAME" -NoNewWindow
                '''
            }
        }
    }

    post {
        success {
            echo "✅ Application deployed successfully!"
        }
        failure {
            echo "❌ Deployment failed!"
        }
    }
}
