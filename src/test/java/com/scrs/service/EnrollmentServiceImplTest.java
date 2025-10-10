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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EnrollmentServiceImplTest {

    CourseRepository courseRepo;
    StudentRepository studentRepo;
    EnrollmentRepository enrollmentRepo;
    EnrollmentService service;

    @BeforeEach
    void setUp() {
        courseRepo = mock(CourseRepository.class);
        studentRepo = mock(StudentRepository.class);
        enrollmentRepo = mock(EnrollmentRepository.class);
        service = new EnrollmentServiceImpl(courseRepo, studentRepo, enrollmentRepo);
    }

    private Course course(String id, int max, int enrolled, int waitlistSize) {
        Course c = new Course();
        c.setCourseId(id);
        c.setTitle("T-" + id);
        c.setMaxSeats(max);
        c.setCurrentEnrolledCount(enrolled);
        c.setWaitlistSize(waitlistSize);
        c.setStartDate(LocalDate.now().plusDays(7));
        c.setEndDate(LocalDate.now().plusDays(60));
        c.setLatestEnrollmentBy(LocalDate.now().plusDays(3));
        return c;
    }

    private Student student(String id, int active, int wait) {
        Student s = new Student();
        s.setStudentId(id);
        s.setName("N-" + id);
        s.setEmail(id + "@mail.com");
        s.setActiveEnrollments(active);
        s.setWaitlistCount(wait);
        return s;
    }

    @Test
    @DisplayName("Enroll into course with available seat → ENROLLED")
    void enroll_withSeat_enrolled() {
        Course c = course("C1", 2, 1, 3);
        Student s = student("S1", 0, 0);

        when(courseRepo.findById("C1")).thenReturn(c);
        when(studentRepo.findById("S1")).thenReturn(s);
        when(enrollmentRepo.findById("S1", "C1")).thenReturn(null);
        when(enrollmentRepo.findByCourse("C1")).thenReturn(new ArrayList<>());

        Enrollment e = service.enroll("S1", "C1");

        assertEquals(EnrollmentStatus.ENROLLED, e.getStatus());
        assertEquals(2, c.getCurrentEnrolledCount());
        assertEquals(1, s.getActiveEnrollments());

        verify(enrollmentRepo).save(any(Enrollment.class));
        verify(courseRepo).save(c);
        verify(studentRepo).save(s);
    }

    @Test
    @DisplayName("Enroll when course is full but waitlist has space → WAITLISTED")
    void enroll_full_waitlist() {
        Course c = course("C2", 1, 1, 2);
        Student s = student("S2", 0, 0);

        when(courseRepo.findById("C2")).thenReturn(c);
        when(studentRepo.findById("S2")).thenReturn(s);
        when(enrollmentRepo.findById("S2","C2")).thenReturn(null);
        List<Enrollment> existing = new ArrayList<>();
        Enrollment other = new Enrollment("S9","C2", EnrollmentStatus.WAITLISTED);
        other.setWaitlistPosition(1);
        existing.add(other);
        when(enrollmentRepo.findByCourse("C2")).thenReturn(existing);

        Enrollment e = service.enroll("S2","C2");

        assertEquals(EnrollmentStatus.WAITLISTED, e.getStatus());
        assertEquals(2, e.getWaitlistPosition());
        assertEquals(1, s.getWaitlistCount());
        verify(enrollmentRepo).save(e);
        verify(courseRepo).save(c);
        verify(studentRepo).save(s);
    }

    @Test
    @DisplayName("Enroll when waitlist is full → throws WaitlistFullException")
    void enroll_waitlist_full() {
        Course c = course("C3", 1, 1, 1);
        Student s = student("S3", 0, 0);

        when(courseRepo.findById("C3")).thenReturn(c);
        when(studentRepo.findById("S3")).thenReturn(s);
        when(enrollmentRepo.findById("S3","C3")).thenReturn(null);

        Enrollment waitlisted = new Enrollment("S9","C3", EnrollmentStatus.WAITLISTED);
        waitlisted.setWaitlistPosition(1);
        when(enrollmentRepo.findByCourse("C3")).thenReturn(List.of(waitlisted));

        assertThrows(WaitlistFullException.class, () -> service.enroll("S3","C3"));
    }

    @Test
    @DisplayName("Duplicate enroll → throws DuplicateEnrollmentException")
    void duplicate_enroll() {
        Course c = course("C4", 3, 0, 2);
        Student s = student("S4", 0, 0);
        Enrollment existing = new Enrollment("S4","C4", EnrollmentStatus.ENROLLED);

        when(courseRepo.findById("C4")).thenReturn(c);
        when(studentRepo.findById("S4")).thenReturn(s);
        when(enrollmentRepo.findById("S4","C4")).thenReturn(existing);

        assertThrows(DuplicateEnrollmentException.class, () -> service.enroll("S4","C4"));
    }

    @Test
    @DisplayName("Cannot enroll after cutoff date → throws EnrollmentClosedException")
    void cutoff_enroll_closed() {
        Course c = course("C5", 3, 0, 2);
        c.setLatestEnrollmentBy(LocalDate.now().minusDays(1));
        Student s = student("S5", 0, 0);

        when(courseRepo.findById("C5")).thenReturn(c);
        when(studentRepo.findById("S5")).thenReturn(s);
        when(enrollmentRepo.findById("S5","C5")).thenReturn(null);

        assertThrows(EnrollmentClosedException.class, () -> service.enroll("S5","C5"));
    }

    @Test
    @DisplayName("Drop enrolled promotes next waitlisted student")
    void drop_promotes_waitlisted() {
        Course c = course("C6", 1, 1, 3);
        Student s1 = student("S6", 1, 0);
        Student s2 = student("S7", 0, 1);

        Enrollment enrolled = new Enrollment("S6","C6", EnrollmentStatus.ENROLLED);
        Enrollment w = new Enrollment("S7","C6", EnrollmentStatus.WAITLISTED);
        w.setWaitlistPosition(1);

        when(enrollmentRepo.findById("S6","C6")).thenReturn(enrolled);
        when(courseRepo.findById("C6")).thenReturn(c);
        when(studentRepo.findById("S6")).thenReturn(s1);
        when(enrollmentRepo.findByCourse("C6")).thenReturn(List.of(w));
        when(studentRepo.findById("S7")).thenReturn(s2);

        service.drop("S6","C6");

        assertEquals(0, s1.getActiveEnrollments());
        assertEquals(EnrollmentStatus.ENROLLED, w.getStatus());
        verify(enrollmentRepo).save(w);
        verify(enrollmentRepo).delete("S6","C6");
        verify(courseRepo, atLeastOnce()).save(c);
        verify(studentRepo, atLeastOnce()).save(any(Student.class));
    }

    @Test
    @DisplayName("Drop waitlisted decrements waitlist counters and removes entry")
    void drop_waitlisted() {
        Course c = course("C7", 1, 1, 3);
        Student s = student("S8", 0, 1);
        Enrollment w = new Enrollment("S8","C7", EnrollmentStatus.WAITLISTED);
        w.setWaitlistPosition(1);

        when(enrollmentRepo.findById("S8","C7")).thenReturn(w);
        when(courseRepo.findById("C7")).thenReturn(c);
        when(studentRepo.findById("S8")).thenReturn(s);

        service.drop("S8","C7");

        assertEquals(0, s.getWaitlistCount());
        verify(enrollmentRepo).delete("S8","C7");
        verify(courseRepo).save(c);
        verify(studentRepo).save(s);
    }

    @Test
    @DisplayName("Not found cases → throw specific exceptions")
    void not_found_cases() {
        when(courseRepo.findById("CX")).thenReturn(null);
        when(studentRepo.findById("SX")).thenReturn(null);
        assertThrows(CourseNotFoundException.class, () -> service.enroll("SX","CX"));

        Course c = course("C8", 1, 0, 1);
        when(courseRepo.findById("C8")).thenReturn(c);
        when(studentRepo.findById("SX")).thenReturn(null);
        assertThrows(StudentNotFoundException.class, () -> service.enroll("SX","C8"));
    }
}