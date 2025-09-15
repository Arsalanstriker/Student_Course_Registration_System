package com.scrs.service;

import com.scrs.exception.*;
import com.scrs.model.Course;
import com.scrs.model.Enrollment;
import com.scrs.model.EnrollmentStatus;
import com.scrs.model.Student;
import com.scrs.repository.InMemoryDatabase;
import com.scrs.repository.impl.InMemoryCourseRepository;
import com.scrs.repository.impl.InMemoryEnrollmentRepository;
import com.scrs.repository.impl.InMemoryStudentRepository;
import com.scrs.service.impl.EnrollmentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EnrollmentServiceImplTest {

    private EnrollmentService service;
    private InMemoryCourseRepository courseRepo;
    private InMemoryStudentRepository studentRepo;
    private InMemoryEnrollmentRepository enrollmentRepo;

    @BeforeEach
    void setup() {
        InMemoryDatabase.getInstance().clearAll();

        courseRepo = new InMemoryCourseRepository();
        studentRepo = new InMemoryStudentRepository();
        enrollmentRepo = new InMemoryEnrollmentRepository();
        service = new EnrollmentServiceImpl(courseRepo, studentRepo, enrollmentRepo);

        studentRepo.save(new Student("S1", "Alice", "alice@mail.com"));
        studentRepo.save(new Student("S2", "Bob", "bob@mail.com"));
        courseRepo.save(new Course("C101", "Java", "John Doe", 1)); // only 1 seat
    }

    @Test
    void testEnrollStudentWaitlistedWhenFull() {
        service.enroll("S1", "C101"); // fills seat
        Enrollment enrollment = service.enroll("S2", "C101"); // waitlisted
        assertEquals(EnrollmentStatus.WAITLISTED, enrollment.getStatus());
        assertEquals(1, enrollment.getWaitlistPosition());
    }

    @Test
    void testEnrollStudentWhenSeatsAvailable() {
        Enrollment enrollment = service.enroll("S1", "C101");
        assertEquals(EnrollmentStatus.ENROLLED, enrollment.getStatus());
    }



    @Test
    void testDropStudentPromotesWaitlist() {
        service.enroll("S1", "C101"); // Alice enrolled
        service.enroll("S2", "C101"); // Bob waitlisted

        service.drop("S1", "C101"); // Alice drops, Bob promoted
        Enrollment updated = enrollmentRepo.findById("S2", "C101");
        assertEquals(EnrollmentStatus.ENROLLED, updated.getStatus());
    }
    @Test
    void testMaxEnrollmentLimitException() {
        Student s = studentRepo.findById("S1");
        // simulate student already enrolled in 5
        for (int i = 0; i < 5; i++) {
            s.incrementEnrollments();
        }
        studentRepo.save(s);

        assertThrows(MaxEnrollmentLimitException.class, () -> service.enroll("S1", "C101"));
    }

    @Test
    void testDuplicateEnrollmentNotAllowed() {
        service.enroll("S1", "C101");
        Enrollment again = service.enroll("S1", "C101");
        assertNotNull(again);
        assertEquals(EnrollmentStatus.ENROLLED, again.getStatus());
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
    void testWaitlistFullException() {
        Student s = studentRepo.findById("S2");
        // simulate student already has 3 waitlists
        for (int i = 0; i < 3; i++) {
            s.incrementWaitlist();
        }
        studentRepo.save(s);

        // seat is already taken
        service.enroll("S1", "C101");
        assertThrows(WaitlistFullException.class, () -> service.enroll("S2", "C101"));
    }
}
