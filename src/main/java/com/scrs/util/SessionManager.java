package com.scrs.util;

public class SessionManager {
    private static String currentStudentId;
    public static void login(String studentId) { currentStudentId = studentId; }
    public static void logout() { currentStudentId = null; }
    public static String getCurrentStudentId() { return currentStudentId; }
    public static boolean isLoggedIn() { return currentStudentId != null; }
}
