package com.courseevaluation.data;

import com.courseevaluation.models.Course;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CourseDatabase {
    private static final String COURSE_FILE = "data/courses.csv";
    private List<Course> courses;

    public CourseDatabase() {
        courses = new ArrayList<>();
        loadCourses();
    }

    private void loadCourses() {
        try (BufferedReader reader = new BufferedReader(new FileReader(COURSE_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                Course course = new Course(
                    parts[0],  // courseCode
                    parts[1],  // title
                    parts[3],  // instructor
                    Integer.parseInt(parts[2]),  // credits
                    parts[4],  // schedule
                    Integer.parseInt(parts[6])   // maxStudents
                );
                course.setEnrolledStudents(Integer.parseInt(parts[5]));
                courses.add(course);
            }
        } catch (IOException e) {
            System.err.println("Error loading courses: " + e.getMessage());
        }
    }

    public void saveCourses() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(COURSE_FILE))) {
            for (Course course : courses) {
                writer.println(course.toString());
            }
        } catch (IOException e) {
            System.err.println("Error saving courses: " + e.getMessage());
        }
    }

    public Course findCourse(String courseCode) {
        for (Course course : courses) {
            if (course.getCourseCode().equals(courseCode)) {
                return course;
            }
        }
        return null;
    }

    public void addCourse(Course course) {
        courses.add(course);
        saveCourses();
    }

    public List<Course> getAllCourses() {
        return new ArrayList<>(courses);
    }

    public List<Course> getCoursesByInstructor(String instructorId) {
        List<Course> instructorCourses = new ArrayList<>();
        for (Course course : courses) {
            if (course.getInstructor().equals(instructorId)) {
                instructorCourses.add(course);
            }
        }
        return instructorCourses;
    }

    public Course getCourseByCode(String courseCode) {
        return findCourse(courseCode);
    }
} 