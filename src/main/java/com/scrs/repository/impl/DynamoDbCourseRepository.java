package com.scrs.repository.impl;

import com.scrs.model.Course;
import com.scrs.repository.CourseRepository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DynamoDbCourseRepository implements CourseRepository {

    private final DynamoDbClient client;
    private static final String TABLE = "Courses"; // 👉 Hardcoded table name

    public DynamoDbCourseRepository(DynamoDbClient client) {
        this.client = client;
    }

    @Override
    public void save(Course course) {
        client.putItem(PutItemRequest.builder()
                .tableName(TABLE)
                .item(CourseMapper.toItem(course))
                .build());
    }

    @Override
    public Course findById(String courseId) {
        Map<String, AttributeValue> key = CourseMapper.key(courseId);
        GetItemResponse response = client.getItem(GetItemRequest.builder()
                .tableName(TABLE)
                .key(key)
                .build());
        return response.hasItem() ? CourseMapper.fromItem(response.item()) : null;
    }

    @Override
    public List<Course> findAll() {
        ScanResponse response = client.scan(ScanRequest.builder().tableName(TABLE).build());
        List<Course> list = new ArrayList<>();
        for (Map<String, AttributeValue> item : response.items()) {
            list.add(CourseMapper.fromItem(item));
        }
        return list;
    }
}
