package com.scrs.app;

import com.scrs.model.Course;
import com.scrs.model.Student;
import com.scrs.repository.impl.InMemoryCourseRepository;
import com.scrs.repository.impl.InMemoryStudentRepository;
import com.scrs.util.JsonLoader;

import java.util.List;

public class StudentCourseRegistrationSystem {
    public static void main(String[] args) {
        InMemoryStudentRepository studentRepo = new InMemoryStudentRepository();
        InMemoryCourseRepository courseRepo = new InMemoryCourseRepository();

        // Load from JSON
        List<Student> students = JsonLoader.loadStudents("/seed/students.json");
        List<Course> courses = JsonLoader.loadCourses("/seed/courses.json");

        // Save into repositories
        students.forEach(studentRepo::save);
        courses.forEach(courseRepo::save);

        System.out.println(" Loaded " + students.size() + " students and "
                + courses.size() + " courses from JSON.");
    }
}
