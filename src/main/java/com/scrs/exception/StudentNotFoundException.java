package com.scrs.exception;

public class StudentNotFoundException extends EnrollmentException {
    public StudentNotFoundException(String studentId) {
        super("Student not found: " + studentId);
    }
}
