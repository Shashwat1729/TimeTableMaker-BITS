package com.courseevaluation.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import com.courseevaluation.data.UserDatabase;
import com.courseevaluation.data.CourseDatabase;
import com.courseevaluation.data.EnrollmentDatabase;
import com.courseevaluation.models.*;
import com.courseevaluation.utils.DateTimeUtil;

public class StudentHome extends JFrame {
    private User student;
    private UserDatabase userDatabase;
    private CourseDatabase courseDatabase;
    private EnrollmentDatabase enrollmentDatabase;
    private JTable courseTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;

    public StudentHome(User student, CourseDatabase courseDatabase, 
                      EnrollmentDatabase enrollmentDatabase) {
        this.student = student;
        this.userDatabase = new UserDatabase();
        this.courseDatabase = courseDatabase;
        this.enrollmentDatabase = enrollmentDatabase;

        setTitle("Student Dashboard - " + student.getUsername());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);

        initializeComponents();
        loadEnrolledCourses();
        setVisible(true);
    }

    private void initializeComponents() {
        // Create main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Top panel for search and add course
        JPanel topPanel = new JPanel(new BorderLayout(10, 0));
        
        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> filterCourses());
        searchPanel.add(new JLabel("Search: "));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        topPanel.add(searchPanel, BorderLayout.WEST);

        // Add course button
        JButton addCourseButton = new JButton("Add New Course");
        addCourseButton.addActionListener(e -> showAddCourseDialog());
        topPanel.add(addCourseButton, BorderLayout.CENTER);

        // Add logout button to the right side of the top panel
        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION);
                
            if (confirm == JOptionPane.YES_OPTION) {
                this.dispose();
                SwingUtilities.invokeLater(() -> {
                    LoginPage loginPage = new LoginPage(userDatabase, courseDatabase, enrollmentDatabase);
                    loginPage.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    loginPage.setSize(600, 400);
                    loginPage.setLocationRelativeTo(null);
                    loginPage.setVisible(true);
                });
            }
        });
        logoutPanel.add(logoutButton);
        topPanel.add(logoutPanel, BorderLayout.EAST);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Course table
        String[] columnNames = {"Course Code", "Title", "Instructor", "Credits", "Status"};
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

    private void loadEnrolledCourses() {
        tableModel.setRowCount(0);
        List<Enrollment> enrollments = enrollmentDatabase.getEnrollmentsByStudent(student.getUsername());
        
        for (Enrollment enrollment : enrollments) {
            Course course = courseDatabase.getCourseByCode(enrollment.getCourseCode());
            if (course != null) {
                tableModel.addRow(new Object[]{
                    course.getCourseCode(),
                    course.getTitle(),
                    course.getInstructor(),
                    course.getCredits(),
                    enrollment.getStatus()
                });
            }
        }
    }

    private void filterCourses() {
        String searchText = searchField.getText().toLowerCase();
        tableModel.setRowCount(0);
        
        List<Enrollment> enrollments = enrollmentDatabase.getEnrollmentsByStudent(student.getUsername());
        List<Course> filteredCourses = enrollments.stream()
            .map(e -> courseDatabase.getCourseByCode(e.getCourseCode()))
            .filter(c -> c != null && (
                c.getCourseCode().toLowerCase().contains(searchText) ||
                c.getTitle().toLowerCase().contains(searchText)
            ))
            .collect(Collectors.toList());

        for (Course course : filteredCourses) {
            Enrollment enrollment = enrollments.stream()
                .filter(e -> e.getCourseCode().equals(course.getCourseCode()))
                .findFirst()
                .orElse(null);

            if (enrollment != null) {
                tableModel.addRow(new Object[]{
                    course.getCourseCode(),
                    course.getTitle(),
                    course.getInstructor(),
                    course.getCredits(),
                    enrollment.getStatus()
                });
            }
        }
    }

    private void showAddCourseDialog() {
        JDialog dialog = new JDialog(this, "Add New Course", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        // Create table model for available courses
        String[] columnNames = {"Course Code", "Title", "Instructor", "Credits", "Status"};
        DefaultTableModel availableCoursesModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable availableCoursesTable = new JTable(availableCoursesModel);
        JScrollPane scrollPane = new JScrollPane(availableCoursesTable);

        // Load available courses (not enrolled)
        List<String> enrolledCourseCodes = enrollmentDatabase.getEnrollmentsByStudent(student.getUsername())
            .stream()
            .map(Enrollment::getCourseCode)
            .collect(Collectors.toList());

        courseDatabase.getAllCourses().stream()
            .filter(course -> !enrolledCourseCodes.contains(course.getCourseCode()))
            .forEach(course -> availableCoursesModel.addRow(new Object[]{
                course.getCourseCode(),
                course.getTitle(),
                course.getInstructor(),
                course.getCredits(),
                course.getStatus()
            }));

        dialog.add(scrollPane, BorderLayout.CENTER);

        // Add buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton enrollButton = new JButton("Enroll");
        JButton cancelButton = new JButton("Cancel");

        enrollButton.addActionListener(e -> {
            int selectedRow = availableCoursesTable.getSelectedRow();
            if (selectedRow >= 0) {
                String courseCode = (String) availableCoursesTable.getValueAt(selectedRow, 0);
                if (enrollmentDatabase.addEnrollment(student.getUsername(), courseCode)) {
                    loadEnrolledCourses();
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog,
                        "Failed to enroll in the course",
                        "Enrollment Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(dialog,
                    "Please select a course to enroll",
                    "Selection Required",
                    JOptionPane.WARNING_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(enrollButton);
        buttonPanel.add(cancelButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }
} 