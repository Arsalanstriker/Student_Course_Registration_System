package com.scrs.exception;

import java.time.LocalDate;

public class EnrollmentClosedException extends EnrollmentException {
    public EnrollmentClosedException(String courseId, LocalDate cutoff) {
        super("Enrollment is closed for course " + courseId +
                (cutoff != null ? " (cutoff: " + cutoff + ")" : ""));
    }
}