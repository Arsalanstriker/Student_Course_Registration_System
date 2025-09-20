package com.scrs.repository.impl;

import com.scrs.model.Course;
import com.scrs.repository.CourseRepository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.ArrayList;
import java.util.List;

public class DynamoDbCourseRepository implements CourseRepository {
    private final DynamoDbClient client;
    private final String tableName;

    public DynamoDbCourseRepository(DynamoDbClient client, String tableName) {
        this.client = client;
        this.tableName = tableName;
    }

    @Override
    public void save(Course course) {
        client.putItem(PutItemRequest.builder()
                .tableName(tableName)
                .item(CourseMapper.toItem(course))
                .build());
    }

    @Override
    public Course findById(String courseId) {
        GetItemResponse response = client.getItem(GetItemRequest.builder()
                .tableName(tableName)
                .key(CourseMapper.key(courseId))
                .build());

        if (!response.hasItem()) return null;
        return CourseMapper.fromItem(response.item());
    }

    @Override
    public List<Course> findAll() {
        ScanResponse response = client.scan(ScanRequest.builder().tableName(tableName).build());
        List<Course> courses = new ArrayList<>();
        response.items().forEach(item -> courses.add(CourseMapper.fromItem(item)));
        return courses;
    }

    @Override
    public void delete(String courseId) {
        client.deleteItem(DeleteItemRequest.builder()
                .tableName(tableName)
                .key(CourseMapper.key(courseId))
                .build());
    }
}
