package com.scrs.integration;

import com.scrs.config.RepositoryFactory;
import com.scrs.model.Course;
import com.scrs.model.Enrollment;
import com.scrs.model.EnrollmentStatus;
import com.scrs.model.Student;
import com.scrs.service.EnrollmentService;
import com.scrs.service.impl.EnrollmentServiceImpl;
import com.scrs.util.DbCleaner;
import com.scrs.util.TableInitializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DynamoDbIntegrationTest {
    private EnrollmentService service;

    @BeforeEach
    void setup() {
        // Ensure tables exist
        TableInitializer.main(new String[]{});

        // Clean tables before each test
        DbCleaner.clearTable("Students", "studentId", null);
        DbCleaner.clearTable("Courses", "courseId", null);
        DbCleaner.clearTable("Enrollments", "studentId", "courseId");

        // Seed fresh data
        var studentRepo = RepositoryFactory.studentRepository();
        var courseRepo = RepositoryFactory.courseRepository();

        studentRepo.save(new Student("S1", "Alice", "a@mail.com"));
        studentRepo.save(new Student("S2", "Bob", "b@mail.com"));
        studentRepo.save(new Student("S3", "Charlie", "c@mail.com"));

        // Only 1 seat to test waitlist & promotion
        courseRepo.save(new Course("C101", "Java Fundamentals", 1));

        service = new EnrollmentServiceImpl(
                RepositoryFactory.courseRepository(),
                RepositoryFactory.studentRepository(),
                RepositoryFactory.enrollmentRepository()
        );
    }

    @Test
    void testFullEnrollmentFlow() {
        // First student -> Enrolled
        Enrollment e1 = service.enroll("S1", "C101");
        assertEquals(EnrollmentStatus.ENROLLED, e1.getStatus());

        // Second student -> Waitlisted
        Enrollment e2 = service.enroll("S2", "C101");
        assertEquals(EnrollmentStatus.WAITLISTED, e2.getStatus());

        // Drop first student -> promotion should happen
        service.drop("S1", "C101");
        Enrollment promoted = RepositoryFactory.enrollmentRepository().findById("S2", "C101");
        assertNotNull(promoted);
        assertEquals(EnrollmentStatus.ENROLLED, promoted.getStatus());

        // Third student tries -> should go to waitlist
        Enrollment e3 = service.enroll("S3", "C101");
        assertEquals(EnrollmentStatus.WAITLISTED, e3.getStatus());
    }

    @Test
    void testDuplicateEnrollmentIsBlocked() {
        Enrollment first = service.enroll("S1", "C101");
        Enrollment second = service.enroll("S1", "C101");
        assertNotNull(second);
        assertEquals(first.getStatus(), second.getStatus(), "Duplicate should return same status, not new enrollment");
    }

    @Test
    void testMultipleWaitlistPromotionOrder() {
        // Fill seat
        service.enroll("S1", "C101");

        // Add two to waitlist
        Enrollment e2 = service.enroll("S2", "C101");
        Enrollment e3 = service.enroll("S3", "C101");
        assertEquals(EnrollmentStatus.WAITLISTED, e2.getStatus());
        assertEquals(EnrollmentStatus.WAITLISTED, e3.getStatus());

        // Drop S1 -> only S2 (first in queue) promoted
        service.drop("S1", "C101");

        Enrollment afterDropS2 = RepositoryFactory.enrollmentRepository().findById("S2", "C101");
        Enrollment afterDropS3 = RepositoryFactory.enrollmentRepository().findById("S3", "C101");

        assertEquals(EnrollmentStatus.ENROLLED, afterDropS2.getStatus());
        assertEquals(EnrollmentStatus.WAITLISTED, afterDropS3.getStatus());
    }
}
