package com.courseevaluation.data;

import com.courseevaluation.models.Enrollment;
import com.courseevaluation.models.Student;
import com.courseevaluation.models.Course;
import com.courseevaluation.utils.DateTimeUtil;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EnrollmentDatabase {
    private static final String FILE_PATH = "data/enrollments.csv";
    private List<Enrollment> enrollments;
    private CourseDatabase courseDatabase;

    public EnrollmentDatabase(CourseDatabase courseDatabase) {
        this.enrollments = new ArrayList<>();
        this.courseDatabase = courseDatabase;
        loadEnrollments();
    }

    private void loadEnrollments() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    enrollments.add(new Enrollment(
                        parts[0],  // studentUsername
                        parts[1],  // courseCode
                        parts[2],  // enrollmentDate
                        parts[3]   // status
                    ));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveEnrollments() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH))) {
            for (Enrollment enrollment : enrollments) {
                writer.println(enrollment.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addEnrollment(Enrollment enrollment) {
        enrollments.add(enrollment);
        saveEnrollments();
    }

    public void removeEnrollment(Enrollment enrollment) {
        enrollments.removeIf(e -> 
            e.getStudentUsername().equals(enrollment.getStudentUsername()) && 
            e.getCourseCode().equals(enrollment.getCourseCode()));
        saveEnrollments();
    }

    public List<Enrollment> getEnrollmentsByStudent(String studentUsername) {
        return enrollments.stream()
            .filter(e -> e.getStudentUsername().equals(studentUsername))
            .collect(Collectors.toList());
    }

    public List<Enrollment> getEnrollmentsByCourse(String courseCode) {
        return enrollments.stream()
            .filter(e -> e.getCourseCode().equals(courseCode))
            .collect(Collectors.toList());
    }

    public List<Enrollment> getAllEnrollments() {
        return new ArrayList<>(enrollments);
    }

    public List<Course> getEnrolledCourses(Student student) {
        return getEnrollmentsByStudent(student.getUsername()).stream()
            .map(enrollment -> courseDatabase.findCourse(enrollment.getCourseCode()))
            .filter(course -> course != null)
            .collect(Collectors.toList());
    }

    public boolean isEnrolled(Student student, Course course) {
        return enrollments.stream()
            .anyMatch(e -> e.getStudentUsername().equals(student.getUsername()) &&
                         e.getCourseCode().equals(course.getCourseCode()));
    }

    public void enrollStudent(Student student, Course course) {
        if (!isEnrolled(student, course)) {
            Enrollment enrollment = new Enrollment(
                student.getUsername(),
                course.getCourseCode(),
                DateTimeUtil.getCurrentDate(),
                "ENROLLED"
            );
            addEnrollment(enrollment);
            course.setEnrolledStudents(course.getEnrolledStudents() + 1);
            courseDatabase.saveCourses();
        }
    }

    public void dropCourse(Student student, Course course) {
        Enrollment enrollment = new Enrollment(
            student.getUsername(),
            course.getCourseCode(),
            DateTimeUtil.getCurrentDate(),
            "DROPPED"
        );
        removeEnrollment(enrollment);
        course.setEnrolledStudents(course.getEnrolledStudents() - 1);
        courseDatabase.saveCourses();
    }
} 