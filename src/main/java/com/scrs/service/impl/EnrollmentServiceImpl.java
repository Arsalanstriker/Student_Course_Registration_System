package com.scrs.service.impl;

import com.scrs.exception.CourseNotFoundException;
import com.scrs.exception.MaxEnrollmentLimitException;
import com.scrs.exception.StudentNotFoundException;
import com.scrs.exception.WaitlistFullException;
import com.scrs.model.Course;
import com.scrs.model.Enrollment;
import com.scrs.model.EnrollmentStatus;
import com.scrs.model.Student;
import com.scrs.repository.CourseRepository;
import com.scrs.repository.EnrollmentRepository;
import com.scrs.repository.StudentRepository;
import com.scrs.service.EnrollmentService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

public class EnrollmentServiceImpl implements EnrollmentService {
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_RESET = "\u001B[0m";

    private final CourseRepository courseRepo;
    private final StudentRepository studentRepo;
    private final EnrollmentRepository enrollmentRepo;

    public EnrollmentServiceImpl(CourseRepository courseRepo,
                                 StudentRepository studentRepo,
                                 EnrollmentRepository enrollmentRepo) {
        this.courseRepo = courseRepo;
        this.studentRepo = studentRepo;
        this.enrollmentRepo = enrollmentRepo;
    }

    @Override
    public Enrollment enroll(String studentId, String courseId) {
        Course course = courseRepo.findById(courseId);
        if (course == null) throw new CourseNotFoundException(courseId);

        Student student = studentRepo.findById(studentId);
        if (student == null) throw new StudentNotFoundException(studentId);

        //  Block if enrollment date has passed
        if (course.getLatestEnrollmentBy() != null &&
                LocalDate.now().isAfter(course.getLatestEnrollmentBy())) {
            throw new RuntimeException(ANSI_RED + "Enrollment Closed " + ANSI_RESET);
        }

        if (student.getActiveEnrollments() >= 5)
            throw new MaxEnrollmentLimitException(studentId);

        // Duplicate check: return existing record immediately
        Enrollment existing = enrollmentRepo.findById(studentId, courseId);
        if (existing != null) {
            return existing; // do not create a new enrollment
        }

        if (course.hasAvailableSeats()) {
            Enrollment e = new Enrollment(studentId, courseId, EnrollmentStatus.ENROLLED);
            e.setTimestamp(LocalDateTime.now());
            course.incrementEnrolled();
            student.incrementEnrollments();
            enrollmentRepo.save(e);
            courseRepo.save(course);
            studentRepo.save(student);
            return e;
        } else {
            if (student.getWaitlistCount() >= 3)
                throw new WaitlistFullException(courseId);

            Enrollment wl = new Enrollment(studentId, courseId, EnrollmentStatus.WAITLISTED);
            wl.setTimestamp(LocalDateTime.now());

            List<Enrollment> waitlisted = enrollmentRepo.findByCourse(courseId).stream()
                    .filter(e -> e.getStatus() == EnrollmentStatus.WAITLISTED)
                    .toList();

            wl.setWaitlistPosition(waitlisted.size() + 1);
            enrollmentRepo.save(wl);

            course.incrementWaitlist();
            student.incrementWaitlist();
            courseRepo.save(course);
            studentRepo.save(student);

            return wl;
        }
    }

    @Override
    public void drop(String studentId, String courseId) {
        Enrollment e = enrollmentRepo.findById(studentId, courseId);
        if (e == null) return;

        Course course = courseRepo.findById(courseId);
        Student student = studentRepo.findById(studentId);

        if (e.getStatus() == EnrollmentStatus.ENROLLED) {
            course.decrementEnrolled();
            student.decrementEnrollments();

            // Promote from waitlist if available
            List<Enrollment> waitlisted = enrollmentRepo.findByCourse(courseId).stream()
                    .filter(en -> en.getStatus() == EnrollmentStatus.WAITLISTED)
                    .sorted(Comparator.comparing(Enrollment::getTimestamp))
                    .toList();

            if (!waitlisted.isEmpty()) {
                Enrollment promote = waitlisted.get(0);
                promote.setStatus(EnrollmentStatus.ENROLLED);
                promote.setWaitlistPosition(0);

                course.decrementWaitlist();
                course.incrementEnrolled();

                Student nextStudent = studentRepo.findById(promote.getStudentId());
                nextStudent.decrementWaitlist();
                nextStudent.incrementEnrollments();

                enrollmentRepo.save(promote);
                studentRepo.save(nextStudent);
            }
        } else if (e.getStatus() == EnrollmentStatus.WAITLISTED) {
            course.decrementWaitlist();
            student.decrementWaitlist();
        }

        enrollmentRepo.delete(studentId, courseId);
        courseRepo.save(course);
        studentRepo.save(student);
    }
}
