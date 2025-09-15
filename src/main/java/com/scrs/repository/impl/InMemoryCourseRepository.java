package com.scrs.repository.impl;

import com.scrs.model.Course;
import com.scrs.repository.CourseRepository;
import com.scrs.repository.InMemoryDatabase;

import java.util.ArrayList;
import java.util.List;

public class InMemoryCourseRepository implements CourseRepository {
    private final InMemoryDatabase db = InMemoryDatabase.getInstance();

    @Override
    public void save(Course course) {
        db.getCourses().put(course.getCourseId(), course);
    }

    @Override
    public Course findById(String courseId) {
        return db.getCourses().get(courseId);
    }

    @Override
    public List<Course> findAll() {
        return new ArrayList<>(db.getCourses().values());
    }

    @Override
    public void delete(String courseId) {
        db.getCourses().remove(courseId);
    }
}
