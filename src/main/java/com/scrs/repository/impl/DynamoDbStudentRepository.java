package com.scrs.repository.impl;

import com.scrs.model.Student;
import com.scrs.repository.StudentRepository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.ArrayList;
import java.util.List;

public class DynamoDbStudentRepository implements StudentRepository {
    private final DynamoDbClient client;
    private final String tableName;

    public DynamoDbStudentRepository(DynamoDbClient client, String tableName) {
        this.client = client;
        this.tableName = tableName;
    }

    @Override
    public void save(Student student) {
        client.putItem(PutItemRequest.builder()
                .tableName(tableName)
                .item(StudentMapper.toItem(student))
                .build());
    }

    @Override
    public Student findById(String studentId) {
        GetItemResponse response = client.getItem(GetItemRequest.builder()
                .tableName(tableName)
                .key(StudentMapper.key(studentId))
                .build());

        if (!response.hasItem()) return null;
        return StudentMapper.fromItem(response.item());
    }

    @Override
    public List<Student> findAll() {
        ScanResponse response = client.scan(ScanRequest.builder().tableName(tableName).build());
        List<Student> students = new ArrayList<>();
        response.items().forEach(item -> students.add(StudentMapper.fromItem(item)));
        return students;
    }

    @Override
    public void delete(String studentId) {
        client.deleteItem(DeleteItemRequest.builder()
                .tableName(tableName)
                .key(StudentMapper.key(studentId))
                .build());
    }
}
