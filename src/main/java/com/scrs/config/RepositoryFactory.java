package com.scrs.config;

import com.scrs.repository.CourseRepository;
import com.scrs.repository.EnrollmentRepository;
import com.scrs.repository.StudentRepository;
import com.scrs.repository.impl.DynamoDbCourseRepository;
import com.scrs.repository.impl.DynamoDbEnrollmentRepository;
import com.scrs.repository.impl.DynamoDbStudentRepository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class RepositoryFactory {
    private static final DynamoDbClient client = DynamoDbConfig.createClient();

    public static CourseRepository courseRepository() {
        return new DynamoDbCourseRepository(client, "Courses");
    }

    public static StudentRepository studentRepository() {
        return new DynamoDbStudentRepository(client, "Students");
    }

    public static EnrollmentRepository enrollmentRepository() {
        return new DynamoDbEnrollmentRepository(client, "Enrollments");
    }
}
