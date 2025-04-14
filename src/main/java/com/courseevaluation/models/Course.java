package com.courseevaluation.models;

public class Course {
    private String courseCode;
    private String title;
    private String instructor;
    private int credits;
    private String schedule;
    private int enrolledStudents;
    private int maxStudents;
    private String status;

    public Course(String courseCode, String title, String instructor, int credits, 
                 String schedule, int maxStudents) {
        this.courseCode = courseCode;
        this.title = title;
        this.instructor = instructor;
        this.credits = credits;
        this.schedule = schedule;
        this.enrolledStudents = 0;
        this.maxStudents = maxStudents;
        this.status = "OPEN";
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

    public int getEnrolledStudents() {
        return enrolledStudents;
    }

    public void setEnrolledStudents(int enrolledStudents) {
        this.enrolledStudents = enrolledStudents;
        if (this.enrolledStudents >= this.maxStudents) {
            this.status = "FULL";
        } else {
            this.status = "OPEN";
        }
    }

    public int getMaxStudents() {
        return maxStudents;
    }

    public String getStatus() {
        return status;
    }

    public boolean isFull() {
        return enrolledStudents >= maxStudents;
    }

    @Override
    public String toString() {
        return String.format("%s - %s (%d/%d)", courseCode, title, enrolledStudents, maxStudents);
    }
} 