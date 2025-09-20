package com.scrs.service.impl;

import com.scrs.exception.*;
import com.scrs.model.*;
import com.scrs.repository.CourseRepository;
import com.scrs.repository.EnrollmentRepository;
import com.scrs.repository.StudentRepository;
import com.scrs.service.EnrollmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class EnrollmentServiceImpl implements EnrollmentService {
    private static final Logger logger = LoggerFactory.getLogger(EnrollmentServiceImpl.class);

    private final CourseRepository courseRepo;
    private final StudentRepository studentRepo;
    private final EnrollmentRepository enrollmentRepo;

    // Waitlists per course (in-memory queue)
    private final Map<String, Queue<String>> waitlists = new HashMap<>();

    public EnrollmentServiceImpl(CourseRepository courseRepo,
                                 StudentRepository studentRepo,
                                 EnrollmentRepository enrollmentRepo) {
        this.courseRepo = courseRepo;
        this.studentRepo = studentRepo;
        this.enrollmentRepo = enrollmentRepo;
    }

    @Override
    public Enrollment enroll(String studentId, String courseId){
        logger.info("Attempting to enroll student {} into course {}", studentId, courseId);

        Course course = courseRepo.findById(courseId);
        Student student = studentRepo.findById(studentId);

        if (course == null) {
            logger.error("Course {} not found", courseId);
            throw new CourseNotFoundException(courseId);
        }
        if (student == null) {
            logger.error("Student {} not found", studentId);
            throw new StudentNotFoundException(studentId);
        }

        // Check student's max active enrollments (5)
        if (student.getActiveEnrollments() >= 5) {
            logger.error("Student {} already has max active enrollments", studentId);
            throw new MaxEnrollmentLimitException(studentId);
        }

        // Prevent duplicate enrollment or waitlist for same course
        Enrollment existing = enrollmentRepo.findById(studentId, courseId);
        if (existing != null) {
            logger.warn("Student {} is already enrolled/waitlisted in {}", studentId, courseId);
            return existing;
        }

        Enrollment enrollment;

        // If course has no available seats -> waitlist
        if (!course.hasAvailableSeats()) {
            if (student.getWaitlistCount() >= 3) {
                logger.error("Student {} already has 3 waitlisted courses", studentId);
                throw new WaitlistFullException(courseId);
            }

            enrollment = new Enrollment(studentId, courseId, EnrollmentStatus.WAITLISTED);
            Queue<String> waitlist = waitlists.computeIfAbsent(courseId, k -> new LinkedList<>());
            waitlist.add(studentId);

            enrollment.setWaitlistPosition(waitlist.size());
            course.incrementWaitlist();
            student.incrementWaitlist();

            logger.info("Student {} WAITLISTED for course {} at position {}",
                    studentId, courseId, enrollment.getWaitlistPosition());
        } else {
            // Seats available -> enroll
            enrollment = new Enrollment(studentId, courseId, EnrollmentStatus.ENROLLED);
            course.incrementEnrolled();
            student.incrementEnrollments();

            logger.info("Student {} successfully ENROLLED in {}", studentId, courseId);
        }

        enrollmentRepo.save(enrollment);
        courseRepo.save(course);
        studentRepo.save(student);
        return enrollment;
    }

    @Override
    public void drop(String studentId, String courseId) {
        logger.info("Student {} attempting to drop course {}", studentId, courseId);

        Course course = courseRepo.findById(courseId);
        Student student = studentRepo.findById(studentId);
        Enrollment enrollment = enrollmentRepo.findById(studentId, courseId);

        if (course == null) {
            throw new CourseNotFoundException(courseId);
        }
        if (student == null) {
            throw new StudentNotFoundException(studentId);
        }
        if (enrollment == null) {
            logger.warn("No enrollment found for student {} in {}", studentId, courseId);
            return;
        }

        if (enrollment.getStatus() == EnrollmentStatus.ENROLLED) {
            course.decrementEnrolled();
            student.decrementEnrollments();
            logger.info("Student {} DROPPED from {}", studentId, courseId);

            // Promote next in waitlist
            Queue<String> waitlist = waitlists.get(courseId);
            if (waitlist != null && !waitlist.isEmpty()) {
                String nextStudentId = waitlist.poll();
                Enrollment next = enrollmentRepo.findById(nextStudentId, courseId);
                Student nextStudent = studentRepo.findById(nextStudentId);

                if (next != null && next.getStatus() == EnrollmentStatus.WAITLISTED) {
                    if (nextStudent.getActiveEnrollments() < 5) {
                        next.setStatus(EnrollmentStatus.ENROLLED);
                        next.setWaitlistPosition(0);
                        nextStudent.decrementWaitlist();
                        nextStudent.incrementEnrollments();

                        enrollmentRepo.save(next);
                        studentRepo.save(nextStudent);

                        course.incrementEnrolled();
                        course.decrementWaitlist();

                        logger.info("Student {} PROMOTED from waitlist to ENROLLED in {}",
                                nextStudentId, courseId);
                    } else {
                        // re-add to queue (keeps FIFO but puts them at back)
                        waitlist.add(nextStudentId);
                        logger.warn("Student {} at max limit, kept in waitlist for {}",
                                nextStudentId, courseId);
                    }
                }
            }
        } else if (enrollment.getStatus() == EnrollmentStatus.WAITLISTED) {
            course.decrementWaitlist();
            student.decrementWaitlist();
            Queue<String> waitlist = waitlists.get(courseId);
            if (waitlist != null) waitlist.remove(studentId);
            logger.info("Student {} removed from WAITLIST for {}", studentId, courseId);
        }

        enrollmentRepo.delete(studentId, courseId);
        courseRepo.save(course);
        studentRepo.save(student);
    }
}
