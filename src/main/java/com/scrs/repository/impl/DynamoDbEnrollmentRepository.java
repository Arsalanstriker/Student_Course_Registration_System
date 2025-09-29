package com.scrs.repository.impl;

import com.scrs.model.Enrollment;
import com.scrs.repository.EnrollmentRepository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DynamoDbEnrollmentRepository implements EnrollmentRepository {

    private final DynamoDbClient client;
    private static final String TABLE = "Enrollments"; // 👉 Hardcoded table name

    public DynamoDbEnrollmentRepository(DynamoDbClient client) {
        this.client = client;
    }

    @Override
    public void save(Enrollment enrollment) {
        client.putItem(PutItemRequest.builder()
                .tableName(TABLE)
                .item(EnrollmentMapper.toItem(enrollment))
                .build());
    }

    @Override
    public Enrollment findById(String studentId, String courseId) {
        Map<String, AttributeValue> key = EnrollmentMapper.key(studentId, courseId);
        GetItemResponse response = client.getItem(GetItemRequest.builder()
                .tableName(TABLE)
                .key(key)
                .build());
        return response.hasItem() ? EnrollmentMapper.fromItem(response.item()) : null;
    }

    @Override
    public List<Enrollment> findByStudent(String studentId) {
        ScanRequest scan = ScanRequest.builder()
                .tableName(TABLE)
                .filterExpression("studentId = :sid")
                .expressionAttributeValues(Map.of(":sid", AttributeValue.fromS(studentId)))
                .build();
        ScanResponse response = client.scan(scan);
        List<Enrollment> list = new ArrayList<>();
        for (Map<String, AttributeValue> item : response.items()) {
            list.add(EnrollmentMapper.fromItem(item));
        }
        return list;
    }

    @Override
    public List<Enrollment> findByCourse(String courseId) {
        ScanRequest scan = ScanRequest.builder()
                .tableName(TABLE)
                .filterExpression("courseId = :cid")
                .expressionAttributeValues(Map.of(":cid", AttributeValue.fromS(courseId)))
                .build();
        ScanResponse response = client.scan(scan);
        List<Enrollment> list = new ArrayList<>();
        for (Map<String, AttributeValue> item : response.items()) {
            list.add(EnrollmentMapper.fromItem(item));
        }
        return list;
    }

    @Override
    public void delete(String studentId, String courseId) {
        client.deleteItem(DeleteItemRequest.builder()
                .tableName(TABLE)
                .key(EnrollmentMapper.key(studentId, courseId))
                .build());
    }
}
