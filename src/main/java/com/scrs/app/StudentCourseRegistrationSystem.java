package com.scrs.app;

import com.scrs.config.RepositoryFactory;
import com.scrs.exception.*;
import com.scrs.model.Course;
import com.scrs.model.Enrollment;
import com.scrs.model.EnrollmentStatus;
import com.scrs.model.Student;
import com.scrs.repository.CourseRepository;
import com.scrs.repository.EnrollmentRepository;
import com.scrs.repository.StudentRepository;
import com.scrs.service.EnrollmentService;
import com.scrs.service.impl.EnrollmentServiceImpl;
import com.scrs.util.JsonCourseLoader;
import com.scrs.util.SessionManager;
import com.scrs.util.TableInitializer;

import java.util.List;
import java.util.Scanner;

public class StudentCourseRegistrationSystem {

    // --- Color Codes ---
    private static final String GREEN = "\u001B[32m";
    private static final String RED = "\u001B[31m";
    private static final String YELLOW = "\u001B[33m";
    private static final String CYAN = "\u001B[36m";
    private static final String RESET = "\u001B[0m";

    // --- Repos ---
    private static final CourseRepository courseRepo = RepositoryFactory.courseRepository();
    private static final StudentRepository studentRepo = RepositoryFactory.studentRepository();
    private static final EnrollmentRepository enrollmentRepo = RepositoryFactory.enrollmentRepository();
    private static final EnrollmentService service = new EnrollmentServiceImpl(courseRepo, studentRepo, enrollmentRepo);

    // ---  In-Memory Session ---
    private static String loggedInStudent = null;

    public static void main(String[] args) {
        //TableInitializer.main(args);
        //--- Json file loader
        JsonCourseLoader.seedCourses("courses.json");
        printBanner();

        Scanner sc = new Scanner(System.in);
        while (true) {
            if (loggedInStudent == null) mainMenu(sc);
            else dashboard(sc);
        }
    }

    // === MENU Options ===
    private static void mainMenu(Scanner sc) {

        System.out.print("""
                
                ================== MAIN MENU ==================
                1) Signup
                2) Login
                3) Exit
                Enter choice : """);
        switch (sc.nextLine().trim()) {
            case "1" -> signup(sc);
            case "2" -> login(sc);
            case "3" -> exitApp();
            default -> printlnWarn("Please choose a valid option (1–3).");
        }
    }

    private static void dashboard(Scanner sc) {
        System.out.print("""
                
                ================== DASHBOARD ==================
                1) View all courses
                2) Search Courses(Ex: Java , AWS)
                3) My Enrollments
                4) Logout
                Enter choice: """);
        switch (sc.nextLine().trim()) {
            case "1" -> listCoursesAndEnroll(sc);
            case "2" -> searchCourses(sc);
            case "3" -> viewMyEnrollments(sc);
            case "4" -> logout();
            default -> printlnWarn("Invalid option. Try again.");
        }
    }

    // === AUTHENTICATION ===
    private static void signup(Scanner sc) {
        System.out.print("Enter new Student ID: ");
        String sid = sc.nextLine().trim();
        if (sid.isEmpty()) {
            printlnError("Student ID cannot be empty!");
            return;
        }

        System.out.print("Enter Name: ");
        String name = sc.nextLine().trim();
        if (name.isEmpty()) {
            printlnError("Name cannot be empty!");
            return;
        }

        System.out.print("Enter Email: ");
        String email = sc.nextLine().trim();
        if (email.isEmpty()) {
            printlnError("Email cannot be empty!");
            return;
        }

        try {
            // Save new student record
            Student s = new Student(sid, name, email);
            studentRepo.save(s);
            printlnSuccess("Signup successful for " + sid + " (" + name + ")");
        } catch (Exception e) {
            printlnError("Error during signup: " + e.getMessage());
        }
    }


    private static void login(Scanner sc) {
        System.out.print("Enter Student ID or Name: ");
        String input = sc.nextLine().trim();
        Student s = studentRepo.findById(input);
        if (s == null) s = studentRepo.findByName(input);

        if (s == null) {
            printlnError("No student found. Please signup first!");
        } else {
            loggedInStudent = s.getStudentId();
            printlnSuccess("Welcome " + s.getName() + "! You are now logged in." +
                    "\n---------- Happy learning--------------");
        }
    }

    private static void logout() {
        loggedInStudent = null;
        printlnInfo("You have been logged out successfully.");
    }

    // === COURSE VIEW & ENROLLMENT ===
    private static void listCoursesAndEnroll(Scanner sc) {
        List<Course> courses = courseRepo.findAll();
        if (courses.isEmpty()) {
            printlnWarn("No courses available!");
            return;
        }
        printCourses(courses);
        System.out.print("Enter Course ID to Enroll or 'back': ");
        String id = sc.nextLine().trim();
        if (!id.equalsIgnoreCase("back")) enrollCourse(id);
    }

    private static void searchCourses(Scanner sc) {
        System.out.print("Enter keyword: ");
        String keyword = sc.nextLine().trim().toLowerCase();
        List<Course> results = courseRepo.findAll().stream()
                .filter(c -> c.getTitle().toLowerCase().contains(keyword))
                .toList();
        if (results.isEmpty()) {
            printlnWarn("No courses found for '" + keyword + "'.");
        } else {
            printCourses(results);
            System.out.print("Enter Course ID to Enroll or 'back': ");
            String id = sc.nextLine().trim();
            if (!id.equalsIgnoreCase("back")) enrollCourse(id);
        }
    }

    private static void viewMyEnrollments(Scanner sc) {
        List<Enrollment> enrolls = enrollmentRepo.findByStudent(loggedInStudent);
        if (enrolls.isEmpty()) {
            printlnWarn("You have no enrollments yet.");
            return;
        }
        System.out.println(CYAN + "\n--- Your Enrollments ---" + RESET);
        enrolls.forEach(e ->
                System.out.printf("%-10s | %-10s%n", e.getCourseId(),
                        e.getStatus() == EnrollmentStatus.ENROLLED ? GREEN + "ENROLLED" + RESET :
                                YELLOW + "WAITLISTED" + RESET));
        System.out.print("Enter Course ID to Drop or 'back': ");
        String id = sc.nextLine().trim();
        if (!id.equalsIgnoreCase("back")) dropCourse(id);
    }

    // === ENROLL / DROP ===
    private static void enrollCourse(String courseId) {
        try {
            Enrollment e = service.enroll(loggedInStudent, courseId);
            if (e.getStatus() == EnrollmentStatus.ENROLLED)
                printlnSuccess(" Enrolled successfully in " + courseId);
            else
                printlnWarn(" Seats full , Added to waitlist for " + courseId);
        } catch (DuplicateEnrollmentException ex) {
            printlnWarn(" Already enrolled or waitlisted!");
        } catch (CourseNotFoundException ex) {
            printlnError(" Course not found!");
        } catch (WaitlistFullException ex) {
            printlnError(" Waitlist full for this course!");
        } catch (Exception ex) {
            printlnError(" Enrollment failed: " + ex.getMessage());
        }
    }

    private static void dropCourse(String courseId) {
        try {
            service.drop(loggedInStudent, courseId);
            printlnSuccess(" Dropped course " + courseId + " successfully!");
        } catch (Exception ex) {
            printlnError(" Drop failed: " + ex.getMessage());
        }
    }

    // === PRINT HELPERS ===
    private static void printCourses(List<Course> courses) {
        System.out.println(CYAN + "\n--- Available Courses ---" + RESET);
        courses.forEach(c ->
                System.out.printf("%-10s | %-25s | Seats %d/%d%n",
                        c.getCourseId(), c.getTitle(),
                        c.getCurrentEnrolledCount(), c.getMaxSeats()));
    }

    private static void exitApp() {
        printlnInfo("Exiting... Goodbye!");
        System.exit(0);
    }

    // === COLOR PRINTS ===
    private static void printlnSuccess(String msg) { System.out.println(GREEN + msg + RESET); }
    private static void printlnError(String msg) { System.out.println(RED + msg + RESET); }
    private static void printlnWarn(String msg) { System.out.println(YELLOW + msg + RESET); }
    private static void printlnInfo(String msg) { System.out.println(CYAN + msg + RESET); }

    private static void printBanner() {
        System.out.println(CYAN + """
        ===================================================
                 STUDENT COURSE REGISTRATION SYSTEM
        ===================================================
        """ + RESET);
        printlnInfo("Click. Enroll. Learn — Seamless Registration, Smarter Learning .");
    }
}
