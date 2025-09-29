package com.scrs.repository;

import com.scrs.model.Course;
import java.util.List;

public interface CourseRepository {
    // Save or update a course in DB
    void save(Course course);

    //  Get a single course using its unique ID
    Course findById(String courseId);

    //  Get all available courses
    List<Course> findAll();
}
