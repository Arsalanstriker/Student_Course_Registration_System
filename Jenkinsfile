pipeline {
    agent any

    tools {
        maven 'Maven3'   // Jenkins Maven installation
        jdk 'JDK17'      // Jenkins JDK installation
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/your-username/student-course-reg-system.git'
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean compile'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Package') {
            steps {
                sh 'mvn package -DskipTests'
            }
            post {
                success {
                    archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
                }
            }
        }

       stage('Deploy Locally (Windows)') {
           steps {
               powershell '''
               $workspace = "${env.WORKSPACE}"
               $appDir = "C:\\app"
               $jarSource = Join-Path $workspace "target\\student-course-registration-1.0-SNAPSHOT.jar"
               $jarDest = Join-Path $appDir "student-course-registration-1.0-SNAPSHOT.jar"

               # Create app directory if it doesn't exist
               if (!(Test-Path $appDir)) {
                   New-Item -Path $appDir -ItemType Directory | Out-Null
               }

               # Copy JAR
               if (Test-Path $jarSource) {
                   Copy-Item -Path $jarSource -Destination $jarDest -Force
               } else {
                   Write-Error "JAR file not found at $jarSource"
                   exit 1
               }

               # Kill running Java processes with this JAR
               Get-Process java -ErrorAction SilentlyContinue | Where-Object {
                   $_.Path -like "*student-course-registration*"
               } | Stop-Process -Force

               # Start new process with log redirection
               Start-Process "java" -ArgumentList "-jar $jarDest" `
                   -RedirectStandardOutput "$appDir\\app.log" `
                   -RedirectStandardError "$appDir\\app-error.log"
               '''
           }
       }

        }
    }
}
