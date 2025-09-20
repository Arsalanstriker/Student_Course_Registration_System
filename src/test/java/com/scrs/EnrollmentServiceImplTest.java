package com.scrs;

import com.scrs.config.DynamoDbConfig;
import com.scrs.config.RepositoryFactory;
import com.scrs.exception.CourseNotFoundException;
import com.scrs.exception.StudentNotFoundException;
import com.scrs.model.Course;
import com.scrs.model.Enrollment;
import com.scrs.model.EnrollmentStatus;
import com.scrs.model.Student;
import com.scrs.repository.CourseRepository;
import com.scrs.repository.EnrollmentRepository;
import com.scrs.repository.StudentRepository;
import com.scrs.service.impl.EnrollmentServiceImpl;
import com.scrs.util.DbCleaner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EnrollmentServiceImplTest {

    private EnrollmentServiceImpl service;
    private CourseRepository courseRepo;
    private StudentRepository studentRepo;
    private EnrollmentRepository enrollmentRepo;

    @BeforeEach
    void setup() {
        // cleanup DynamoDB tables before each test
        DbCleaner.clearTable(DynamoDbConfig.(), "Courses", "courseId");
        DbCleaner.clearTable(DynamoDbConfig.(), "Students", "studentId");
        DbCleaner.clearTable(DynamoDbConfig.(), "Enrollments", "studentId", "courseId");

        courseRepo = RepositoryFactory.courseRepository();
        studentRepo = RepositoryFactory.studentRepository();
        enrollmentRepo = RepositoryFactory.enrollmentRepository();
        service = new EnrollmentServiceImpl(courseRepo, studentRepo, enrollmentRepo);

        // Seed fresh data
        courseRepo.save(new Course("C101", "Java Basics", "John Doe", 1));
        courseRepo.save(new Course("C102", "Python Basics", "Jane Doe", 2));

        studentRepo.save(new Student("S1", "Alice", "alice@mail.com"));
        studentRepo.save(new Student("S2", "Bob", "bob@mail.com"));
        studentRepo.save(new Student("S3", "Charlie", "charlie@mail.com"));
    }

    @Test
    void testEnrollStudentSuccess() {
        Enrollment e = service.enroll("S1", "C101");
        assertEquals(EnrollmentStatus.ENROLLED, e.getStatus());
        assertEquals(1, courseRepo.findById("C101").getCurrentEnrolledCount());
    }

    @Test
    void testEnrollStudentWaitlistedWhenFull() {
        service.enroll("S1", "C101"); // seat taken
        Enrollment e2 = service.enroll("S2", "C101");

        assertEquals(EnrollmentStatus.WAITLISTED, e2.getStatus());
        assertEquals(1, courseRepo.findById("C101").getWaitlistSize());
    }

    @Test
    void testDropEnrolledStudent() {
        service.enroll("S1", "C101");
        service.drop("S1", "C101");

        assertEquals(0, courseRepo.findById("C101").getCurrentEnrolledCount());
    }

    @Test
    void testDropWaitlistedStudent() {
        service.enroll("S1", "C101");
        service.enroll("S2", "C101"); // waitlisted
        service.drop("S2", "C101");

        assertEquals(0, courseRepo.findById("C101").getWaitlistSize());
    }

    @Test
    void testPromoteWaitlistedStudent() {
        service.enroll("S1", "C101");
        service.enroll("S2", "C101"); // waitlisted
        service.drop("S1", "C101");   // free seat

        Enrollment e2 = enrollmentRepo.findById("S2", "C101");
        assertEquals(EnrollmentStatus.ENROLLED, e2.getStatus());
    }

    @Test
    void testCourseNotFoundException() {
        assertThrows(CourseNotFoundException.class, () -> service.enroll("S1", "INVALID"));
    }

    @Test
    void testStudentNotFoundException() {
        assertThrows(StudentNotFoundException.class, () -> service.enroll("INVALID", "C101"));
    }

    @Test
    void testMaxEnrollmentLimitException() {
        Student s = studentRepo.findById("S1");
        for (int i = 0; i < 5; i++) s.incrementEnrollments();
        studentRepo.save(s);

        assertThrows(RuntimeException.class, () -> service.enroll("S1", "C102"));
    }
}
