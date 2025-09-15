package com.scrs.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Order {
    private String orderId;
    private String studentId;
    private List<String> courseIds;
    private Instant createdAt;

    public Order(String studentId, List<String> courseIds) {
        this.orderId = UUID.randomUUID().toString();
        this.studentId = studentId;
        this.courseIds = new ArrayList<>(courseIds);
        this.createdAt = Instant.now();
    }

    public String getOrderId() { return orderId; }
    public String getStudentId() { return studentId; }
    public List<String> getCourseIds() { return courseIds; }
    public Instant getCreatedAt() { return createdAt; }
}
