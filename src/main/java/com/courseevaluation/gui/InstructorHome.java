package com.courseevaluation.gui;

import com.courseevaluation.models.*;
import com.courseevaluation.data.*;
import com.courseevaluation.utils.DateTimeUtil;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InstructorHome extends JFrame {
    private UserDatabase userDatabase;
    private CourseDatabase courseDatabase;
    private EnrollmentDatabase enrollmentDatabase;
    private Instructor instructor;
    private DefaultTableModel tableModel;
    private JTable courseTable;

    public InstructorHome(UserDatabase userDatabase, CourseDatabase courseDatabase, 
                         EnrollmentDatabase enrollmentDatabase, Instructor instructor) {
        this.userDatabase = userDatabase;
        this.courseDatabase = courseDatabase;
        this.enrollmentDatabase = enrollmentDatabase;
        this.instructor = instructor;

        setTitle("Instructor Dashboard - " + instructor.getUsername());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);

        initializeComponents();
        loadAssignedCourses();
        setVisible(true);
    }

    private void initializeComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top panel with buttons
        JPanel topPanel = new JPanel(new BorderLayout());
        
        // Left side buttons
        JPanel leftButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton viewStudentsButton = new JButton("View Students");
        JButton viewScheduleButton = new JButton("View Schedule");
        
        viewStudentsButton.addActionListener(e -> showStudentsDialog());
        viewScheduleButton.addActionListener(e -> showScheduleDialog());
        
        leftButtons.add(viewStudentsButton);
        leftButtons.add(viewScheduleButton);
        topPanel.add(leftButtons, BorderLayout.WEST);

        // Right side logout button
        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION);
                
            if (confirm == JOptionPane.YES_OPTION) {
                this.dispose();
                new LoginPage(userDatabase, courseDatabase, enrollmentDatabase);
            }
        });
        logoutPanel.add(logoutButton);
        topPanel.add(logoutPanel, BorderLayout.EAST);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Course table
        String[] columnNames = {"Course Code", "Title", "Credits", "Students Enrolled"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        courseTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(courseTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Bottom panel for status
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel statusLabel = new JLabel("Last updated: " + DateTimeUtil.getCurrentDateTime());
        bottomPanel.add(statusLabel);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void showStudentsDialog() {
        // Course ke students ko dikhane ke liye function hai
        int selectedRow = courseTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a course to view its students.",
                "No Course Selected",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        String courseCode = (String) tableModel.getValueAt(selectedRow, 0);
        List<Enrollment> enrollments = enrollmentDatabase.getEnrollmentsByCourse(courseCode);
        
        JDialog dialog = new JDialog(this, "Students in " + courseCode, true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);

        String[] columnNames = {"Student ID", "Name", "Status"};
        DefaultTableModel studentModel = new DefaultTableModel(columnNames, 0);
        JTable studentTable = new JTable(studentModel);
        JScrollPane scrollPane = new JScrollPane(studentTable);

        for (Enrollment enrollment : enrollments) {
            User student = userDatabase.findUser(enrollment.getStudentUsername());
            if (student != null) {
                studentModel.addRow(new Object[]{
                    student.getUsername(),
                    student.getName(),
                    enrollment.getStatus()
                });
            }
        }

        dialog.add(scrollPane, BorderLayout.CENTER);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(closeButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void showScheduleDialog() {
        List<Course> assignedCourses = courseDatabase.getAllCourses().stream()
            .filter(course -> course.getInstructor().equals(instructor.getUsername()))
            .collect(Collectors.toList());

        if (assignedCourses.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "No courses assigned to you.",
                "No Courses",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog(this, "Teaching Schedule", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(800, 600);
        dialog.setLocationRelativeTo(this);

        TimetablePanel timetablePanel = new TimetablePanel(assignedCourses);
        dialog.add(timetablePanel, BorderLayout.CENTER);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(closeButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void loadAssignedCourses() {
        tableModel.setRowCount(0);
        List<Course> assignedCourses = courseDatabase.getAllCourses().stream()
            .filter(course -> course.getInstructor().equals(instructor.getUsername()))
            .collect(Collectors.toList());

        for (Course course : assignedCourses) {
            List<Enrollment> enrollments = enrollmentDatabase.getEnrollmentsByCourse(course.getCourseCode());
            int enrolledCount = enrollments.size();
            tableModel.addRow(new Object[]{
                course.getCourseCode(),
                course.getTitle(),
                course.getCredits(),
                enrolledCount
            });
        }
    }
} 