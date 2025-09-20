package com.scrs.repository;

import com.scrs.model.Course;

import java.util.List;

public interface CourseRepository {
    void save(Course course);
    Course findById(String courseId);
    List<Course> findAll();
    void delete(String courseId);
}
