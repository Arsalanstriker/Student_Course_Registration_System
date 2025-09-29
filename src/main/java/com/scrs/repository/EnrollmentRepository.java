package com.scrs.repository;

import com.scrs.model.Enrollment;
import java.util.List;

public interface EnrollmentRepository {
    // Save or update a student’s enrollment record
    void save(Enrollment enrollment);

    //  Find a specific enrollment using student + course
    Enrollment findById(String studentId, String courseId);

    // Find all courses one student has enrolled in
    List<Enrollment> findByStudent(String studentId);

    //  Find all students who enrolled (or waitlisted) in a course
    List<Enrollment> findByCourse(String courseId);

    //  Delete a specific student’s enrollment record
    void delete(String studentId, String courseId);
}
