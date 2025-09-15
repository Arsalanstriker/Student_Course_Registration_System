package com.scrs.exception;

public class WaitlistFullException extends EnrollmentException {
    public WaitlistFullException(String courseId) {
        super("Waitlist is full for course: " + courseId);
    }
}
