package com.courseevaluation.models;

import java.util.ArrayList;
import java.util.List;

public class Student extends User {
    private String studentId;
    private String major;
    private int year;
    private List<Course> enrolledCourses;

    public Student(String username, String password, String name, String department,
                  String studentId, String major, int year) {
        super(username, password, "STUDENT", name, department);
        this.studentId = studentId;
        this.major = major;
        this.year = year;
        this.enrolledCourses = new ArrayList<>();
    }

    public String getStudentId() {
        return studentId;
    }

    public String getMajor() {
        return major;
    }

    public int getYear() {
        return year;
    }

    public List<Course> getEnrolledCourses() {
        return enrolledCourses;
    }

    public void enrollInCourse(Course course) {
        if (!enrolledCourses.contains(course)) {
            enrolledCourses.add(course);
        }
    }

    public void dropCourse(Course course) {
        enrolledCourses.remove(course);
    }

    @Override
    public String toString() {
        return String.format("STUDENT,%s,%s,%s,%s,%s,%s,%d",
            getUsername(), getPassword(), getName(), getDepartment(), studentId, major, year);
    }
} 