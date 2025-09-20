package com.scrs.model;

import java.time.Instant;

public class Enrollment {
    private String studentId;
    private String courseId;
    private EnrollmentStatus status;
    private int waitlistPosition = 0;
    private Instant createdAt = Instant.now();

    public Enrollment() {}

    public Enrollment(String studentId, String courseId, EnrollmentStatus status) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.status = status;
        this.createdAt = Instant.now();
    }

    // Getters / setters
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }

    public EnrollmentStatus getStatus() { return status; }
    public void setStatus(EnrollmentStatus status) { this.status = status; }

    public int getWaitlistPosition() { return waitlistPosition; }
    public void setWaitlistPosition(int waitlistPosition) { this.waitlistPosition = waitlistPosition; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
