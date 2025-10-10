package com.scrs.service;

import com.scrs.model.Enrollment;

public interface EnrollmentService {
    Enrollment enroll(String studentId, String courseId);//adds student to Db
    void drop(String studentId, String courseId);//drops student to db
}
