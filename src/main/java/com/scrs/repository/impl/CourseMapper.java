package com.scrs.repository.impl;

import com.scrs.model.Course;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class CourseMapper {

    // Convert Course -> DynamoDB item
    public static Map<String, AttributeValue> toItem(Course course) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("courseId", AttributeValue.fromS(course.getCourseId()));
        item.put("title", AttributeValue.fromS(course.getTitle()));
        item.put("maxSeats", AttributeValue.fromN(String.valueOf(course.getMaxSeats())));
        item.put("currentEnrolledCount", AttributeValue.fromN(String.valueOf(course.getCurrentEnrolledCount())));
        item.put("waitlistSize", AttributeValue.fromN(String.valueOf(course.getWaitlistSize())));

        if (course.getStartDate() != null) {
            item.put("startDate", AttributeValue.fromS(course.getStartDate().toString()));
        }
        if (course.getEndDate() != null) {
            item.put("endDate", AttributeValue.fromS(course.getEndDate().toString()));
        }
        if (course.getLatestEnrollmentBy() != null) {
            item.put("latestEnrollmentBy", AttributeValue.fromS(course.getLatestEnrollmentBy().toString()));

        }
        if (course.getTags() != null) {
            item.put("tags", AttributeValue.fromS(course.getTags()));
        }

        return item;
    }

    // Convert DynamoDB item -> Course
    public static Course fromItem(Map<String, AttributeValue> item) {
        if (item == null || item.isEmpty()) return null;

        Course course = new Course(
                item.get("courseId").s(),
                item.get("title").s(),
                Integer.parseInt(item.get("maxSeats").n())
        );

        if (item.containsKey("currentEnrolledCount")) {
            course.setCurrentEnrolledCount(Integer.parseInt(item.get("currentEnrolledCount").n()));
        }
        if (item.containsKey("waitlistSize")) {
            course.setWaitlistSize(Integer.parseInt(item.get("waitlistSize").n()));
        }
        if (item.containsKey("startDate")) {
            course.setStartDate(LocalDate.parse(item.get("startDate").s()));
        }
        if (item.containsKey("endDate")) {
            course.setEndDate(LocalDate.parse(item.get("endDate").s()));
        }
        if (item.containsKey("latestEnrollmentBy")) {
            course.setLatestEnrollmentBy(LocalDate.parse(item.get("latestEnrollmentBy").s()));

        }
        if (item.containsKey("tags")) {
            course.setTags(item.get("tags").s());
        }

        return course;
    }

    // Primary key
    public static Map<String, AttributeValue> key(String courseId) {
        Map<String, AttributeValue> key = new HashMap<>();
        key.put("courseId", AttributeValue.fromS(courseId));//Primary key
        return key;
    }
}
