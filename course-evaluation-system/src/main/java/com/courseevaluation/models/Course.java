package com.courseevaluation.models;

import java.util.ArrayList;
import java.util.List;

public class Course {
    private String courseCode;
    private String title;
    private String instructor;
    private int credits;
    private String schedule;
    private String status;
    private int enrolledStudents;
    private int maxStudents;

    public Course(String courseCode, String title, String instructor, int credits, 
                 String schedule, int enrolledStudents, int maxStudents, String status) {
        this.courseCode = courseCode;
        this.title = title;
        this.instructor = instructor;
        this.credits = credits;
        this.schedule = schedule;
        this.enrolledStudents = enrolledStudents;
        this.maxStudents = maxStudents;
        this.status = status;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public String getTitle() {
        return title;
    }

    public String getInstructor() {
        return instructor;
    }

    public int getCredits() {
        return credits;
    }

    public String getSchedule() {
        return schedule;
    }

    public String getStatus() {
        return status;
    }

    public int getEnrolledStudents() {
        return enrolledStudents;
    }

    public int getMaxStudents() {
        return maxStudents;
    }

    public boolean isFull() {
        return enrolledStudents >= maxStudents;
    }

    @Override
    public String toString() {
        return String.format("%s,%s,%s,%d,%s,%d/%d,%s", 
            courseCode, title, instructor, credits, schedule, 
            enrolledStudents, maxStudents, status);
    }
} 