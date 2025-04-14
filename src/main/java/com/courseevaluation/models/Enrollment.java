package com.courseevaluation.models;

public class Enrollment {
    private String studentUsername;
    private String courseCode;
    private String enrollmentDate;
    private String status;

    public Enrollment(String studentUsername, String courseCode, String enrollmentDate, String status) {
        this.studentUsername = studentUsername;
        this.courseCode = courseCode;
        this.enrollmentDate = enrollmentDate;
        this.status = status;
    }

    public String getStudentUsername() {
        return studentUsername;
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

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return String.format("%s,%s,%s,%s", studentUsername, courseCode, enrollmentDate, status);
    }
} 