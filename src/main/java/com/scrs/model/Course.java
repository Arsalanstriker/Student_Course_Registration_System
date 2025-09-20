package com.scrs.model;

import java.time.LocalDate;
import java.util.Objects;

public class Course {
    private String courseId;
    private String title;
    private String instructor;
    private int maxSeats;
    private int currentEnrolledCount;
    private int waitlistSize;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate latestEnrollmentBy;
    private String level;
    private String tags;

    // No-args constructor for Jackson
    public Course() {}

    public Course(String courseId, String title, String instructor, int maxSeats) {
        this.courseId = courseId;
        this.title = title;
        this.instructor = instructor;
        this.maxSeats = maxSeats;
        this.currentEnrolledCount = 0;
        this.waitlistSize = 0;
    }

    // Seat availability logic
    public boolean hasAvailableSeats() {
        return currentEnrolledCount < maxSeats;
    }

    public int remainingSeats() {
        return maxSeats - currentEnrolledCount;
    }

    // Getters / Setters
    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getInstructor() { return instructor; }
    public void setInstructor(String instructor) { this.instructor = instructor; }

    public int getMaxSeats() { return maxSeats; }
    public void setMaxSeats(int maxSeats) { this.maxSeats = maxSeats; }

    public int getCurrentEnrolledCount() { return currentEnrolledCount; }
    public void setCurrentEnrolledCount(int currentEnrolledCount) { this.currentEnrolledCount = currentEnrolledCount; }

    public int getWaitlistSize() { return waitlistSize; }
    public void setWaitlistSize(int waitlistSize) { this.waitlistSize = waitlistSize; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public LocalDate getLatestEnrollmentBy() { return latestEnrollmentBy; }
    public void setLatestEnrollmentBy(LocalDate latestEnrollmentBy) { this.latestEnrollmentBy = latestEnrollmentBy; }

    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }

    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

    // Methods to update counts
    public void incrementEnrolled() { currentEnrolledCount++; }
    public void decrementEnrolled() { if (currentEnrolledCount > 0) currentEnrolledCount--; }
    public void incrementWaitlist() { waitlistSize++; }
    public void decrementWaitlist() { if (waitlistSize > 0) waitlistSize--; }

    @Override
    public String toString() {
        return "Course{" +
                "courseId='" + courseId + '\'' +
                ", title='" + title + '\'' +
                ", instructor='" + instructor + '\'' +
                ", maxSeats=" + maxSeats +
                ", enrolled=" + currentEnrolledCount +
                ", waitlist=" + waitlistSize +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Course)) return false;
        Course course = (Course) o;
        return Objects.equals(courseId, course.courseId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseId);
    }
}
