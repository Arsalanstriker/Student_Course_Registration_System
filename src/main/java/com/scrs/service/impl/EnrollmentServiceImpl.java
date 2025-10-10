package com.scrs.service.impl;

import com.scrs.exception.CourseNotFoundException;
import com.scrs.exception.DuplicateEnrollmentException;
import com.scrs.exception.EnrollmentClosedException;
import com.scrs.exception.EnrollmentException;
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
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

 //Enrollment service enroll, drop,  waitlist promotion; default waitlist cap=3
public class EnrollmentServiceImpl implements EnrollmentService {


    private static final int DEFAULT_MAX_ACTIVE_ENROLLMENTS = 5;
    private static final int DEFAULT_WAITLIST_CAPACITY = 3;
//Repos interacts with workbench
    private final CourseRepository courseRepo;
    private final StudentRepository studentRepo;
    private final EnrollmentRepository enrollmentRepo;

    public EnrollmentServiceImpl(CourseRepository courseRepo,
                                 StudentRepository studentRepo,
                                 EnrollmentRepository enrollmentRepo) {
        this.courseRepo = Objects.requireNonNull(courseRepo, "courseRepo");
        this.studentRepo = Objects.requireNonNull(studentRepo, "studentRepo");
        this.enrollmentRepo = Objects.requireNonNull(enrollmentRepo, "enrollmentRepo");
    }

    // Enrolls or waitlists enforces cutoffs/limits/duplicates
    @Override
    public Enrollment enroll(String studentId, String courseId) {
        if (isBlank(studentId) || isBlank(courseId)) {
            throw new EnrollmentException("studentId and courseId must be non-null/non-blank");
        }

        Course course = courseRepo.findById(courseId);
        if (course == null) throw new CourseNotFoundException(courseId);

        Student student = studentRepo.findById(studentId);
        if (student == null) throw new StudentNotFoundException(studentId);

        Enrollment existing = enrollmentRepo.findById(studentId, courseId);
        if (existing != null) throw new DuplicateEnrollmentException(studentId, courseId);

        LocalDate today = LocalDate.now();
        if (course.getLatestEnrollmentBy() != null && today.isAfter(course.getLatestEnrollmentBy())) {
            throw new EnrollmentClosedException(courseId, course.getLatestEnrollmentBy());
        }
        if (course.getStartDate() != null && !today.isBefore(course.getStartDate())) {
            throw new EnrollmentClosedException(courseId, course.getLatestEnrollmentBy());
        }

        if (safeInt(student.getActiveEnrollments()) >= DEFAULT_MAX_ACTIVE_ENROLLMENTS) {
            throw new MaxEnrollmentLimitException(studentId);
        }

        int enrolled = safeInt(course.getCurrentEnrolledCount());
        int maxSeats = safeInt(course.getMaxSeats());

        if (enrolled < maxSeats) {
            Enrollment e = new Enrollment(studentId, courseId, EnrollmentStatus.ENROLLED);
            course.setCurrentEnrolledCount(enrolled + 1);
            student.setActiveEnrollments(safeInt(student.getActiveEnrollments()) + 1);

            enrollmentRepo.save(e);
            courseRepo.save(updateDerivedWaitlistCount(course)); // keep derived counts fresh
            studentRepo.save(student);
            return e;
        }

        List<Enrollment> allForCourse = enrollmentRepo.findByCourse(courseId);
        int currentWaitlisted = (int) allForCourse.stream()
                .filter(en -> en.getStatus() == EnrollmentStatus.WAITLISTED)
                .count();

        int capacity = effectiveWaitlistCapacity(course);
        if (currentWaitlisted >= capacity) {
            throw new WaitlistFullException(courseId);
        }

        Enrollment e = new Enrollment(studentId, courseId, EnrollmentStatus.WAITLISTED);
        e.setWaitlistPosition(currentWaitlisted + 1);
        student.setWaitlistCount(safeInt(student.getWaitlistCount()) + 1);

        enrollmentRepo.save(e);
        studentRepo.save(student);
        courseRepo.save(updateDerivedWaitlistCount(course));
        return e;
    }

    //Dropping an enrollment and promoting the first waitlisted student
    @Override
    public void drop(String studentId, String courseId) {
        if (isBlank(studentId) || isBlank(courseId)) {
            throw new EnrollmentException("studentId and courseId must be non-null/non-blank");
        }

        Enrollment target = enrollmentRepo.findById(studentId, courseId);
        if (target == null) {
            throw new EnrollmentException("No enrollment found for student " + studentId + " in course " + courseId);
        }

        Course course = courseRepo.findById(courseId);
        if (course == null) throw new CourseNotFoundException(courseId);

        Student actor = studentRepo.findById(studentId);
        if (actor == null) throw new StudentNotFoundException(studentId);

        if (target.getStatus() == EnrollmentStatus.ENROLLED) {
            course.setCurrentEnrolledCount(Math.max(0, safeInt(course.getCurrentEnrolledCount()) - 1));
            actor.setActiveEnrollments(Math.max(0, safeInt(actor.getActiveEnrollments()) - 1));
            enrollmentRepo.delete(studentId, courseId);

            List<Enrollment> waitlisted = waitlistForCourseOrdered(courseId);

            if (!waitlisted.isEmpty()) {
                Enrollment promote = waitlisted.get(0);
                Student promotedStudent = studentRepo.findById(promote.getStudentId());
                if (promotedStudent != null) {
                    promote.setStatus(EnrollmentStatus.ENROLLED);
                    promote.setWaitlistPosition(0);
                    course.setCurrentEnrolledCount(safeInt(course.getCurrentEnrolledCount()) + 1);
                    promotedStudent.setWaitlistCount(Math.max(0, safeInt(promotedStudent.getWaitlistCount()) - 1));
                    promotedStudent.setActiveEnrollments(safeInt(promotedStudent.getActiveEnrollments()) + 1);

                    enrollmentRepo.save(promote);
                    studentRepo.save(promotedStudent);
                }
                for (int i = 1; i < waitlisted.size(); i++) {
                    Enrollment w = waitlisted.get(i);
                    int newPos = Math.max(1, safeInt(w.getWaitlistPosition()) - 1);
                    if (newPos != safeInt(w.getWaitlistPosition())) {
                        w.setWaitlistPosition(newPos);
                        enrollmentRepo.save(w);
                    }
                }
            }

            courseRepo.save(updateDerivedWaitlistCount(course));
            studentRepo.save(actor);
            return;
        }

        if (target.getStatus() == EnrollmentStatus.WAITLISTED) {
            int droppedPos = safeInt(target.getWaitlistPosition());
            actor.setWaitlistCount(Math.max(0, safeInt(actor.getWaitlistCount()) - 1));
            enrollmentRepo.delete(studentId, courseId);

            List<Enrollment> remaining = waitlistForCourseOrdered(courseId);
            for (Enrollment w : remaining) {
                if (safeInt(w.getWaitlistPosition()) > droppedPos) {
                    w.setWaitlistPosition(safeInt(w.getWaitlistPosition()) - 1);
                    enrollmentRepo.save(w);
                }
            }

            courseRepo.save(updateDerivedWaitlistCount(course));
            studentRepo.save(actor);
            return;
        }

        throw new EnrollmentException("Unsupported enrollment status for drop: " + target.getStatus());
    }

    //sorting waitlist pistions
    private List<Enrollment> waitlistForCourseOrdered(String courseId) {
        return enrollmentRepo.findByCourse(courseId).stream()
                .filter(e -> e.getStatus() == EnrollmentStatus.WAITLISTED)
                .sorted(Comparator.comparingInt(e -> {
                    Integer p = e.getWaitlistPosition();
                    return p == null || p < 1 ? Integer.MAX_VALUE : p;
                }))
                .collect(Collectors.toList());
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private static int safeInt(Integer v) {
        return v == null ? 0 : v;
    }

    //waitlist capacity
    private static int effectiveWaitlistCapacity(Course c) {
        Integer sz = c.getWaitlistSize();
        return (sz == null || sz <= 0) ? DEFAULT_WAITLIST_CAPACITY : sz;
    }
    //updating waitlist count
    private Course updateDerivedWaitlistCount(Course course) {
        int currentWaitlisted = (int) enrollmentRepo.findByCourse(course.getCourseId()).stream()
                .filter(e -> e.getStatus() == EnrollmentStatus.WAITLISTED)
                .count();
        try {
            Course.class.getMethod("setCurrentWaitlistedCount", Integer.class)
                    .invoke(course, Integer.valueOf(currentWaitlisted));
        } catch (ReflectiveOperationException ignored) {
        }
        return course;
    }
}