package com.scrs.util;

import com.scrs.config.RepositoryFactory;
import com.scrs.model.Course;
import com.scrs.model.Student;

public class DataLoader {
    public static void main(String[] args) {
        var courseRepo = RepositoryFactory.courseRepository();
        var studentRepo = RepositoryFactory.studentRepository();

        // ✅ Seed courses (courseId, title, description, maxSeats)
        courseRepo.save(new Course("C101", "Java Fundamentals", "Intro to Java programming", 3));
        courseRepo.save(new Course("C102", "Data Structures", "Learn arrays, linked lists, trees, graphs", 2));
        courseRepo.save(new Course("C103", "Databases", "SQL & NoSQL fundamentals", 2));
        courseRepo.save(new Course("C104", "Web Development", "Frontend + Backend basics", 5));
        courseRepo.save(new Course("C105", "Cloud Computing", "AWS, Azure, GCP overview", 4));

        // ✅ Seed students (studentId, name, email)
        studentRepo.save(new Student("S101", "Alice", "alice@example.com"));
        studentRepo.save(new Student("S102", "Bob", "bob@example.com"));
        studentRepo.save(new Student("S103", "Charlie", "charlie@example.com"));

        System.out.println("✅ Seed data loaded into DynamoDB!");
    }
}
