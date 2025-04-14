package com.courseevaluation.gui;

import com.courseevaluation.data.UserDatabase;
import com.courseevaluation.data.CourseDatabase;
import com.courseevaluation.data.EnrollmentDatabase;
import com.courseevaluation.models.Student;
import com.courseevaluation.models.Course;
import com.courseevaluation.models.Enrollment;
import com.courseevaluation.models.Timetable;
import com.courseevaluation.models.TimeSlot;
import com.courseevaluation.utils.DateTimeUtil;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Collections;

public class StudentHome extends JFrame {
    private final Student student;
    private final CourseDatabase courseDatabase;
    private final EnrollmentDatabase enrollmentDatabase;
    private final UserDatabase userDatabase;
    
    private JTable courseTable;
    private DefaultTableModel courseTableModel;
    private JLabel statusLabel;
    private List<Course> enrolledCourses;
    private List<Course> availableCourses;
    private JPanel timetablePanel;

    public StudentHome(Student student, CourseDatabase courseDatabase, EnrollmentDatabase enrollmentDatabase, UserDatabase userDatabase) {
        this.student = student;
        this.courseDatabase = courseDatabase;
        this.enrollmentDatabase = enrollmentDatabase;
        this.userDatabase = userDatabase;
        this.enrolledCourses = new ArrayList<>();
        this.availableCourses = new ArrayList<>();
        this.timetablePanel = new JPanel();
        
        initializeUI();
        loadEnrolledCourses();
    }

    private void initializeUI() {
        setTitle("Student Dashboard - " + student.getUsername());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);

        initializeComponents();
        setVisible(true);
    }

    private void initializeComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top panel with title and search
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        
        // Title panel with modern styling
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel titleLabel = new JLabel("Student Dashboard");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(70, 130, 180));
        titlePanel.add(titleLabel);
        
        // Add student info
        JLabel studentInfo = new JLabel(" - " + student.getName() + " (" + student.getStudentId() + ")");
        studentInfo.setFont(new Font("Arial", Font.PLAIN, 16));
        titlePanel.add(studentInfo);
        
        topPanel.add(titlePanel, BorderLayout.WEST);
        
        // Search panel with modern styling
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel searchLabel = new JLabel("Search: ");
        JTextField searchField = new JTextField(20);
        searchField.setPreferredSize(new Dimension(200, 30));
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        topPanel.add(searchPanel, BorderLayout.EAST);
        
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Button panel
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.NORTH);

        // Create split pane for enrolled courses and available courses
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.5);

        // Enrolled courses table
        JPanel enrolledPanel = new JPanel(new BorderLayout());
        enrolledPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(70, 130, 180), 2),
            "Enrolled Courses",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14),
            new Color(70, 130, 180)
        ));

        String[] columns = {"Course Code", "Title", "Instructor", "Credits", "Schedule", "Status"};
        courseTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        courseTable = new JTable(courseTableModel);
        styleTable(courseTable);
        
        JScrollPane enrolledScrollPane = new JScrollPane(courseTable);
        enrolledPanel.add(enrolledScrollPane, BorderLayout.CENTER);
        
        // Available courses table
        JPanel availablePanel = new JPanel(new BorderLayout());
        availablePanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(70, 130, 180), 2),
            "Available Courses",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14),
            new Color(70, 130, 180)
        ));

        DefaultTableModel availableTableModel = new DefaultTableModel(
            new String[]{"Course Code", "Title", "Instructor", "Credits", "Schedule", "Available Seats"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable availableTable = new JTable(availableTableModel);
        styleTable(availableTable);
        
        JScrollPane availableScrollPane = new JScrollPane(availableTable);
        availablePanel.add(availableScrollPane, BorderLayout.CENTER);

        splitPane.setTopComponent(enrolledPanel);
        splitPane.setBottomComponent(availablePanel);
        
        mainPanel.add(splitPane, BorderLayout.CENTER);

        // Status bar with modern styling
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));
        statusLabel = new JLabel("Last updated: " + DateTimeUtil.getCurrentDateTime());
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statusBar.add(statusLabel);
        mainPanel.add(statusBar, BorderLayout.SOUTH);

        add(mainPanel);
        
        // Load initial data
        loadEnrolledCourses();
        loadAvailableCourses(availableTableModel);
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setFocusPainted(false);
        return button;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        
        JButton addButton = createButton("Add Course");
        JButton dropButton = createButton("Drop Course");
        JButton viewButton = createButton("View Schedule");
        JButton autoGenButton = createButton("Auto-Generate Timetable");
        JButton refreshButton = createButton("Refresh");
        JButton logoutButton = createButton("Logout");
        
        // Style the buttons with modern look
        Color primaryColor = new Color(70, 130, 180); // Steel Blue
        Color logoutColor = new Color(220, 53, 69);   // Red color for logout
        Font buttonFont = new Font("Arial", Font.PLAIN, 14);
        
        // Style regular buttons
        for (JButton button : new JButton[]{addButton, dropButton, viewButton, autoGenButton, refreshButton}) {
            button.setFont(buttonFont);
            button.setBackground(primaryColor);
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);
            button.setBorderPainted(false);
            button.setOpaque(true);
        }
        
        // Style logout button differently
        logoutButton.setFont(buttonFont);
        logoutButton.setBackground(logoutColor);
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setBorderPainted(false);
        logoutButton.setOpaque(true);
        
        addButton.addActionListener(e -> showAddCourseDialog());
        dropButton.addActionListener(e -> dropSelectedCourse());
        viewButton.addActionListener(e -> showScheduleDialog());
        autoGenButton.addActionListener(e -> autoGenerateTimetable());
        refreshButton.addActionListener(e -> refreshCourseTable());
        logoutButton.addActionListener(e -> handleLogout());
        
        buttonPanel.add(addButton);
        buttonPanel.add(dropButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(autoGenButton);
        buttonPanel.add(refreshButton);
        
        // Add logout button to the right side
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.add(logoutButton);
        buttonPanel.add(rightPanel);
        
        return buttonPanel;
    }

    private void styleTable(JTable table) {
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.setSelectionBackground(new Color(70, 130, 180, 50));
        table.setGridColor(Color.LIGHT_GRAY);
        table.setShowVerticalLines(true);
        table.setShowHorizontalLines(true);
    }

    private void loadEnrolledCourses() {
        courseTableModel.setRowCount(0);
        enrolledCourses = enrollmentDatabase.getEnrolledCourses(student);
        
        for (Course course : enrolledCourses) {
            courseTableModel.addRow(new Object[]{
                course.getCourseCode(),
                course.getTitle(),
                course.getInstructor(),
                course.getCredits(),
                course.getSchedule(),
                course.getStatus()
            });
        }
        
        statusLabel.setText("Last updated: " + DateTimeUtil.getCurrentDateTime());
    }

    private void loadAvailableCourses(DefaultTableModel model) {
        model.setRowCount(0);
        List<Course> availableCourses = courseDatabase.getAllCourses().stream()
            .filter(course -> !enrollmentDatabase.isEnrolled(student, course))
            .filter(course -> !course.isFull())
            .toList();
            
        for (Course course : availableCourses) {
            model.addRow(new Object[]{
                course.getCourseCode(),
                course.getTitle(),
                course.getInstructor(),
                course.getCredits(),
                course.getSchedule(),
                course.getMaxStudents() - course.getEnrolledStudents()
            });
        }
    }

    private void showAddCourseDialog() {
        // Ye function student ko course add karne ke liye hai
        JDialog dialog = new JDialog(this, "Add Course", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(700, 500);
        dialog.setLocationRelativeTo(this);

        // Create table model for available courses
        String[] columns = {"Course Code", "Title", "Instructor", "Credits", "Schedule", "Available Seats", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable courseTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(courseTable);

        // Load available courses
        availableCourses = courseDatabase.getAllCourses().stream()
            .filter(course -> !enrollmentDatabase.isEnrolled(student, course))
            .filter(course -> !course.isFull())
            .toList();

        for (Course course : availableCourses) {
            model.addRow(new Object[]{
                course.getCourseCode(),
                course.getTitle(),
                course.getInstructor(),
                course.getCredits(),
                course.getSchedule(),
                course.getMaxStudents() - course.getEnrolledStudents(),
                course.getStatus()
            });
        }

        // Add search field
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel searchLabel = new JLabel("Search: ");
        JTextField searchField = new JTextField(20);
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        
        // Add row sorter for search functionality
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        courseTable.setRowSorter(sorter);
        
        // Add search field listener
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { search(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { search(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { search(); }
            
            public void search() {
                String text = searchField.getText();
                if (text.trim().length() == 0) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }
        });

        // Add buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton enrollButton = createButton("Enroll");
        JButton cancelButton = createButton("Cancel");
        JButton infoButton = createButton("Course Info");

        enrollButton.addActionListener(e -> {
            int selectedRow = courseTable.getSelectedRow();
            if (selectedRow >= 0) {
                selectedRow = courseTable.convertRowIndexToModel(selectedRow);
                String courseCode = (String) model.getValueAt(selectedRow, 0);
                Course selectedCourse = courseDatabase.getCourseByCode(courseCode);
                
                if (selectedCourse != null) {
                    // Check for schedule conflicts
                    if (hasScheduleConflict(selectedCourse)) {
                        JOptionPane.showMessageDialog(dialog,
                            "This course conflicts with your current schedule.",
                            "Schedule Conflict",
                            JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    // Enroll the student
                    enrollmentDatabase.enrollStudent(student, selectedCourse);
                    selectedCourse.setEnrolledStudents(selectedCourse.getEnrolledStudents() + 1);
                    courseDatabase.saveCourses();
                    
                    loadEnrolledCourses();
                    statusLabel.setText("Enrolled in " + courseCode + ". Last updated: " + DateTimeUtil.getCurrentDateTime());
                    dialog.dispose();
                }
            } else {
                JOptionPane.showMessageDialog(dialog,
                    "Please select a course to enroll.",
                    "No Course Selected",
                    JOptionPane.WARNING_MESSAGE);
            }
        });

        infoButton.addActionListener(e -> {
            int selectedRow = courseTable.getSelectedRow();
            if (selectedRow >= 0) {
                selectedRow = courseTable.convertRowIndexToModel(selectedRow);
                String courseCode = (String) model.getValueAt(selectedRow, 0);
                Course selectedCourse = courseDatabase.getCourseByCode(courseCode);
                
                if (selectedCourse != null) {
                    showCourseInfoDialog(selectedCourse);
                }
            } else {
                JOptionPane.showMessageDialog(dialog,
                    "Please select a course to view information.",
                    "No Course Selected",
                    JOptionPane.WARNING_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(infoButton);
        buttonPanel.add(enrollButton);
        buttonPanel.add(cancelButton);

        dialog.add(searchPanel, BorderLayout.NORTH);
        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void showCourseInfoDialog(Course course) {
        JDialog dialog = new JDialog(this, "Course Information", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);

        JPanel infoPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Add course information
        gbc.gridx = 0; gbc.gridy = 0;
        infoPanel.add(new JLabel("Course Code:"), gbc);
        gbc.gridx = 1;
        infoPanel.add(new JLabel(course.getCourseCode()), gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        infoPanel.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1;
        infoPanel.add(new JLabel(course.getTitle()), gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        infoPanel.add(new JLabel("Instructor:"), gbc);
        gbc.gridx = 1;
        infoPanel.add(new JLabel(course.getInstructor()), gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        infoPanel.add(new JLabel("Credits:"), gbc);
        gbc.gridx = 1;
        infoPanel.add(new JLabel(String.valueOf(course.getCredits())), gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        infoPanel.add(new JLabel("Schedule:"), gbc);
        gbc.gridx = 1;
        infoPanel.add(new JLabel(course.getSchedule()), gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        infoPanel.add(new JLabel("Enrollment:"), gbc);
        gbc.gridx = 1;
        infoPanel.add(new JLabel(course.getEnrolledStudents() + "/" + course.getMaxStudents()), gbc);

        gbc.gridx = 0; gbc.gridy = 6;
        infoPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        infoPanel.add(new JLabel(course.getStatus()), gbc);

        dialog.add(infoPanel, BorderLayout.CENTER);

        JButton closeButton = createButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(closeButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private boolean hasScheduleConflict(Course newCourse) {
        for (Course enrolledCourse : enrolledCourses) {
            if (hasScheduleConflict(newCourse, enrolledCourse)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasScheduleConflict(Course course1, Course course2) {
        // Parse schedule strings to get day and time
        String[] schedule1 = course1.getSchedule().split(" ");
        String[] schedule2 = course2.getSchedule().split(" ");
        
        if (schedule1.length < 2 || schedule2.length < 2) {
            return false;
        }
        
        String day1 = schedule1[0];
        String time1 = schedule1[1];
        String day2 = schedule2[0];
        String time2 = schedule2[1];
        
        // Check if courses are on the same day
        if (!day1.equals(day2)) {
            return false;
        }
        
        // Parse time slots
        try {
            String[] time1Parts = time1.split("-");
            String[] time2Parts = time2.split("-");
            
            if (time1Parts.length < 2 || time2Parts.length < 2) {
                return false;
            }
            
            // Convert time strings to comparable values (e.g., "9:00" to 900)
            int start1 = convertTimeToValue(time1Parts[0]);
            int end1 = convertTimeToValue(time1Parts[1]);
            int start2 = convertTimeToValue(time2Parts[0]);
            int end2 = convertTimeToValue(time2Parts[1]);
            
            // Check for overlap
            return (start1 < end2 && start2 < end1);
        } catch (Exception e) {
            return false;
        }
    }
    
    private int convertTimeToValue(String time) {
        // Convert time string (e.g., "9:00" or "13:30") to a comparable integer value
        String[] parts = time.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        return hours * 100 + minutes;
    }

    private void dropSelectedCourse() {
        // Course drop karne ke liye function hai
        int selectedRow = courseTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a course to drop.",
                "No Course Selected",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        selectedRow = courseTable.convertRowIndexToModel(selectedRow);
        String courseCode = (String) courseTableModel.getValueAt(selectedRow, 0);
        Course course = courseDatabase.getCourseByCode(courseCode);
        
        if (course != null) {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to drop " + courseCode + "?",
                "Confirm Drop",
                JOptionPane.YES_NO_OPTION);
                
            if (confirm == JOptionPane.YES_OPTION) {
                enrollmentDatabase.dropCourse(student, course);
                course.setEnrolledStudents(course.getEnrolledStudents() - 1);
                courseDatabase.saveCourses();
                
                loadEnrolledCourses();
                statusLabel.setText("Dropped " + courseCode + ". Last updated: " + DateTimeUtil.getCurrentDateTime());
            }
        }
    }

    private void showScheduleDialog() {
        JDialog dialog = new JDialog(this, "Course Schedule", true);
        dialog.setLayout(new BorderLayout());
        
        TimetablePanel timetablePanel = new TimetablePanel(enrolledCourses);
        dialog.add(timetablePanel, BorderLayout.CENTER);
        
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());
        dialog.add(closeButton, BorderLayout.SOUTH);
        
        dialog.setSize(800, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void refreshCourseTable() {
        DefaultTableModel model = (DefaultTableModel) courseTable.getModel();
        model.setRowCount(0);
        
        for (Course course : availableCourses) {
            model.addRow(new Object[]{
                course.getCourseCode(),
                course.getTitle(),
                course.getInstructor(),
                course.getCredits(),
                course.getSchedule(),
                course.getStatus()
            });
        }
    }

    private void handleLogout() {
        int choice = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to logout?",
            "Logout Confirmation",
            JOptionPane.YES_NO_OPTION
        );
        
        if (choice == JOptionPane.YES_OPTION) {
            dispose();
            new LoginPage(userDatabase, courseDatabase, enrollmentDatabase).setVisible(true);
        }
    }

    private void autoGenerateTimetable() {
        // Get available courses that the student isn't enrolled in
        availableCourses = courseDatabase.getAllCourses().stream()
            .filter(course -> !enrollmentDatabase.isEnrolled(student, course))
            .filter(course -> !course.isFull())
            .collect(Collectors.toList());
            
        if (availableCourses.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "No available courses found for enrollment. All courses are either full or you're already enrolled.",
                "Auto-Generate Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Generate multiple timetables
        List<List<Course>> generatedTimetables = new ArrayList<>();
        
        // Try to generate 5 different timetables
        for (int i = 0; i < 5; i++) {
            List<Course> selectedCourses = new ArrayList<>();
            List<Course> remainingCourses = new ArrayList<>(availableCourses);
            Collections.shuffle(remainingCourses); // Randomize course order for variety
            
            // Try to create a valid timetable with maximum 5 courses
            for (Course course : remainingCourses) {
                if (selectedCourses.size() >= 5) break;
                
                boolean hasConflict = false;
                for (Course selected : selectedCourses) {
                    if (hasScheduleConflict(course, selected)) {
                        hasConflict = true;
                        break;
                    }
                }
                
                if (!hasConflict) {
                    selectedCourses.add(course);
                }
            }
            
            if (!selectedCourses.isEmpty() && !containsSameCourses(generatedTimetables, selectedCourses)) {
                generatedTimetables.add(selectedCourses);
            }
        }

        if (generatedTimetables.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Could not generate any valid timetables. All available courses have schedule conflicts.",
                "Auto-Generate Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Show timetable selection dialog
        JDialog dialog = new JDialog(this, "Select Timetable", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(1000, 700);
        dialog.setLocationRelativeTo(this);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));
        
        for (int i = 0; i < generatedTimetables.size(); i++) {
            List<Course> timetable = generatedTimetables.get(i);
            JPanel optionPanel = new JPanel(new BorderLayout(10, 10));
            optionPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            // Create course list panel
            JPanel courseListPanel = new JPanel();
            courseListPanel.setLayout(new BoxLayout(courseListPanel, BoxLayout.Y_AXIS));
            courseListPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(70, 130, 180), 2),
                "Selected Courses",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14),
                new Color(70, 130, 180)
            ));
            
            // Add course details
            for (Course course : timetable) {
                JPanel coursePanel = new JPanel(new GridLayout(1, 4, 5, 0));
                coursePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                
                coursePanel.add(new JLabel(course.getCourseCode()));
                coursePanel.add(new JLabel(course.getTitle()));
                coursePanel.add(new JLabel(course.getInstructor()));
                coursePanel.add(new JLabel(course.getSchedule()));
                
                courseListPanel.add(coursePanel);
                courseListPanel.add(Box.createVerticalStrut(5));
            }
            
            // Add scroll pane for course list
            JScrollPane courseScrollPane = new JScrollPane(courseListPanel);
            courseScrollPane.setPreferredSize(new Dimension(0, 150));
            optionPanel.add(courseScrollPane, BorderLayout.NORTH);
            
            // Create visual timetable
            TimetablePanel visualTimetable = new TimetablePanel(timetable);
            optionPanel.add(new JScrollPane(visualTimetable), BorderLayout.CENTER);
            
            // Add enroll button with modern styling
            JButton enrollButton = new JButton("Enroll in These Courses");
            enrollButton.setFont(new Font("Arial", Font.BOLD, 14));
            enrollButton.setBackground(new Color(70, 130, 180));
            enrollButton.setForeground(Color.WHITE);
            enrollButton.setFocusPainted(false);
            enrollButton.setBorderPainted(false);
            enrollButton.setOpaque(true);
            
            enrollButton.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(dialog,
                    "Are you sure you want to enroll in these courses?",
                    "Confirm Enrollment",
                    JOptionPane.YES_NO_OPTION);
                    
                if (confirm == JOptionPane.YES_OPTION) {
                    enrollInCourses(timetable);
                    dialog.dispose();
                }
            });
            
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            buttonPanel.add(enrollButton);
            optionPanel.add(buttonPanel, BorderLayout.SOUTH);
            
            tabbedPane.addTab("Option " + (i + 1), optionPanel);
        }
        
        dialog.add(tabbedPane, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private boolean containsSameCourses(List<List<Course>> timetables, List<Course> newTimetable) {
        for (List<Course> existingTimetable : timetables) {
            if (existingTimetable.size() == newTimetable.size() &&
                existingTimetable.containsAll(newTimetable) &&
                newTimetable.containsAll(existingTimetable)) {
                return true;
            }
        }
        return false;
    }

    private void enrollInCourses(List<Course> courses) {
        for (Course course : courses) {
            if (!enrollmentDatabase.isEnrolled(student, course) && !course.isFull()) {
                enrollmentDatabase.enrollStudent(student, course);
            }
        }
        enrollmentDatabase.saveEnrollments();
        loadEnrolledCourses();
        updateTimetableDisplay();
    }

    private void updateTimetableDisplay() {
        // Clear existing timetable display
        timetablePanel.removeAll();
        
        // Create new timetable display
        timetablePanel.setLayout(new GridLayout(6, 6)); // 5 days + header, 6 time slots + header
        
        // Add headers
        String[] days = {"Time", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
        String[] times = {"", "9:00-10:30", "10:30-12:00", "13:30-15:00", "15:00-16:30", "16:30-18:00"};
        
        // Add time column headers
        for (int i = 0; i < times.length; i++) {
            JLabel label = new JLabel(times[i]);
            label.setBorder(BorderFactory.createEtchedBorder());
            label.setHorizontalAlignment(SwingConstants.CENTER);
            timetablePanel.add(label);
        }
        
        // Add day headers
        for (int i = 1; i < days.length; i++) {
            JLabel label = new JLabel(days[i]);
            label.setBorder(BorderFactory.createEtchedBorder());
            label.setHorizontalAlignment(SwingConstants.CENTER);
            timetablePanel.add(label);
        }
        
        // Add course slots
        for (Course course : enrolledCourses) {
            String schedule = course.getSchedule();
            String[] parts = schedule.split(" ");
            if (parts.length == 2) {
                String day = parts[0];
                String time = parts[1];
                
                int dayIndex = getDayIndex(day);
                int timeIndex = getTimeIndex(time);
                
                if (dayIndex >= 0 && timeIndex >= 0) {
                    int position = (timeIndex * 6) + dayIndex + 1;
                    if (position < timetablePanel.getComponentCount()) {
                        JLabel courseLabel = new JLabel(course.getCourseCode() + "<br>" + course.getTitle());
                        courseLabel.setBorder(BorderFactory.createEtchedBorder());
                        courseLabel.setHorizontalAlignment(SwingConstants.CENTER);
                        timetablePanel.add(courseLabel, position);
                    }
                }
            }
        }
        
        timetablePanel.revalidate();
        timetablePanel.repaint();
    }

    private int getDayIndex(String day) {
        switch (day.toUpperCase()) {
            case "MON": return 1;
            case "TUE": return 2;
            case "WED": return 3;
            case "THU": return 4;
            case "FRI": return 5;
            default: return -1;
        }
    }

    private int getTimeIndex(String time) {
        switch (time) {
            case "09:00-10:30": return 1;
            case "10:30-12:00": return 2;
            case "13:30-15:00": return 3;
            case "15:00-16:30": return 4;
            case "16:30-18:00": return 5;
            default: return -1;
        }
    }
} 