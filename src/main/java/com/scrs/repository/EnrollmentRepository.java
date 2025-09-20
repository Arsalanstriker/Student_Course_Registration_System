package com.scrs.repository;

import com.scrs.model.Enrollment;
import java.util.List;

public interface EnrollmentRepository {
    void save(Enrollment enrollment);
    Enrollment findById(String studentId, String courseId);
    List<Enrollment> findByStudent(String studentId);  // required
    List<Enrollment> findAll();
    void delete(String studentId, String courseId);
}
