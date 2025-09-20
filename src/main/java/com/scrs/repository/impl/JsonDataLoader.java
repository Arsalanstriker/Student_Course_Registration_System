//package com.scrs.repository.impl;
//
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.scrs.model.Course;
//import com.scrs.model.Student;
//
//import java.io.InputStream;
//import java.util.List;
//
//public class JsonDataLoader {
//
//    private static final ObjectMapper mapper = new ObjectMapper();
//
//    /**
//     * Load data from resources/seed/students.json and resources/seed/courses.json
//     * Put them into the InMemoryDatabase maps.
//     */
//    public static void loadData() {
//        InMemoryDatabase db = InMemoryDatabase.getInstance();
//
//        try (InputStream studentStream = JsonDataLoader.class.getResourceAsStream("/students.json")) {
//            if (studentStream != null) {
//                List<Student> students = mapper.readValue(studentStream, new TypeReference<List<Student>>() {});
//                for (Student s : students) {
//                    db.getStudents().put(s.getStudentId(), s);
//                }
//            } else {
//                throw new RuntimeException("students.json not found on classpath /seed/");
//            }
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to load students.json", e);
//        }
//
//        try (InputStream courseStream = JsonDataLoader.class.getResourceAsStream("/courses.json")) {
//            if (courseStream != null) {
//                List<Course> courses = mapper.readValue(courseStream, new TypeReference<List<Course>>() {});
//                for (Course c : courses) {
//                    db.getCourses().put(c.getCourseId(), c);
//                }
//            } else {
//                throw new RuntimeException("courses.json not found on classpath /seed/");
//            }
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to load courses.json", e);
//        }
//    }
//}