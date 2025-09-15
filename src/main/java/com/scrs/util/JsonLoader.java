package com.scrs.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scrs.model.Course;
import com.scrs.model.Student;

import java.io.InputStream;
import java.util.List;

public class JsonLoader {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static List<Student> loadStudents(String path) {
        try (InputStream is = JsonLoader.class.getResourceAsStream(path)) {
            return mapper.readValue(is, new TypeReference<List<Student>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Failed to load students from " + path, e);
        }
    }

    public static List<Course> loadCourses(String path) {
        try (InputStream is = JsonLoader.class.getResourceAsStream(path)) {
            return mapper.readValue(is, new TypeReference<List<Course>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Failed to load courses from " + path, e);
        }
    }
}
