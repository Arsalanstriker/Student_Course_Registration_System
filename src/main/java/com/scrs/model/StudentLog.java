package com.scrs.model;

import java.time.Instant;
import java.util.UUID;

public class StudentLog {
    private String logId;
    private String studentId;
    private String action;
    private String courseId;
    private Instant timestamp;

    public StudentLog(String studentId, String action, String courseId) {
        this.logId = UUID.randomUUID().toString();
        this.studentId = studentId;
        this.action = action;
        this.courseId = courseId;
        this.timestamp = Instant.now();
    }

    public String getLogId() { return logId; }
    public String getStudentId() { return studentId; }
    public String getAction() { return action; }
    public String getCourseId() { return courseId; }
    public Instant getTimestamp() { return timestamp; }
}
