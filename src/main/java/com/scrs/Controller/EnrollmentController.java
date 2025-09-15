package com.scrs.Controller;

import com.scrs.exception.EnrollmentException;
import com.scrs.service.EnrollmentService;

public class EnrollmentController {
    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    public void enroll(String studentId, String courseId) {
        try {
            var enrollment = enrollmentService.enroll(studentId, courseId);
            System.out.println("Enrollment status: " + enrollment.getStatus());
        } catch (EnrollmentException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public void drop(String studentId, String courseId) {
        try {
            enrollmentService.drop(studentId, courseId);
            System.out.println("Drop successful for " + studentId + " in " + courseId);
        } catch (EnrollmentException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
