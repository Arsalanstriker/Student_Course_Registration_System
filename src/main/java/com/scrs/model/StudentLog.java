package com.scrs.model;

import java.time.Instant;

public class StudentLog {
    private String logId;
    private String studentId;
    private String action;
    private String courseId;
    private Instant timestamp;

    public StudentLog() {}

    public StudentLog(String logId, String studentId, String action, String courseId) {
        this.logId = logId;
        this.studentId = studentId;
        this.action = action;
        this.courseId = courseId;
        this.timestamp = Instant.now();
    }

    // getters/setters
    public String getLogId() { return logId; }
    public void setLogId(String logId) { this.logId = logId; }
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }
    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
}
