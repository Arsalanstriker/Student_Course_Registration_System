package com.scrs.repository.impl;

import com.scrs.model.Course;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.Map;

public class CourseMapper {
    static Map<String, AttributeValue> toItem(Course course) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("courseId", AttributeValue.fromS(course.getCourseId()));
        item.put("title", AttributeValue.fromS(course.getTitle()));
        item.put("instructor", AttributeValue.fromS(course.getInstructor()));
        item.put("maxSeats", AttributeValue.fromN(String.valueOf(course.getMaxSeats())));
        item.put("currentEnrolledCount", AttributeValue.fromN(String.valueOf(course.getCurrentEnrolledCount())));
        item.put("waitlistSize", AttributeValue.fromN(String.valueOf(course.getWaitlistSize())));
        return item;
    }

    static Map<String, AttributeValue> key(String courseId) {
        Map<String, AttributeValue> key = new HashMap<>();
        key.put("courseId", AttributeValue.fromS(courseId));
        return key;
    }

    static Course fromItem(Map<String, AttributeValue> item) {
        Course course = new Course(
                item.get("courseId").s(),
                item.get("title").s(),
                item.get("instructor").s(),
                Integer.parseInt(item.get("maxSeats").n())
        );
        course.setCurrentEnrolledCount(Integer.parseInt(item.get("currentEnrolledCount").n()));
        course.setWaitlistSize(Integer.parseInt(item.get("waitlistSize").n()));
        return course;
    }
}
