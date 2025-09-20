package com.scrs.repository.impl;

import com.scrs.model.Student;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.Map;

public class StudentMapper {

    static Map<String, AttributeValue> toItem(Student student) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("studentId", AttributeValue.fromS(student.getStudentId()));
        item.put("name", AttributeValue.fromS(student.getName()));
        item.put("email", AttributeValue.fromS(student.getEmail()));
        item.put("activeEnrollments", AttributeValue.fromN(String.valueOf(student.getActiveEnrollments())));
        item.put("waitlistCount", AttributeValue.fromN(String.valueOf(student.getWaitlistCount())));
        return item;
    }

    static Map<String, AttributeValue> key(String studentId) {
        Map<String, AttributeValue> key = new HashMap<>();
        key.put("studentId", AttributeValue.fromS(studentId));
        return key;
    }

    static Student fromItem(Map<String, AttributeValue> item) {
        Student student = new Student(
                item.get("studentId").s(),
                item.get("name").s(),
                item.get("email").s()
        );
        student.setActiveEnrollments(Integer.parseInt(item.get("activeEnrollments").n()));
        student.setWaitlistCount(Integer.parseInt(item.get("waitlistCount").n()));
        return student;
    }
}
