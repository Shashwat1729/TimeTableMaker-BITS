package com.courseevaluation;

import com.courseevaluation.models.Course;
import com.courseevaluation.models.Timetable;
import java.util.ArrayList;
import java.util.List;

public class TimetableTest {
    public static void main(String[] args) {
        List<Course> courses = new ArrayList<>();
        
        // Create test courses
        Course course1 = new Course("CS101", "Intro to Programming", "Dr. Smith", 3, "Monday 09:00-10:00", 30);
        Course course2 = new Course("CS102", "Data Structures", "Dr. Johnson", 3, "Tuesday 10:00-11:00", 30);
        Course course3 = new Course("CS103", "Algorithms", "Dr. Williams", 3, "Wednesday 11:00-12:00", 30);
        Course course4 = new Course("CS104", "Database Systems", "Dr. Brown", 3, "Thursday 13:00-14:00", 30);
        
        courses.add(course1);
        courses.add(course2);
        courses.add(course3);
        courses.add(course4);
        
        // Create a list of course preferences
        List<String> preferences = new ArrayList<>();
        preferences.add("CS101");
        preferences.add("CS102");
        preferences.add("CS103");
        preferences.add("CS104");
        
        // Generate multiple timetables
        List<Timetable> timetables = Timetable.generateMultipleTimetables(courses, preferences, 3);
        
        // Print the generated timetables
        for (int i = 0; i < timetables.size(); i++) {
            System.out.println("\nTimetable Option " + (i + 1) + ":");
            System.out.println(timetables.get(i));
        }
    }
} 