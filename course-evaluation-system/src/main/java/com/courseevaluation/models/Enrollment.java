package com.courseevaluation.models;

import java.time.LocalDateTime;

public class Enrollment {
    private String studentId;
    private String courseCode;
    private String enrollmentDate;
    private String status;

    public Enrollment(String studentId, String courseCode, String enrollmentDate, String status) {
        this.studentId = studentId;
        this.courseCode = courseCode;
        this.enrollmentDate = enrollmentDate;
        this.status = status;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public String getEnrollmentDate() {
        return enrollmentDate;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return String.format("%s,%s,%s,%s", studentId, courseCode, enrollmentDate, status);
    }
} 