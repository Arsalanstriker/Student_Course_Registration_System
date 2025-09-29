package com.scrs.app;

import com.scrs.config.RepositoryFactory;
import com.scrs.model.Course;
import com.scrs.model.Enrollment;
import com.scrs.model.Student;
import com.scrs.service.EnrollmentService;
import com.scrs.service.impl.EnrollmentServiceImpl;
import com.scrs.repository.CourseRepository;
import com.scrs.repository.StudentRepository;
//import com.scrs.util.DataLoader;
import com.scrs.util.JsonCourseLoader;
import com.scrs.util.SessionManager;
import com.scrs.util.TableInitializer;

import java.util.List;
import java.util.Scanner;

public class StudentCourseRegistrationSystem {
    public static void main(String[] args) {
        // Build DB tables only if missing (fast startup)
        TableInitializer.main(args);

        // Seed students (courses come only from JSON file)
       // DataLoader.seedStudents();
        JsonCourseLoader.seedCourses("src/main/resources/courses.json");

        EnrollmentService service = new EnrollmentServiceImpl(
                RepositoryFactory.courseRepository(),
                RepositoryFactory.studentRepository(),
                RepositoryFactory.enrollmentRepository()
        );

        StudentRepository studentRepo = RepositoryFactory.studentRepository();
        CourseRepository courseRepo = RepositoryFactory.courseRepository();

        Scanner sc = new Scanner(System.in);
        System.out.println("Welcome to SCRS (Student Course Registration System)");

        while (true) {
            if (!SessionManager.isLoggedIn()) {
                // 👉 Not logged in yet
                System.out.println("\nMain Menu:");
                System.out.println("1. Signup");
                System.out.println("2. Login");
                System.out.println("3. Exit");
                System.out.print("Choice: ");
                String c = sc.nextLine();

                switch (c) {
                    case "1": // Signup
                        System.out.print("Enter new StudentId: ");
                        String sid = sc.nextLine();
                        System.out.print("Name: ");
                        String name = sc.nextLine();
                        System.out.print("Email: ");
                        String email = sc.nextLine();
                        studentRepo.save(new Student(sid, name, email));
                        System.out.println("Signup successful for " + sid);
                        break;

                    case "2": // Login
                        System.out.print("Enter StudentId or Name: ");
                        String loginInput = sc.nextLine();
                        Student st = studentRepo.findById(loginInput);
                        if (st == null) st = studentRepo.findByName(loginInput);

                        if (st == null) {
                            System.out.println("Student not found. Please signup first.");
                        } else {
                            SessionManager.login(st.getStudentId());
                            System.out.println("Welcome, " + st.getName() + "!");
                        }
                        break;

                    case "3":
                        System.out.println("Goodbye!");
                        sc.close();
                        return;

                    default:
                        System.out.println("Invalid choice.");
                }
            } else {
                // 👉 Student is logged in
                System.out.println("\nDashboard:");
                System.out.println("1. View All Courses");
                System.out.println("2. Search Course by Keyword (tags/title)");
                System.out.println("3. View My Enrollments");
                System.out.println("4. Logout");
                System.out.print("Choice: ");
                String c = sc.nextLine();

                switch (c) {
                    case "1": // Show all courses
                        List<Course> courses = courseRepo.findAll();
                        System.out.println("\nAvailable Courses:");
                        for (Course ci : courses) {
                            System.out.println(ci.getCourseId() + " | " + ci.getTitle() +
                                    " | Seats: " + ci.getCurrentEnrolledCount() + "/" + ci.getMaxSeats() +
                                    " | Deadline: " + ci.getLatestEnrollmentBy());
                        }
                        System.out.print("Enter CourseId to enroll or 'back': ");
                        String courseId = sc.nextLine();
                        if (!courseId.equalsIgnoreCase("back")) {
                            try {
                                Enrollment enr = service.enroll(SessionManager.getCurrentStudentId(), courseId);
                                System.out.println("Enrollment status: " + enr.getStatus());
                            } catch (Exception ex) {
                                System.out.println("Error: " + ex.getMessage());
                            }
                        }
                        break;

                    case "2": // Search by tag/title
                        System.out.print("Enter keyword (e.g., Java, Cloud, Beginner): ");
                        String keyword = sc.nextLine().toLowerCase();
                        List<Course> matches = courseRepo.findAll().stream()
                                .filter(ci -> ci.getTitle().toLowerCase().contains(keyword) ||
                                        (ci.getTags() != null && ci.getTags().toLowerCase().contains(keyword)))
                                .toList();

                        if (matches.isEmpty()) {
                            System.out.println("No courses found for: " + keyword);
                        } else {
                            for (Course ci : matches) {
                                System.out.println(ci.getCourseId() + " | " + ci.getTitle() +
                                        " | Seats: " + ci.getCurrentEnrolledCount() + "/" + ci.getMaxSeats() +
                                        " | Deadline: " + ci.getLatestEnrollmentBy());
                            }
                        }
                        break;

                    case "3": // My Enrollments
                        List<Enrollment> myEnrolls =
                                RepositoryFactory.enrollmentRepository().findByStudent(SessionManager.getCurrentStudentId());
                        System.out.println("\nMy Enrollments:");
                        if (myEnrolls.isEmpty()) {
                            System.out.println("No enrollments yet.");
                        } else {
                            for (Enrollment e : myEnrolls) {
                                System.out.println(e.getCourseId() + " | Status: " + e.getStatus());
                            }
                            System.out.print("Enter CourseId to drop or 'back': ");
                            String dropId = sc.nextLine();
                            if (!dropId.equalsIgnoreCase("back")) {
                                try {
                                    service.drop(SessionManager.getCurrentStudentId(), dropId);
                                    System.out.println("Dropped successfully.");
                                } catch (Exception ex) {
                                    System.out.println("Error: " + ex.getMessage());
                                }
                            }
                        }
                        break;

                    case "4":
                        SessionManager.logout();
                        System.out.println("Logged out.");
                        break;

                    default:
                        System.out.println("Invalid choice.");
                }
            }
        }
    }
}
