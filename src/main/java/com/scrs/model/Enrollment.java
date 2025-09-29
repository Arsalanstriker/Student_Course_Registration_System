package com.scrs.model;

import java.time.LocalDateTime;

public class Enrollment {
    private String studentId;
    private String courseId;
    private EnrollmentStatus status;
    private int waitlistPosition;
    private LocalDateTime timestamp;

    public Enrollment() {}

    public Enrollment(String studentId, String courseId, EnrollmentStatus status) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.status = status;
        this.waitlistPosition = 0;
        this.timestamp = LocalDateTime.now();
    }

    // getters/setters
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }

    public EnrollmentStatus getStatus() { return status; }
    public void setStatus(EnrollmentStatus status) { this.status = status; }

    public int getWaitlistPosition() { return waitlistPosition; }
    public void setWaitlistPosition( int waitlistPosition) { this.waitlistPosition = waitlistPosition; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
