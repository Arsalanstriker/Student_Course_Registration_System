package com.scrs.controller;

import com.scrs.exception.EnrollmentException;
import com.scrs.model.Enrollment;
import com.scrs.service.EnrollmentService;

public class EnrollmentController {
    private final EnrollmentService service;

    public EnrollmentController(EnrollmentService service) {
        this.service = service;
    }

    public void enroll(String studentId, String courseId) {
        try {
            Enrollment e = service.enroll(studentId, courseId);
            System.out.println("Enrollment status: " + e.getStatus());
        } catch (EnrollmentException ex) {
            System.err.println("Error: " + ex.getMessage());
        }
    }

    public void drop(String studentId, String courseId) {
        try {
            service.drop(studentId, courseId);
            System.out.println("Drop successful for " + studentId + " in " + courseId);
        } catch (EnrollmentException ex) {
            System.err.println("Error: " + ex.getMessage());
        }
    }
}
