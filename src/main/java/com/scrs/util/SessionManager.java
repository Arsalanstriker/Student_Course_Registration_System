package com.scrs.util;

import com.scrs.model.Student;

public class SessionManager {
    private static Student currentStudent;

    public static void login(Student student) {
        currentStudent = student;
        System.out.println("Logged in as " + student.getName());
    }

    public static Student getCurrentStudent() {
        return currentStudent;
    }

    public static void logout() {
        System.out.println("Logged out " +
                (currentStudent != null ? currentStudent.getName() : "No user"));
        currentStudent = null;
    }
}
