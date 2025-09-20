package com.scrs.model;

import java.time.Instant;
import java.util.List;

public class Order {
    private String orderId;
    private String studentId;
    private List<String> courseIds;
    private Instant createdAt;
    // totals etc. omitted for simplicity

    public Order() {}

    public Order(String orderId, String studentId, List<String> courseIds) {
        this.orderId = orderId;
        this.studentId = studentId;
        this.courseIds = courseIds;
        this.createdAt = Instant.now();
    }

    // getters / setters
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public List<String> getCourseIds() { return courseIds; }
    public void setCourseIds(List<String> courseIds) { this.courseIds = courseIds; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
