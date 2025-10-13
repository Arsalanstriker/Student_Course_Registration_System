package com.scrs.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.scrs.config.RepositoryFactory;
import com.scrs.model.Course;
import com.scrs.repository.CourseRepository;

import java.io.InputStream;
import java.io.FileNotFoundException;
import java.util.List;

public class JsonCourseLoader {

    public static void seedCourses(String fileName) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule()); // helps parse LocalDate

            //  Load file from classpath (inside JAR)
            InputStream inputStream = JsonCourseLoader.class
                    .getClassLoader()
                    .getResourceAsStream(fileName);

            if (inputStream == null) {
                throw new FileNotFoundException("Could not find " + fileName + " in classpath");
            }

            List<Course> courses = mapper.readValue(inputStream, new TypeReference<>() {});
            CourseRepository repo = RepositoryFactory.courseRepository();

            for (Course c : courses) {
                if (repo.findById(c.getCourseId()) == null) {
                    repo.save(c);
                }
            }

            System.out.println(" Successfully loaded courses from " + fileName);

        } catch (Exception e) {
            throw new RuntimeException("Failed to load " + fileName, e);
        }
    }
}
