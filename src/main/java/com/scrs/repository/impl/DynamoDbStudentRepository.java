package com.scrs.repository.impl;

import com.scrs.model.Student;
import com.scrs.repository.StudentRepository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DynamoDbStudentRepository implements StudentRepository {

    private final DynamoDbClient client;
    private static final String TABLE = "Students";

    public DynamoDbStudentRepository(DynamoDbClient client) {
        this.client = client;
    }

    @Override
    public void save(Student student) {
        client.putItem(PutItemRequest.builder()
                .tableName(TABLE)
                .item(StudentMapper.toItem(student))
                .build());
    }

    @Override
    public Student findById(String studentId) {
        Map<String, AttributeValue> key = StudentMapper.key(studentId);
        GetItemResponse response = client.getItem(GetItemRequest.builder()
                .tableName(TABLE)
                .key(key)
                .build());
        return response.hasItem() ? StudentMapper.fromItem(response.item()) : null;
    }

    @Override
    public Student findByName(String name) {
        //Find by name
        Map<String, String> expressionNames = Map.of("#nm", "name");

        Map<String, AttributeValue> expressionValues = Map.of(
                ":n", AttributeValue.fromS(name)
        );

        ScanRequest scan = ScanRequest.builder()
                .tableName(TABLE)
                .filterExpression("#nm = :n")
                .expressionAttributeNames(expressionNames)
                .expressionAttributeValues(expressionValues)
                .build();

        ScanResponse response = client.scan(scan);
        if (!response.items().isEmpty()) {
            return StudentMapper.fromItem(response.items().get(0));
        }
        return null;
    }


    @Override
    public List<Student> findAll() {
        ScanResponse response = client.scan(ScanRequest.builder().tableName(TABLE).build());
        List<Student> list = new ArrayList<>();
        for (Map<String, AttributeValue> item : response.items()) {
            list.add(StudentMapper.fromItem(item));
        }
        return list;
    }
}
