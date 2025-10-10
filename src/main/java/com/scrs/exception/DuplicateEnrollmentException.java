package com.scrs.exception;

public class DuplicateEnrollmentException extends EnrollmentException {
    public DuplicateEnrollmentException(String studentId, String courseId) {
        super("Student " + studentId + " is already enrolled or waitlisted in course " + courseId);
    }
}