package com.scrs.exception;

public class CourseNotFoundException extends EnrollmentException {
    public CourseNotFoundException(String courseId) {
        super("Course not found: " + courseId);
    }
}
