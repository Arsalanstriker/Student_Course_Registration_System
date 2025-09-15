package com.scrs.model;

public class Student {
    private String studentId;
    private String name;
    private String email;
    private String passwordHash;
    private int activeEnrollments;
    private int waitlistCount;

    public Student(String studentId, String name, String email) {
        this.studentId = studentId;
        this.name = name;
        this.email = email;
        this.activeEnrollments = 0;
        this.waitlistCount = 0;
    }

    // Getters & Setters
    public String getStudentId() { return studentId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public int getActiveEnrollments() { return activeEnrollments; }
    public int getWaitlistCount() { return waitlistCount; }

    public void incrementEnrollments() { activeEnrollments++; }
    public void decrementEnrollments() { if (activeEnrollments > 0) activeEnrollments--; }
    public void incrementWaitlist() { waitlistCount++; }
    public void decrementWaitlist() { if (waitlistCount > 0) waitlistCount--; }
}
