package com.courseevaluation.data;

import com.courseevaluation.models.Enrollment;
import com.courseevaluation.utils.FileUtil;
import com.courseevaluation.utils.DateTimeUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EnrollmentDatabase {
    private List<Enrollment> enrollments;
    private static final String ENROLLMENTS_FILE = "data/enrollments.txt";

    public EnrollmentDatabase() {
        this.enrollments = new ArrayList<>();
        loadEnrollments();
    }

    private void loadEnrollments() {
        List<String> lines = FileUtil.readLines(ENROLLMENTS_FILE);
        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts.length >= 4) {
                Enrollment enrollment = new Enrollment(
                    parts[0], // studentId
                    parts[1], // courseCode
                    parts[2], // enrollmentDate
                    parts[3]  // status
                );
                enrollments.add(enrollment);
            }
        }
    }

    public List<Enrollment> getAllEnrollments() {
        return new ArrayList<>(enrollments);
    }

    public List<Enrollment> getEnrollmentsByStudent(String studentId) {
        return enrollments.stream()
            .filter(enrollment -> enrollment.getStudentId().equals(studentId))
            .collect(Collectors.toList());
    }

    public List<Enrollment> getEnrollmentsByCourse(String courseCode) {
        return enrollments.stream()
            .filter(enrollment -> enrollment.getCourseCode().equals(courseCode))
            .collect(Collectors.toList());
    }

    public boolean addEnrollment(String studentId, String courseCode) {
        // Check if enrollment already exists
        if (enrollments.stream().anyMatch(e -> 
            e.getStudentId().equals(studentId) && e.getCourseCode().equals(courseCode))) {
            return false;
        }

        // Create new enrollment
        Enrollment enrollment = new Enrollment(
            studentId,
            courseCode,
            DateTimeUtil.getCurrentDate(),
            "ACTIVE"
        );
        enrollments.add(enrollment);
        saveEnrollments();
        return true;
    }

    public boolean updateEnrollmentStatus(String studentId, String courseCode, String newStatus) {
        for (int i = 0; i < enrollments.size(); i++) {
            Enrollment enrollment = enrollments.get(i);
            if (enrollment.getStudentId().equals(studentId) && 
                enrollment.getCourseCode().equals(courseCode)) {
                enrollments.set(i, new Enrollment(
                    studentId,
                    courseCode,
                    enrollment.getEnrollmentDate(),
                    newStatus
                ));
                saveEnrollments();
                return true;
            }
        }
        return false;
    }

    public boolean removeEnrollment(String studentId, String courseCode) {
        boolean removed = enrollments.removeIf(e -> 
            e.getStudentId().equals(studentId) && e.getCourseCode().equals(courseCode));
        if (removed) {
            saveEnrollments();
        }
        return removed;
    }

    private void saveEnrollments() {
        List<String> lines = enrollments.stream()
            .map(Enrollment::toString)
            .collect(Collectors.toList());
        FileUtil.writeLines(ENROLLMENTS_FILE, lines);
    }
} 