package com.scrs.service;

import com.scrs.exception.*;
import com.scrs.model.Course;
import com.scrs.model.Enrollment;
import com.scrs.model.EnrollmentStatus;
import com.scrs.model.Student;
import com.scrs.repository.CourseRepository;
import com.scrs.repository.EnrollmentRepository;
import com.scrs.repository.StudentRepository;
import com.scrs.service.impl.EnrollmentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EnrollmentServiceEdgeCasesTest {

    CourseRepository courseRepo;
    StudentRepository studentRepo;
    EnrollmentRepository enrollmentRepo;
    EnrollmentService service;

    @BeforeEach
    void init() {
        courseRepo = mock(CourseRepository.class);
        studentRepo = mock(StudentRepository.class);
        enrollmentRepo = mock(EnrollmentRepository.class);
        service = new EnrollmentServiceImpl(courseRepo, studentRepo, enrollmentRepo);
    }

    private Course course(String id, int max, int enrolled, int waitlist) {
        Course c = new Course();
        c.setCourseId(id);
        c.setTitle("T-" + id);
        c.setMaxSeats(max);
        c.setCurrentEnrolledCount(enrolled);
        c.setWaitlistSize(waitlist);
        c.setStartDate(LocalDate.now().plusDays(1));
        c.setEndDate(LocalDate.now().plusDays(30));
        c.setLatestEnrollmentBy(LocalDate.now());
        return c;
    }

    private Student student(String id) {
        Student s = new Student();
        s.setStudentId(id);
        s.setName("N-" + id);
        s.setEmail(id + "@mail.com");
        return s;
    }

    @Test
    @DisplayName("MaxEnrollmentLimitException is thrown when student exceeds per-student limit (if implemented)")
    void student_limit() {
        Course c = course("C10", 10, 0, 10);
        Student s = student("S10");
        s.setActiveEnrollments(5); // max limit is 5

        when(courseRepo.findById("C10")).thenReturn(c);
        when(studentRepo.findById("S10")).thenReturn(s);
        when(enrollmentRepo.findById("S10","C10")).thenReturn(null);
        when(enrollmentRepo.findByCourse("C10")).thenReturn(new ArrayList<>());

        assertThrows(MaxEnrollmentLimitException.class, () -> service.enroll("S10","C10"));
    }

    @Test
    @DisplayName("Cannot enroll if course already started")
    void course_started() {
        Course started = course("C11", 10, 0, 5);
        started.setStartDate(LocalDate.now().minusDays(1));

        when(courseRepo.findById("C11")).thenReturn(started);
        when(studentRepo.findById("S11")).thenReturn(student("S11"));
        when(enrollmentRepo.findById("S11","C11")).thenReturn(null);

        assertThrows(EnrollmentClosedException.class, () -> service.enroll("S11","C11"));
    }
}