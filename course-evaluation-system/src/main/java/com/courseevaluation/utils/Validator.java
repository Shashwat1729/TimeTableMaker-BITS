package com.courseevaluation.utils;

public class Validator {
    public static boolean isValidUsername(String username) {
        return username != null && username.length() >= 3 && username.length() <= 20;
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }

    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(emailRegex);
    }

    public static boolean isValidName(String name) {
        return name != null && !name.trim().isEmpty();
    }

    public static boolean isValidStudentId(String studentId) {
        return studentId != null && studentId.matches("\\d{8}");
    }

    public static boolean isValidInstructorId(String instructorId) {
        return instructorId != null && instructorId.matches("\\d{6}");
    }

    public static boolean isValidAdminId(String adminId) {
        return adminId != null && adminId.matches("\\d{4}");
    }

    public static boolean isValidYear(int year) {
        return year >= 1 && year <= 4;
    }

    public static boolean isValidDepartment(String department) {
        return department != null && !department.trim().isEmpty();
    }

    public static boolean isValidTitle(String title) {
        return title != null && !title.trim().isEmpty();
    }

    public static boolean isValidMajor(String major) {
        return major != null && !major.trim().isEmpty();
    }

    public static boolean isValidSpecialization(String specialization) {
        return specialization != null && !specialization.trim().isEmpty();
    }

    public static boolean isValidAccessLevel(String accessLevel) {
        return accessLevel != null && 
               (accessLevel.equals("FULL") || accessLevel.equals("LIMITED"));
    }
} 