# 🎓 Student Course Registration System (SCRS)

I designed and implemented a **console-based Student Course Registration System** in Java.  
The system allows students to sign up, log in, view courses, enroll, drop, and manage waitlists — all backed by **DynamoDB Local**.

---

## 🚀 Features
- **Student Management**: 
- Signup, login with ID or name.
- **Course Management**:
- View courses, search by tags, enrollment deadlines.
- **Enrollment Rules**:
    - Max 5 active enrollments per student.
    - Waitlist up to 3 per student.
    - Deadline checks for course registration.
- **Admin Support**:
- Load/seed courses from JSON into DynamoDB.
-   - View all students, courses, enrollments

- **Persistence**: 
- DynamoDB Local (no data loss between runs).
- **CLI Interface**: 
- Clean menu-driven navigation.
# Main menu 
1) Sign up
2) Login
3) Exit

---

## 🛠️ Tech Stack
- **Java 17**
- **Maven**
- **DynamoDB Local** (Amazon NoSQL database)
- **JUnit 5** (testing)
- **Docker & Docker Compose**
- **Jenkins (CI/CD)**


---

## 🧪 Testing

I added **JUnit5 test cases**:
- `EnrollmentServiceImplTest` → validates service logic (duplicate check, waitlist, drop).
- `DynamoDbIntegrationTest` → validates integration with DynamoDB Local.

Run tests with:
```bash
mvn test

---

## 📦 Build & Run

### Local (without Docker)
```bash
mvn clean package
java -jar target/student-course-registration-1.0-SNAPSHOT.jar



##  📂 Project Structure

Student-Course-Registration-System/
├── src/
│   ├── main/java/com/scrs/
│   │   ├── app/StudentCourseRegistrationSystem.java   # Main CLI
│   │   ├── config/DynamoDbConfig.java
│   │   ├── model/ (Student, Course, Enrollment, Enums)
│   │   ├── repository/ (Interfaces)
│   │   ├── repository/impl/ (DynamoDb Repos + Mappers)
│   │   ├── service/ (Interfaces)
│   │   ├── service/impl/ (EnrollmentServiceImpl, etc)
│   │   └── exception/ (Custom exceptions)
│   └── test/java/com/scrs/
│       ├── integration/DynamoDbIntegrationTest.java
│       ├── service/EnrollmentServiceImplTest.java
│       └── ...
├── pom.xml
├── Dockerfile
├── docker-compose.yml
├── Jenkinsfile
├── README.md   # Project summary
└── docs/
    ├── report.docx   # Final Report 
    └── uml.puml      # PlantUML diagrams

#### 🔄 CI/CD Pipeline
GitHub → stores project source code.
I set up Jenkins Pipeline to:

1) Pull code from GitHub

2) Build & run tests

3) Package JAR

4) Build Docker image

5) Deploy with Docker Compose

6) Run health checks

7)  Auto teardown after pipeline

- Starts 
docker-compose up --build -d

run from CLI
git clone https://github.com/Arsalanstriker/Student_Course_Registration_System.git
cd Student_Course_Registration_System

mvn clean package

Run ith docker 
docker-compose up --build -d

---------------------------- ***************** -----------------------------------
