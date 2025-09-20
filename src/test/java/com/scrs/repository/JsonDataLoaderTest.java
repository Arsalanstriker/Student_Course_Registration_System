package com.scrs.repository;

import com.scrs.model.Course;
import com.scrs.model.Student;
import com.scrs.repository.impl.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JsonDataLoaderTest {

    private InMemoryCourseRepository courseRepo;
    private InMemoryStudentRepository studentRepo;

    @BeforeEach
    void setup() {
        InMemoryDatabase.getInstance().clearAll();
        courseRepo = new InMemoryCourseRepository();
        studentRepo = new InMemoryStudentRepository();
    }

    @Test
    void testJsonDataLoading() {
        JsonDataLoader.loadData();

        List<Student> students = studentRepo.findAll();
        List<Course> courses = courseRepo.findAll();

        assertEquals(3, students.size(), "Should load 12 students from JSON (if you used the 12 list)");
        assertTrue(courses.size() >= 2, "Should load courses from JSON");
    }
}
