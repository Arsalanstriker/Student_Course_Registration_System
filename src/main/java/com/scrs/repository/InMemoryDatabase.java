package com.scrs.repository;

import com.scrs.model.Course;
import com.scrs.model.Enrollment;
import com.scrs.model.Student;

import java.util.HashMap;
import java.util.Map;

public class InMemoryDatabase {
    private static InMemoryDatabase instance;

    private final Map<String, Student> students;
    private final Map<String, Course> courses;
    private final Map<String, Enrollment> enrollments;

    private InMemoryDatabase() {
        students = new HashMap<>();
        courses = new HashMap<>();
        enrollments = new HashMap<>();
    }

    public static synchronized InMemoryDatabase getInstance() {
        if (instance == null) {
            instance = new InMemoryDatabase();
        }
        return instance;
    }

    public Map<String, Student> getStudents() {
        return students;
    }

    public Map<String, Course> getCourses() {
        return courses;
    }

    public Map<String, Enrollment> getEnrollments() {
        return enrollments;
    }

    public void clearAll() {
        students.clear();
        courses.clear();
        enrollments.clear();
    }
}