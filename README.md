# 🎓 Student Course Registration System (CLI + DynamoDB + Jenkins CI/CD)

## 📌 Project Overview
This is a **Command-Line Interface (CLI) Student-Course Registration System** built in Java.  
It simulates a simple learning platform where students can log in, view available courses, enroll, and manage waitlists.  
All data (students, courses, enrollments) is stored in **AWS DynamoDB** (local or cloud).  

The project was developed in **10 days** as part of a structured roadmap covering:
- Linux basics
- Java (OOP, Collections, Debugging)
- DynamoDB integration
- JUnit testing
- Jenkins CI/CD pipeline

---

## 🚀 Features
- **Student Management**
  - Register & log in using **ID or name**
- **Course Management**
  - Courses seeded from JSON or DynamoDB
  - Admin can add new courses via JSON file
  - Each course shows:
    - Name
    - Tags (e.g., Java, Cloud)
    - Seats left
    - Enrollment deadline
- **Enrollment & Waitlist**
  - Students can enroll if seats are available
  - Waitlisting when full
  - Auto-promotion when a seat frees up
  - Max 5 enrollments / 3 waitlists per student
- **Persistence**
  - DynamoDB stores all student, course, and enrollment data
  - Data survives across multiple runs
- **Search**
  - Search courses by tags or keywords
- **Logging**
  - SLF4J + Logback for clean logs

---

## 🛠️ Tech Stack
- **Language**: Java 17  
- **Database**: AWS DynamoDB (Local / Cloud)  
- **Build Tool**: Maven  
- **Testing**: JUnit 5  
- **Logging**: SLF4J + Logback  
- **DevOps**: Jenkins CI/CD pipeline  

---

## 📂 Project Structure
src/
├── main/java/com/scrs
│ ├── app # CLI entry point
│ ├── config # RepositoryFactory, DynamoDB config
│ ├── controller # CLI controllers
│ ├── exception # Custom exceptions
│ ├── model # Student, Course, Enrollment
│ ├── repository # Interfaces
│ │ └── impl # DynamoDB implementations
│ ├── service # Interfaces
│ │ └── impl # Business logic
│ └── util # DataLoader, SessionManager, DbCleaner
├── main/resources # JSON seed data
└── test/java/com/scrs # JUnit tests



---

## ⚙️ Local Setup & Run

### 1. Start DynamoDB Local
Download from AWS and run:
```bash
java -Djava.library.path=./DynamoDBLocal_lib -jar DynamoDBLocal.jar -sharedDb


2. Build & Run Application
mvn clean package
java -jar target/student-course-reg-system-1.0-SNAPSHOT.jar

🤖 Jenkins CI/CD Pipeline
Pipeline Flow
GitHub → Jenkins → Build → Test → Package → Deploy → Linux VM

Stages

Checkout – Clone code from GitHub

Build – Compile Java code (mvn clean compile)

Test – Run unit tests (mvn test)

Package – Create executable .jar

Deploy – Copy .jar to VM & run with Java

Jenkinsfile
pipeline {
    agent any
    tools {
        maven 'Maven3'
        jdk 'JDK17'
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


👉 Builds are triggered manually in Jenkins (no GitHub webhook).

🖥️ Linux VM Deployment
1. Install Java
sudo apt update && sudo apt install openjdk-17-jdk -y

2. Deploy with Jenkins

Jenkins copies .jar → VM (scp)

Starts app with:

nohup java -jar student-course-reg-system-1.0-SNAPSHOT.jar &

3. Verify
ps -ef | grep java
tail -f nohup.out

✅ Testing

Run all tests:

mvn test


Unit Tests → Enrollment rules, login

Integration Tests → DynamoDB persistence, enrollments

📊 Final Report Summary

Goal Achieved: Built a CLI Student-Course Registration System with persistence + CI/CD.

Key Strengths:

Enrollment & waitlist logic

JSON course seeding

DynamoDB persistence

Jenkins pipeline automation

Demo Flow:

Student signs up / logs in

Student views & searches courses

Student enrolls or waitlisted

Dropping course promotes waitlist

Admin adds new courses via JSON

Jenkins builds & deploys app to VM

🎉 Conclusion

This project was completed in 10 days covering:

Linux fundamentals

Java OOP & collections

DynamoDB integration

CLI app development

JUnit testing

Jenkins CI/CD pipeline

Final Deliverable → A working CLI Student-Course Registration System deployed via Jenkins to a Linux VM.


---

✅ This is a **ready-to-use README**: you can paste it directly into your GitHub repo.  
It doubles as your **final report** for Day 10.  

👉 Do you also want me to prepare a **simple ASCII pipeline diagram** inside this README (GitHub → Jenkins → VM → Running App) so it looks more professional?
