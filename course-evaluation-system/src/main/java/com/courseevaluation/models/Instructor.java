package com.courseevaluation.models;

import java.util.ArrayList;
import java.util.List;

public class Instructor extends User {
    private String instructorId;
    private String specialization;
    private List<Course> teachingCourses;

    public Instructor(String username, String password, String name, String department,
                     String instructorId, String specialization) {
        super(username, password, "INSTRUCTOR", name, department);
        this.instructorId = instructorId;
        this.specialization = specialization;
        this.teachingCourses = new ArrayList<>();
    }

    public String getInstructorId() {
        return instructorId;
    }

    public String getSpecialization() {
        return specialization;
    }

    public List<Course> getTeachingCourses() {
        return teachingCourses;
    }

    public void addCourse(Course course) {
        if (!teachingCourses.contains(course)) {
            teachingCourses.add(course);
        }
    }

    public void removeCourse(Course course) {
        teachingCourses.remove(course);
    }

    @Override
    public String toString() {
        return String.format("INSTRUCTOR,%s,%s,%s,%s,%s,%s",
            getUsername(), getPassword(), getName(), getDepartment(), instructorId, specialization);
    }
} 