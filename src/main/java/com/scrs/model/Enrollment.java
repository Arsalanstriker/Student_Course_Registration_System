package com.scrs.model;

import java.time.Instant;

public class Enrollment {
    private String studentId;
    private String courseId;
    private EnrollmentStatus status;
    private int waitlistPosition;
    private Instant createdAt;

    public Enrollment(String studentId, String courseId, EnrollmentStatus status) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.status = status;
        this.createdAt = Instant.now();
    }

    // Getters & Setters
    public String getStudentId() { return studentId; }
    public String getCourseId() { return courseId; }
    public EnrollmentStatus getStatus() { return status; }
    public void setStatus(EnrollmentStatus status) { this.status = status; }
    public int getWaitlistPosition() { return waitlistPosition; }
    public void setWaitlistPosition(int pos) { this.waitlistPosition = pos; }
}
