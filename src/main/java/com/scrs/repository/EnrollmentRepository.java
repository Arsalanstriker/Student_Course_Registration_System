package com.scrs.repository;

import com.scrs.model.Enrollment;
import java.util.List;

public interface EnrollmentRepository {
    void save(Enrollment enrollment);
    Enrollment findById(String studentId, String courseId);
    List<Enrollment> findByStudentId(String studentId);
    List<Enrollment> findByCourseId(String courseId);
    void delete(String studentId, String courseId);
}
