package com.courseevaluation.data;

import com.courseevaluation.models.Course;
import com.courseevaluation.utils.FileUtil;
import com.courseevaluation.utils.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CourseDatabase {
    private List<Course> courses;
    private static final String COURSES_FILE = "data/courses.txt";

    public CourseDatabase() {
        this.courses = new ArrayList<>();
        loadCourses();
    }

    private void loadCourses() {
        List<String> lines = FileUtil.readLines(COURSES_FILE);
        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts.length >= 7) {
                Course course = new Course(
                    parts[0], // courseCode
                    parts[1], // title
                    parts[2], // instructor
                    Integer.parseInt(parts[3]), // credits
                    parts[4], // schedule
                    Integer.parseInt(parts[5].split("/")[0]), // enrolledStudents
                    Integer.parseInt(parts[5].split("/")[1]), // maxStudents
                    parts[6]  // status
                );
                courses.add(course);
            }
        }
    }

    public List<Course> getAllCourses() {
        return new ArrayList<>(courses);
    }

    public List<Course> getCoursesByInstructor(String instructorUsername) {
        return courses.stream()
                .filter(course -> course.getInstructor().equals(instructorUsername))
                .collect(Collectors.toList());
    }

    public Course getCourseByCode(String courseCode) {
        return courses.stream()
            .filter(course -> course.getCourseCode().equals(courseCode))
            .findFirst()
            .orElse(null);
    }

    public boolean addCourse(Course course) {
        if (getCourseByCode(course.getCourseCode()) != null) {
            return false;
        }
        courses.add(course);
        saveCourses();
        return true;
    }

    public boolean updateCourse(Course updatedCourse) {
        for (int i = 0; i < courses.size(); i++) {
            if (courses.get(i).getCourseCode().equals(updatedCourse.getCourseCode())) {
                courses.set(i, updatedCourse);
                saveCourses();
                return true;
            }
        }
        return false;
    }

    public boolean deleteCourse(String courseCode) {
        boolean removed = courses.removeIf(course -> course.getCourseCode().equals(courseCode));
        if (removed) {
            saveCourses();
        }
        return removed;
    }

    private void saveCourses() {
        List<String> lines = courses.stream()
            .map(course -> String.format("%s,%s,%s,%d,%s,%d/%d,%s",
                course.getCourseCode(),
                course.getTitle(),
                course.getInstructor(),
                course.getCredits(),
                course.getSchedule(),
                course.getEnrolledStudents(),
                course.getMaxStudents(),
                course.getStatus()))
            .collect(Collectors.toList());
        FileUtil.writeLines(COURSES_FILE, lines);
    }
} 