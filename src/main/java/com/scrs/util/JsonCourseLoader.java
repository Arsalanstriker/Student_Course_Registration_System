package com.scrs.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.scrs.config.RepositoryFactory;
import com.scrs.model.Course;
import com.scrs.repository.CourseRepository;

import java.io.File;
import java.util.List;

public class JsonCourseLoader {
    public static void seedCourses(String filePath) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule()); //  Helps parse LocalDate

            List<Course> courses = mapper.readValue(new File(filePath), new TypeReference<>() {});
            CourseRepository repo = RepositoryFactory.courseRepository();

            for (Course c : courses) {
                //  Only save if this course does NOT already exist in DB
                if (repo.findById(c.getCourseId()) == null) {
                    repo.save(c);
                }
            }
            System.out.println("");
        } catch (Exception e) {
            throw new RuntimeException("Failed to load courses.json", e);
        }
    }
}
