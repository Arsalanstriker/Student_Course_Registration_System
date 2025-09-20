package com.scrs.app;

import com.scrs.config.RepositoryFactory;
import com.scrs.model.Course;
import com.scrs.model.Enrollment;
import com.scrs.model.Student;
import com.scrs.service.EnrollmentService;
import com.scrs.service.impl.EnrollmentServiceImpl;

import java.util.List;
import java.util.Scanner;

public class StudentCourseRegistrationSystem {
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        var courseRepo = RepositoryFactory.courseRepository();
        var studentRepo = RepositoryFactory.studentRepository();
        var enrollmentRepo = RepositoryFactory.enrollmentRepository();
        EnrollmentService service = new EnrollmentServiceImpl(courseRepo, studentRepo, enrollmentRepo);

        System.out.println("🎓 Welcome to the Student Course Registration System!");

        String studentId = null;
        while (true) {
            try {
                if (studentId == null) {
                    showAuthMenu();
                    int choice = Integer.parseInt(scanner.nextLine());

                    switch (choice) {
                        case 1 -> {
                            System.out.print("Enter studentId: ");
                            studentId = scanner.nextLine();
                            System.out.print("Enter name: ");
                            String name = scanner.nextLine();
                            System.out.print("Enter email: ");
                            String email = scanner.nextLine();

                            Student s = new Student(studentId, name, email);
                            studentRepo.save(s);
                            System.out.println("✅ Signup successful!");
                        }
                        case 2 -> {
                            System.out.print("Enter studentId: ");
                            String id = scanner.nextLine();
                            if (studentRepo.findById(id) != null) {
                                studentId = id;
                                System.out.println("✅ Login successful!");
                            } else {
                                System.out.println("❌ Student not found. Please sign up first.");
                            }
                        }
                        case 3 -> System.exit(0);
                    }
                } else {
                    showMainMenu();
                    int choice = Integer.parseInt(scanner.nextLine());

                    switch (choice) {
                        case 1 -> {
                            List<Course> courses = courseRepo.findAll();
                            courses.forEach(c -> System.out.println(
                                    c.getCourseId() + " - " + c.getTitle() +
                                            " | Seats: " + c.getCurrentEnrolledCount() + "/" + c.getMaxSeats()
                            ));
                        }
                        case 2 -> {
                            System.out.print("Enter courseId: ");
                            String courseId = scanner.nextLine();
                            try {
                                Enrollment e = service.enroll(studentId, courseId);
                                System.out.println("✅ Status: " + e.getStatus());
                            } catch (Exception ex) {
                                System.out.println("❌ " + ex.getMessage());
                            }
                        }
                        case 3 -> {
                            System.out.print("Enter courseId: ");
                            String courseId = scanner.nextLine();
                            service.drop(studentId, courseId);
                            System.out.println("✅ Dropped from course " + courseId);
                        }
                        case 4 -> {
                            List<Enrollment> myEnrollments = enrollmentRepo.findByStudent(studentId);
                            if (myEnrollments.isEmpty()) {
                                System.out.println("📭 No enrollments yet.");
                            } else {
                                myEnrollments.forEach(e -> System.out.println(
                                        e.getCourseId() + " -> " + e.getStatus()
                                ));
                            }
                        }
                        case 5 -> {
                            studentId = null;
                            System.out.println("🔒 Logged out.");
                        }
                        case 6 -> System.exit(0);
                    }
                }
            } catch (Exception e) {
                System.out.println("⚠️ Error: " + e.getMessage());
            }
        }
    }

    private static void showAuthMenu() {
        System.out.println("\n1. Sign Up\n2. Login\n3. Exit");
        System.out.print("Choose: ");
    }

    private static void showMainMenu() {
        System.out.println("\n1. View Courses\n2. Enroll in Course\n3. Drop Course\n4. View My Enrollments\n5. Logout\n6. Exit");
        System.out.print("Choose: ");
    }
}
