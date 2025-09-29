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

        stage('Deploy') {
            steps {
                sh '''
                echo "Deploying to Linux VM..."
                scp target/student-course-reg-system-1.0-SNAPSHOT.jar user@your-vm-ip:/home/user/
                ssh user@your-vm-ip "nohup java -jar /home/user/student-course-reg-system-1.0-SNAPSHOT.jar &"
                '''
            }
        }
    }
}
