package com.scrs.model;

public class Student {
    private String studentId;
    private String name;
    private String email;
    private int activeEnrollments ;
    private int waitlistCount ;

    // No-args constructor for Jackson/deserialization
    public Student() {}

    public Student(String studentId, String name, String email) {
        this.studentId = studentId;
        this.name = name;
        this.email = email;
    }

    // Getters / Setters
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public int getActiveEnrollments() { return activeEnrollments; }
    public void setActiveEnrollments(int activeEnrollments) { this.activeEnrollments = activeEnrollments; }

    public int getWaitlistCount() { return waitlistCount; }
    public void setWaitlistCount(int waitlistCount) { this.waitlistCount = waitlistCount; }

    // Helpers
    public void incrementEnrollments() { activeEnrollments++; }
    public void decrementEnrollments() { if (activeEnrollments > 0) activeEnrollments--; }

    public void incrementWaitlist() { waitlistCount++; }
    public void decrementWaitlist() { if (waitlistCount > 0) waitlistCount--; }

    @Override
    public String toString() {
        return "Student{" + studentId + ',' + name + ',' + email + '}';
    }
}
