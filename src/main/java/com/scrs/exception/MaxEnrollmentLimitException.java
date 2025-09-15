package com.scrs.exception;

public class MaxEnrollmentLimitException extends EnrollmentException {
    public MaxEnrollmentLimitException(String studentId) {
        super("Student " + studentId + " already has 5 active enrollments");
    }
}
