package com.scrs.repository.impl;

import com.scrs.model.Enrollment;
import com.scrs.model.EnrollmentStatus;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;


import java.util.HashMap;
import java.util.Map;

public class EnrollmentMapper {

    public static Map<String, AttributeValue> toItem(Enrollment enrollment) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("studentId", AttributeValue.fromS(enrollment.getStudentId()));
        item.put("courseId", AttributeValue.fromS(enrollment.getCourseId()));
        item.put("status", AttributeValue.fromS(enrollment.getStatus().name()));

        if (enrollment.getWaitlistPosition() > 0) {
            item.put("waitlistPosition", AttributeValue.fromN(
                    String.valueOf(enrollment.getWaitlistPosition())));
        }
        return item;
    }

    public static Map<String, AttributeValue> key(String studentId, String courseId) {
        Map<String, AttributeValue> key = new HashMap<>();
        key.put("studentId", AttributeValue.fromS(studentId));
        key.put("courseId", AttributeValue.fromS(courseId));
        return key;
    }

    public static Enrollment fromItem(Map<String, AttributeValue> item) {
        Enrollment enrollment = new Enrollment(
                item.get("studentId").s(),
                item.get("courseId").s(),
                EnrollmentStatus.valueOf(item.get("status").s())
        );

        if (item.containsKey("waitlistPosition")) {
            enrollment.setWaitlistPosition(Integer.parseInt(item.get("waitlistPosition").n()));
        }
        return enrollment;
    }

}
