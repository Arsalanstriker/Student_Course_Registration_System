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
    private final String tableName;

    public DynamoDbEnrollmentRepository(DynamoDbClient client, String tableName) {
        this.client = client;
        this.tableName = tableName;
    }

    @Override
    public void save(Enrollment enrollment) {
        client.putItem(PutItemRequest.builder()
                .tableName(tableName)
                .item(EnrollmentMapper.toItem(enrollment))
                .build());
    }

    @Override
    public Enrollment findById(String studentId, String courseId) {
        GetItemResponse response = client.getItem(GetItemRequest.builder()
                .tableName(tableName)
                .key(EnrollmentMapper.key(studentId, courseId))
                .build());

        if (!response.hasItem()) return null;
        return EnrollmentMapper.fromItem(response.item());
    }

    @Override
    public List<Enrollment> findByStudent(String studentId) {
        QueryRequest request = QueryRequest.builder()
                .tableName(tableName)
                .keyConditionExpression("studentId = :sid")
                .expressionAttributeValues(Map.of(":sid", AttributeValue.fromS(studentId)))
                .build();

        QueryResponse response = client.query(request);

        List<Enrollment> enrollments = new ArrayList<>();
        response.items().forEach(item -> enrollments.add(EnrollmentMapper.fromItem(item)));
        return enrollments;
    }

    @Override
    public List<Enrollment> findAll() {
        ScanResponse response = client.scan(ScanRequest.builder().tableName(tableName).build());
        List<Enrollment> enrollments = new ArrayList<>();
        response.items().forEach(item -> enrollments.add(EnrollmentMapper.fromItem(item)));
        return enrollments;
    }

    @Override
    public void delete(String studentId, String courseId) {
        client.deleteItem(DeleteItemRequest.builder()
                .tableName(tableName)
                .key(EnrollmentMapper.key(studentId, courseId))
                .build());
    }
}
