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

public class InstructorHome extends JFrame {
    private User instructor;
    private UserDatabase userDatabase;
    private CourseDatabase courseDatabase;
    private EnrollmentDatabase enrollmentDatabase;
    private JTable courseTable;
    private JTable studentTable;
    private DefaultTableModel courseTableModel;
    private DefaultTableModel studentTableModel;

    public InstructorHome(User instructor, UserDatabase userDatabase, CourseDatabase courseDatabase, 
                         EnrollmentDatabase enrollmentDatabase) {
        this.instructor = instructor;
        this.userDatabase = userDatabase;
        this.courseDatabase = courseDatabase;
        this.enrollmentDatabase = enrollmentDatabase;

        setTitle("Instructor Dashboard - " + instructor.getUsername());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        initializeComponents();
        loadCourses();
        setVisible(true);
    }

    private void initializeComponents() {
        // Create main panel with split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(300);

        // Top panel for courses
        JPanel coursePanel = new JPanel(new BorderLayout(10, 10));
        coursePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Add logout button to the top right
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
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
        topPanel.add(logoutButton);
        coursePanel.add(topPanel, BorderLayout.NORTH);

        // Course table
        String[] courseColumns = {"Course Code", "Title", "Credits", "Schedule", "Enrolled/Max", "Status"};
        courseTableModel = new DefaultTableModel(courseColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        courseTable = new JTable(courseTableModel);
        courseTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadStudents();
            }
        });
        JScrollPane courseScrollPane = new JScrollPane(courseTable);
        
        // Create a panel for the course label
        JPanel courseLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        courseLabelPanel.add(new JLabel("Your Courses"));
        coursePanel.add(courseLabelPanel, BorderLayout.CENTER);
        coursePanel.add(courseScrollPane, BorderLayout.SOUTH);

        // Bottom panel for students
        JPanel studentPanel = new JPanel(new BorderLayout(10, 10));
        studentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Student table
        String[] studentColumns = {"Student ID", "Name", "Department", "Enrollment Date", "Status"};
        studentTableModel = new DefaultTableModel(studentColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        studentTable = new JTable(studentTableModel);
        JScrollPane studentScrollPane = new JScrollPane(studentTable);
        studentPanel.add(new JLabel("Enrolled Students"), BorderLayout.NORTH);
        studentPanel.add(studentScrollPane, BorderLayout.CENTER);

        // Add panels to split pane
        splitPane.setTopComponent(coursePanel);
        splitPane.setBottomComponent(studentPanel);

        // Add split pane to frame
        add(splitPane);

        // Status bar
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel statusLabel = new JLabel("Last updated: " + DateTimeUtil.getCurrentDateTime());
        statusBar.add(statusLabel);
        add(statusBar, BorderLayout.SOUTH);
    }

    private void loadCourses() {
        courseTableModel.setRowCount(0);
        List<Course> courses = courseDatabase.getCoursesByInstructor(instructor.getUsername());
        
        for (Course course : courses) {
            courseTableModel.addRow(new Object[]{
                course.getCourseCode(),
                course.getTitle(),
                course.getCredits(),
                course.getSchedule(),
                course.getEnrolledStudents() + "/" + course.getMaxStudents(),
                course.getStatus()
            });
        }
    }

    private void loadStudents() {
        studentTableModel.setRowCount(0);
        int selectedRow = courseTable.getSelectedRow();
        
        if (selectedRow >= 0) {
            String courseCode = (String) courseTable.getValueAt(selectedRow, 0);
            List<Enrollment> enrollments = enrollmentDatabase.getEnrollmentsByCourse(courseCode);
            
            for (Enrollment enrollment : enrollments) {
                studentTableModel.addRow(new Object[]{
                    enrollment.getStudentId(),
                    "N/A", // Would need user database to get name
                    "N/A", // Would need user database to get department
                    enrollment.getEnrollmentDate(),
                    enrollment.getStatus()
                });
            }
        }
    }
} 