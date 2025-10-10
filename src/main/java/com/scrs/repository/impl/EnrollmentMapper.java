package com.scrs.repository.impl;

import com.scrs.model.Enrollment;
import com.scrs.model.EnrollmentStatus;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class EnrollmentMapper {
    public static Map<String, AttributeValue> toItem(Enrollment e) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("studentId", AttributeValue.fromS(e.getStudentId()));
        item.put("courseId", AttributeValue.fromS(e.getCourseId()));
        item.put("status", AttributeValue.fromS(e.getStatus().name()));
        item.put("waitlistPosition", AttributeValue.fromN(String.valueOf(e.getWaitlistPosition())));
        if (e.getTimestamp() != null) item.put("timestamp", AttributeValue.fromS(e.getTimestamp().toString()));
        return item;
    }

    public static Map<String, AttributeValue> key(String studentId, String courseId) {
        Map<String, AttributeValue> key = new HashMap<>();
        key.put("studentId", AttributeValue.fromS(studentId));//Keys(Pk)-->Primary key
        key.put("courseId", AttributeValue.fromS(courseId));//Key(SK)-->sortKey
        return key;
    }

    public static Enrollment fromItem(Map<String, AttributeValue> item) {
        Enrollment e = new Enrollment(
                item.get("studentId").s(),
                item.get("courseId").s(),
                EnrollmentStatus.valueOf(item.get("status").s())
        );
        if (item.containsKey("waitlistPosition")) e.setWaitlistPosition(Integer.parseInt(item.get("waitlistPosition").n()));
        if (item.containsKey("timestamp")) e.setTimestamp(LocalDateTime.parse(item.get("timestamp").s()));
        return e;
    }
}
