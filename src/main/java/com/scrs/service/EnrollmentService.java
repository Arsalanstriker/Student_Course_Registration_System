package com.scrs.service;

import com.scrs.model.Enrollment;

public interface EnrollmentService {
    Enrollment enroll(String studentId, String courseId);
    void drop(String studentId, String courseId);
}
