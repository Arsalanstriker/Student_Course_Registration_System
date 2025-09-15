package com.scrs.repository.impl;

import com.scrs.model.Enrollment;
import com.scrs.repository.EnrollmentRepository;
import com.scrs.repository.InMemoryDatabase;

import java.util.ArrayList;
import java.util.List;

public class InMemoryEnrollmentRepository implements EnrollmentRepository {
    private final InMemoryDatabase db = InMemoryDatabase.getInstance();

    @Override
    public void save(Enrollment enrollment) {
        String key = enrollment.getStudentId() + "-" + enrollment.getCourseId();
        db.getEnrollments().put(key, enrollment);
    }

    @Override
    public Enrollment findById(String studentId, String courseId) {
        return db.getEnrollments().get(studentId + "-" + courseId);
    }

    @Override
    public List<Enrollment> findByStudentId(String studentId) {
        return db.getEnrollments().values().stream()
                .filter(e -> e.getStudentId().equals(studentId))
                .toList();
    }

    @Override
    public List<Enrollment> findByCourseId(String courseId) {
        return db.getEnrollments().values().stream()
                .filter(e -> e.getCourseId().equals(courseId))
                .toList();
    }

    @Override
    public void delete(String studentId, String courseId) {
        db.getEnrollments().remove(studentId + "-" + courseId);
    }
}
