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

public class AdminHome extends JFrame {
    private UserDatabase userDatabase;
    private CourseDatabase courseDatabase;
    private EnrollmentDatabase enrollmentDatabase;
    private JTabbedPane tabbedPane;
    private DefaultTableModel userTableModel;
    private DefaultTableModel courseTableModel;
    private JTable userTable;
    private JTable courseTable;
    private JLabel statusLabel;

    public AdminHome(UserDatabase userDatabase, CourseDatabase courseDatabase, 
                    EnrollmentDatabase enrollmentDatabase) {
        this.userDatabase = userDatabase;
        this.courseDatabase = courseDatabase;
        this.enrollmentDatabase = enrollmentDatabase;

        setTitle("Admin Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        // Create main tabbed pane
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));

        // Add tabs
        tabbedPane.addTab("User Management", createUserPanel());
        tabbedPane.addTab("Course Management", createCoursePanel());
        tabbedPane.addTab("Timetable Management", createTimetablePanel());
        tabbedPane.addTab("System Settings", createSettingsPanel());

        // Add tabbed pane to frame
        add(tabbedPane, BorderLayout.CENTER);

        // Add status bar
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(BorderFactory.createEtchedBorder());
        statusLabel = new JLabel("Ready");
        statusBar.add(statusLabel, BorderLayout.WEST);
        
        // Add logout button to status bar
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> handleLogout());
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.add(logoutButton);
        statusBar.add(rightPanel, BorderLayout.EAST);
        
        add(statusBar, BorderLayout.SOUTH);

        loadData();
        setVisible(true);
    }

    private JPanel createUserPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top section with filter options and modern styling
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        JLabel filterLabel = new JLabel("Filter by Role:");
        filterLabel.setFont(new Font("Arial", Font.BOLD, 14));
        JComboBox<String> roleFilter = new JComboBox<>(new String[]{"All", "Student", "Instructor", "Admin"});
        roleFilter.setPreferredSize(new Dimension(150, 30));
        roleFilter.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // Add role filter functionality
        roleFilter.addActionListener(e -> {
            String selectedRole = (String) roleFilter.getSelectedItem();
            filterUsersByRole(selectedRole);
        });
        
        filterPanel.add(filterLabel);
        filterPanel.add(roleFilter);
        panel.add(filterPanel, BorderLayout.NORTH);

        // Center section with user table
        String[] columns = {"Username", "Name", "Role", "Department", "ID", "Status"};
        userTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        userTable = new JTable(userTableModel);
        styleTable(userTable);
        JScrollPane scrollPane = new JScrollPane(userTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Button panel with modern styling
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        JButton addButton = createStyledButton("Add User");
        JButton editButton = createStyledButton("Edit User");
        JButton deleteButton = createStyledButton("Delete User");
        JButton resetButton = createStyledButton("Reset Password");
        JButton refreshButton = createStyledButton("Refresh");

        addButton.addActionListener(e -> showAddUserDialog());
        editButton.addActionListener(e -> showEditUserDialog());
        deleteButton.addActionListener(e -> deleteSelectedUser());
        resetButton.addActionListener(e -> resetUserPassword());
        refreshButton.addActionListener(e -> refreshUserTable());

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(resetButton);
        buttonPanel.add(refreshButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void filterUsersByRole(String selectedRole) {
        userTableModel.setRowCount(0);
        for (User user : userDatabase.getAllUsers()) {
            if (selectedRole.equals("All") || 
                (selectedRole.equals("Student") && user instanceof Student) ||
                (selectedRole.equals("Instructor") && user instanceof Instructor) ||
                (selectedRole.equals("Admin") && user instanceof Admin)) {
                
                String role = user instanceof Student ? "Student" :
                             user instanceof Instructor ? "Instructor" : "Admin";
                String id = user instanceof Student ? ((Student) user).getStudentId() :
                           user instanceof Instructor ? ((Instructor) user).getInstructorId() :
                           ((Admin) user).getAdminId();
                userTableModel.addRow(new Object[]{
                    user.getUsername(),
                    user.getName(),
                    role,
                    user.getDepartment(),
                    id,
                    "Active"
                });
            }
        }
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        return button;
    }

    private void styleTable(JTable table) {
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.setSelectionBackground(new Color(70, 130, 180, 50));
        table.setGridColor(Color.LIGHT_GRAY);
        table.setShowVerticalLines(true);
        table.setShowHorizontalLines(true);
        table.getTableHeader().setReorderingAllowed(false);
    }

    private JPanel createCoursePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top section with search
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search:"));
        JTextField searchField = new JTextField(20);
        searchPanel.add(searchField);
        panel.add(searchPanel, BorderLayout.NORTH);

        // Center section with course table
        String[] columns = {"Course Code", "Title", "Instructor", "Credits", "Schedule", "Enrolled/Max", "Status"};
        courseTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        courseTable = new JTable(courseTableModel);
        courseTable.getTableHeader().setReorderingAllowed(false);
        JScrollPane scrollPane = new JScrollPane(courseTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton("Add Course");
        JButton editButton = new JButton("Edit Course");
        JButton deleteButton = new JButton("Delete Course");
        JButton viewButton = new JButton("View Schedule");
        JButton enrollmentsButton = new JButton("View Enrollments");
        JButton refreshButton = new JButton("Refresh");

        addButton.addActionListener(e -> showAddCourseDialog());
        editButton.addActionListener(e -> showEditCourseDialog());
        deleteButton.addActionListener(e -> deleteSelectedCourse());
        viewButton.addActionListener(e -> showScheduleDialog());
        enrollmentsButton.addActionListener(e -> showEnrollmentsDialog());
        refreshButton.addActionListener(e -> refreshCourseTable());

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(enrollmentsButton);
        buttonPanel.add(refreshButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createTimetablePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top section with buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton viewButton = createStyledButton("View Timetable");
        JButton generateButton = createStyledButton("Generate Timetable");
        JButton exportButton = createStyledButton("Export Timetable");

        viewButton.addActionListener(e -> showScheduleDialog());
        generateButton.addActionListener(e -> showTimetableGenerationDialog());
        exportButton.addActionListener(e -> exportTimetable());

        buttonPanel.add(viewButton);
        buttonPanel.add(generateButton);
        buttonPanel.add(exportButton);
        panel.add(buttonPanel, BorderLayout.NORTH);

        // Center section with timetable
        List<Course> allCourses = courseDatabase.getAllCourses();
        TimetablePanel timetablePanel = new TimetablePanel(allCourses);
        panel.add(new JScrollPane(timetablePanel), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createSettingsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create settings form
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Add settings fields
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("System Name:"), gbc);
        gbc.gridx = 1;
        formPanel.add(new JTextField("Course Evaluation System", 20), gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Max Students per Course:"), gbc);
        gbc.gridx = 1;
        formPanel.add(new JTextField("50", 20), gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Default Course Status:"), gbc);
        gbc.gridx = 1;
        formPanel.add(new JComboBox<>(new String[]{"OPEN", "CLOSED"}), gbc);

        panel.add(formPanel, BorderLayout.CENTER);

        // Add save button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = createStyledButton("Save Settings");
        saveButton.addActionListener(e -> {
            // TODO: Implement settings save functionality
            JOptionPane.showMessageDialog(this,
                "Settings saved successfully!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
        });
        buttonPanel.add(saveButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void showAddUserDialog() {
        // Naya user add karne ke liye function hai
        JDialog dialog = new JDialog(this, "Add New User", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username field
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        JTextField usernameField = new JTextField(20);
        formPanel.add(usernameField, gbc);

        // Password field
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        JPasswordField passwordField = new JPasswordField(20);
        formPanel.add(passwordField, gbc);

        // Name field
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        JTextField nameField = new JTextField(20);
        formPanel.add(nameField, gbc);

        // Department field
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Department:"), gbc);
        gbc.gridx = 1;
        JTextField deptField = new JTextField(20);
        formPanel.add(deptField, gbc);

        // User type selection
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("User Type:"), gbc);
        gbc.gridx = 1;
        String[] types = {"Student", "Instructor"};
        JComboBox<String> typeCombo = new JComboBox<>(types);
        formPanel.add(typeCombo, gbc);

        // Additional fields for Student
        JPanel studentFields = new JPanel(new GridBagLayout());
        GridBagConstraints sgbc = new GridBagConstraints();
        sgbc.insets = new Insets(5, 5, 5, 5);
        sgbc.fill = GridBagConstraints.HORIZONTAL;

        sgbc.gridx = 0; sgbc.gridy = 0;
        studentFields.add(new JLabel("Student ID:"), sgbc);
        sgbc.gridx = 1;
        JTextField studentIdField = new JTextField(20);
        studentFields.add(studentIdField, sgbc);

        sgbc.gridx = 0; sgbc.gridy = 1;
        studentFields.add(new JLabel("Major:"), sgbc);
        sgbc.gridx = 1;
        JTextField majorField = new JTextField(20);
        studentFields.add(majorField, sgbc);

        sgbc.gridx = 0; sgbc.gridy = 2;
        studentFields.add(new JLabel("Year:"), sgbc);
        sgbc.gridx = 1;
        JSpinner yearSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 4, 1));
        studentFields.add(yearSpinner, sgbc);

        // Additional fields for Instructor
        JPanel instructorFields = new JPanel(new GridBagLayout());
        GridBagConstraints igbc = new GridBagConstraints();
        igbc.insets = new Insets(5, 5, 5, 5);
        igbc.fill = GridBagConstraints.HORIZONTAL;

        igbc.gridx = 0; igbc.gridy = 0;
        instructorFields.add(new JLabel("Instructor ID:"), igbc);
        igbc.gridx = 1;
        JTextField instructorIdField = new JTextField(20);
        instructorFields.add(instructorIdField, igbc);

        igbc.gridx = 0; igbc.gridy = 1;
        instructorFields.add(new JLabel("Specialization:"), igbc);
        igbc.gridx = 1;
        JTextField specializationField = new JTextField(20);
        instructorFields.add(specializationField, igbc);

        // Card layout to switch between student and instructor fields
        JPanel cardPanel = new JPanel(new CardLayout());
        cardPanel.add(studentFields, "Student");
        cardPanel.add(instructorFields, "Instructor");

        // Add card panel to form
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 2;
        formPanel.add(cardPanel, gbc);

        // Add type combo listener to switch cards
        typeCombo.addActionListener(e -> {
            CardLayout cl = (CardLayout) cardPanel.getLayout();
            cl.show(cardPanel, (String) typeCombo.getSelectedItem());
        });

        dialog.add(formPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            String name = nameField.getText().trim();
            String department = deptField.getText().trim();
            String type = (String) typeCombo.getSelectedItem();

            if (username.isEmpty() || password.isEmpty() || name.isEmpty() || department.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                    "Please fill in all required fields.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            User newUser;
            if (type.equals("Student")) {
                String studentId = studentIdField.getText().trim();
                String major = majorField.getText().trim();
                int year = (Integer) yearSpinner.getValue();
                
                if (studentId.isEmpty() || major.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog,
                        "Please fill in all student fields.",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                newUser = new Student(username, password, name, department, studentId, major, year);
            } else {
                String instructorId = instructorIdField.getText().trim();
                String specialization = specializationField.getText().trim();
                
                if (instructorId.isEmpty() || specialization.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog,
                        "Please fill in all instructor fields.",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                newUser = new Instructor(username, password, name, department, instructorId, specialization);
            }

            // Check if username already exists
            if (userDatabase.findUser(username) != null) {
                JOptionPane.showMessageDialog(dialog,
                    "Username already exists. Please choose another.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            userDatabase.addUser(newUser);
            loadData();
            statusLabel.setText("User added successfully. Last updated: " + DateTimeUtil.getCurrentDateTime());
            dialog.dispose();
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void showEditUserDialog() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a user to edit.",
                "No User Selected",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        String username = (String) userTableModel.getValueAt(selectedRow, 0);
        User user = userDatabase.findUser(username);
        
        if (user == null) {
            JOptionPane.showMessageDialog(this,
                "User not found.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog(this, "Edit User", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Create fields
        JTextField usernameField = new JTextField(user.getUsername(), 20);
        JPasswordField passwordField = new JPasswordField(user.getPassword(), 20);
        JTextField nameField = new JTextField(user.getName(), 20);
        JTextField departmentField = new JTextField(user.getDepartment(), 20);
        JComboBox<String> roleComboBox = new JComboBox<>(new String[]{"Student", "Instructor", "Admin"});
        roleComboBox.setSelectedItem(user.getRole());

        // Add show/hide password button
        JButton togglePasswordButton = createStyledButton("Show Password");
        togglePasswordButton.addActionListener(e -> {
            if (passwordField.getEchoChar() == '\u0000') {
                passwordField.setEchoChar('•');
                togglePasswordButton.setText("Show Password");
            } else {
                passwordField.setEchoChar('\u0000');
                togglePasswordButton.setText("Hide Password");
            }
        });

        // Add components
        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        dialog.add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        dialog.add(passwordField, gbc);
        gbc.gridx = 2;
        dialog.add(togglePasswordButton, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        dialog.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        dialog.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        dialog.add(new JLabel("Department:"), gbc);
        gbc.gridx = 1;
        dialog.add(departmentField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        dialog.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1;
        dialog.add(roleComboBox, gbc);

        // Add buttons
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> {
            String newUsername = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            String name = nameField.getText().trim();
            String department = departmentField.getText().trim();
            String role = (String) roleComboBox.getSelectedItem();

            if (newUsername.isEmpty() || password.isEmpty() || name.isEmpty() || department.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                    "All fields are required",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Check if username is already taken by another user
            if (!newUsername.equals(user.getUsername())) {
                User existingUser = userDatabase.findUser(newUsername);
                if (existingUser != null) {
                    JOptionPane.showMessageDialog(dialog,
                        "Username is already taken",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            // Update user based on role
            if (role.equals("Student")) {
                Student student = (Student) user;
                student.setUsername(newUsername);
                student.setPassword(password);
                student.setName(name);
                student.setDepartment(department);
            } else if (role.equals("Instructor")) {
                Instructor instructor = (Instructor) user;
                instructor.setUsername(newUsername);
                instructor.setPassword(password);
                instructor.setName(name);
                instructor.setDepartment(department);
            } else if (role.equals("Admin")) {
                Admin admin = (Admin) user;
                admin.setUsername(newUsername);
                admin.setPassword(password);
                admin.setName(name);
                admin.setDepartment(department);
            }

            userDatabase.saveUsers();
            loadData();
            dialog.dispose();
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        dialog.add(buttonPanel, gbc);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void deleteSelectedUser() {
        // User delete karne ke liye function hai
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to delete");
            return;
        }

        String username = (String) userTableModel.getValueAt(selectedRow, 0);
        User user = userDatabase.findUser(username);
        
        if (user != null) {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete user " + username + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);
                
            if (confirm == JOptionPane.YES_OPTION) {
                userDatabase.getAllUsers().remove(user);
                userDatabase.saveUsers();
                loadData();
                statusLabel.setText("User deleted successfully");
            }
        }
    }

    private void resetUserPassword() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to reset password");
            return;
        }

        String username = (String) userTableModel.getValueAt(selectedRow, 0);
        User user = userDatabase.findUser(username);
        
        if (user != null) {
            String newPassword = JOptionPane.showInputDialog(this, "Enter new password:");
            if (newPassword != null && !newPassword.trim().isEmpty()) {
                // Create a new user with the same details but new password
                User newUser;
                if (user instanceof Student) {
                    Student s = (Student) user;
                    newUser = new Student(username, newPassword, s.getName(), s.getDepartment(), 
                                       s.getStudentId(), s.getMajor(), s.getYear());
                } else if (user instanceof Instructor) {
                    Instructor i = (Instructor) user;
                    newUser = new Instructor(username, newPassword, i.getName(), i.getDepartment(), 
                                          i.getInstructorId(), i.getSpecialization());
                } else {
                    Admin a = (Admin) user;
                    newUser = new Admin(username, newPassword, a.getName(), a.getDepartment(), 
                                     a.getAdminId(), a.getAccessLevel());
                }
                
                userDatabase.getAllUsers().remove(user);
                userDatabase.addUser(newUser);
                loadData();
                statusLabel.setText("Password reset successfully");
            }
        }
    }

    private void showAddCourseDialog() {
        // Naya course add karne ke liye function hai
        JDialog dialog = new JDialog(this, "Add New Course", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Course code field
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Course Code:"), gbc);
        gbc.gridx = 1;
        JTextField codeField = new JTextField(20);
        formPanel.add(codeField, gbc);

        // Title field
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1;
        JTextField titleField = new JTextField(20);
        formPanel.add(titleField, gbc);

        // Instructor field
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Instructor:"), gbc);
        gbc.gridx = 1;
        JTextField instructorField = new JTextField(20);
        formPanel.add(instructorField, gbc);

        // Credits field
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Credits:"), gbc);
        gbc.gridx = 1;
        JTextField creditsField = new JTextField(20);
        formPanel.add(creditsField, gbc);

        // Schedule field
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Schedule:"), gbc);
        gbc.gridx = 1;
        JTextField scheduleField = new JTextField(20);
        formPanel.add(scheduleField, gbc);

        // Max students field
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Max Students:"), gbc);
        gbc.gridx = 1;
        JTextField maxStudentsField = new JTextField(20);
        formPanel.add(maxStudentsField, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> {
            try {
                String code = codeField.getText().trim();
                String title = titleField.getText().trim();
                String instructor = instructorField.getText().trim();
                int credits = Integer.parseInt(creditsField.getText().trim());
                String schedule = scheduleField.getText().trim();
                int maxStudents = Integer.parseInt(maxStudentsField.getText().trim());

                if (code.isEmpty() || title.isEmpty() || instructor.isEmpty() || schedule.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "All fields are required");
                    return;
                }

                Course course = new Course(code, title, instructor, credits, schedule, maxStudents);
                courseDatabase.addCourse(course);
                loadData();
                dialog.dispose();
                statusLabel.setText("Course added successfully");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter valid numbers for credits and max students");
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void showEditCourseDialog() {
        int selectedRow = courseTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a course to edit.",
                "No Course Selected",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        String courseCode = (String) courseTableModel.getValueAt(selectedRow, 0);
        Course course = courseDatabase.findCourse(courseCode);
        
        if (course == null) {
            JOptionPane.showMessageDialog(this,
                "Course not found.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Show edit dialog (similar to add dialog but with pre-filled values)
        JDialog dialog = new JDialog(this, "Edit Course", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);

        // Create form with pre-filled values
        // (Similar to add course dialog but with course data pre-filled)
        
        dialog.setVisible(true);
    }

    private void deleteSelectedCourse() {
        int selectedRow = courseTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a course to delete");
            return;
        }

        String courseCode = (String) courseTableModel.getValueAt(selectedRow, 0);
        Course course = courseDatabase.findCourse(courseCode);
        
        if (course != null) {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete course " + courseCode + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);
                
            if (confirm == JOptionPane.YES_OPTION) {
                courseDatabase.getAllCourses().remove(course);
                courseDatabase.saveCourses();
                loadData();
                statusLabel.setText("Course deleted successfully");
            }
        }
    }

    private void showScheduleDialog() {
        JDialog dialog = new JDialog(this, "Course Schedule", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(800, 600);
        dialog.setLocationRelativeTo(this);

        // Create timetable panel with all courses
        List<Course> allCourses = courseDatabase.getAllCourses();
        TimetablePanel timetablePanel = new TimetablePanel(allCourses);
        dialog.add(new JScrollPane(timetablePanel), BorderLayout.CENTER);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void showTimetableGenerationDialog() {
        JDialog dialog = new JDialog(this, "Generate Timetable", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Course selection
        JPanel coursePanel = new JPanel(new BorderLayout(5, 5));
        coursePanel.setBorder(BorderFactory.createTitledBorder("Select Courses"));

        DefaultListModel<Course> courseListModel = new DefaultListModel<>();
        for (Course course : courseDatabase.getAllCourses()) {
            courseListModel.addElement(course);
        }

        JList<Course> courseList = new JList<>(courseListModel);
        courseList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                        boolean isSelected, boolean cellHasFocus) {
                Course course = (Course) value;
                String text = course.getCourseCode() + " - " + course.getTitle();
                return super.getListCellRendererComponent(list, text, index, isSelected, cellHasFocus);
            }
        });
        courseList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        coursePanel.add(new JScrollPane(courseList), BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton generateButton = new JButton("Generate");
        JButton cancelButton = new JButton("Cancel");

        generateButton.addActionListener(e -> {
            List<Course> selectedCourses = courseList.getSelectedValuesList();
            if (selectedCourses.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please select at least one course");
                return;
            }

            dialog.dispose();
            showGeneratedTimetable(selectedCourses);
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(generateButton);
        buttonPanel.add(cancelButton);

        mainPanel.add(coursePanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    private void showGeneratedTimetable(List<Course> courses) {
        JDialog dialog = new JDialog(this, "Generated Timetable", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(800, 600);
        dialog.setLocationRelativeTo(this);

        TimetablePanel timetablePanel = new TimetablePanel(courses);
        dialog.add(new JScrollPane(timetablePanel), BorderLayout.CENTER);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            this.dispose();
            new LoginPage(userDatabase, courseDatabase, enrollmentDatabase).setVisible(true);
        }
    }

    private void loadData() {
        refreshUserTable();
        refreshCourseTable();
        statusLabel.setText("Data loaded successfully");
    }

    private void refreshUserTable() {
        userTableModel.setRowCount(0);
        for (User user : userDatabase.getAllUsers()) {
            String role = user instanceof Student ? "Student" :
                         user instanceof Instructor ? "Instructor" : "Admin";
            String id = user instanceof Student ? ((Student) user).getStudentId() :
                       user instanceof Instructor ? ((Instructor) user).getInstructorId() :
                       ((Admin) user).getAdminId();
            userTableModel.addRow(new Object[]{
                user.getUsername(),
                user.getName(),
                role,
                user.getDepartment(),
                id,
                "Active"
            });
        }
    }

    private void refreshCourseTable() {
        courseTableModel.setRowCount(0);
        for (Course course : courseDatabase.getAllCourses()) {
            courseTableModel.addRow(new Object[]{
                course.getCourseCode(),
                course.getTitle(),
                course.getInstructor(),
                course.getCredits(),
                course.getSchedule(),
                course.getEnrolledStudents() + "/" + course.getMaxStudents(),
                course.getStatus()
            });
        }
    }

    private void exportTimetable() {
        // TODO: Implement timetable export functionality
        JOptionPane.showMessageDialog(this,
            "Timetable export feature coming soon!",
            "Information",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void showEnrollmentsDialog() {
        int selectedRow = courseTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a course to view enrollments",
                "No Course Selected",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        String courseCode = (String) courseTable.getValueAt(selectedRow, 0);
        Course course = courseDatabase.findCourse(courseCode);
        if (course == null) {
            JOptionPane.showMessageDialog(this,
                "Could not find course details",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog(this, "Course Enrollments - " + courseCode, true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);

        String[] columns = {"Student ID", "Student Name", "Department", "Enrollment Date"};
        DefaultTableModel enrollmentModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable enrollmentTable = new JTable(enrollmentModel);
        dialog.add(new JScrollPane(enrollmentTable), BorderLayout.CENTER);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }
} 