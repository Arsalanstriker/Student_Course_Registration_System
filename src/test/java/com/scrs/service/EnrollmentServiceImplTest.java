package com.scrs.service;

import com.scrs.config.RepositoryFactory;
import com.scrs.model.Course;
import com.scrs.model.Student;
import com.scrs.model.EnrollmentStatus;
import com.scrs.service.impl.EnrollmentServiceImpl;
import com.scrs.util.DbCleaner;
import com.scrs.util.TableInitializer;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class EnrollmentServiceImplTest {
    private EnrollmentServiceImpl service;

    @BeforeEach
    void init() {
        TableInitializer.main(new String[]{});
        DbCleaner.clearTable("Students", "studentId", null);
        DbCleaner.clearTable("Courses", "courseId", null);
        DbCleaner.clearTable("Enrollments", "studentId", "courseId");

        var studentRepo = RepositoryFactory.studentRepository();
        var courseRepo = RepositoryFactory.courseRepository();
        var enrollmentRepo = RepositoryFactory.enrollmentRepository();

        studentRepo.save(new Student("S1", "Alice", "a@mail.com"));
        studentRepo.save(new Student("S2", "Bob", "b@mail.com"));

        courseRepo.save(new Course("C1", "Java Basics", 1));

        service = new EnrollmentServiceImpl(courseRepo, studentRepo, enrollmentRepo);
    }

    @Test
    void testWaitlistWhenFull() {
        var e1 = service.enroll("S1", "C1");
        assertEquals(EnrollmentStatus.ENROLLED, e1.getStatus());

        var e2 = service.enroll("S2", "C1");
        assertEquals(EnrollmentStatus.WAITLISTED, e2.getStatus());
    }

    @Test
    void testDuplicateEnrollment() {
        var e1 = service.enroll("S1", "C1");
        var e2 = service.enroll("S1", "C1");
        assertSame(e1.getStatus(), e2.getStatus(), "Duplicate should not create new record");
    }
}
