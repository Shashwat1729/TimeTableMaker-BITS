package com.courseevaluation.gui;

import com.courseevaluation.data.UserDatabase;
import com.courseevaluation.models.*;
import com.courseevaluation.utils.Validator;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import com.courseevaluation.data.CourseDatabase;
import com.courseevaluation.data.EnrollmentDatabase;

public class LoginPage extends JFrame {
    private UserDatabase userDatabase;
    private CourseDatabase courseDatabase;
    private EnrollmentDatabase enrollmentDatabase;
    private JComboBox<String> roleSelector;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public LoginPage(UserDatabase userDatabase, CourseDatabase courseDatabase, 
                    EnrollmentDatabase enrollmentDatabase) {
        this.userDatabase = userDatabase;
        this.courseDatabase = courseDatabase;
        this.enrollmentDatabase = enrollmentDatabase;

        setLayout(new BorderLayout());
        initializeComponents();
    }

    private void initializeComponents() {
        // Create main panel with some padding
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        // Add title
        JLabel titleLabel = new JLabel("Course Evaluation System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(30));

        // Role selector
        String[] roles = {"Student", "Instructor", "Admin"};
        roleSelector = new JComboBox<>(roles);
        roleSelector.setMaximumSize(new Dimension(200, 30));
        roleSelector.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(roleSelector);
        mainPanel.add(Box.createVerticalStrut(20));

        // Username field
        usernameField = new JTextField(20);
        usernameField.setMaximumSize(new Dimension(200, 30));
        usernameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(new JLabel("Username:"));
        mainPanel.add(usernameField);
        mainPanel.add(Box.createVerticalStrut(10));

        // Password field
        passwordField = new JPasswordField(20);
        passwordField.setMaximumSize(new Dimension(200, 30));
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(new JLabel("Password:"));
        mainPanel.add(passwordField);
        mainPanel.add(Box.createVerticalStrut(20));

        // Login button
        loginButton = new JButton("Login");
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });
        mainPanel.add(loginButton);

        // Add main panel to the center of the page
        add(mainPanel, BorderLayout.CENTER);
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String role = (String) roleSelector.getSelectedItem();

        System.out.println("Login attempt - Username: " + username + ", Role: " + role);

        if (!Validator.isValidUsername(username) || !Validator.isValidPassword(password)) {
            System.out.println("Invalid username or password format");
            JOptionPane.showMessageDialog(this,
                "Invalid username or password format",
                "Login Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        User user = userDatabase.findUser(username, password);
        System.out.println("Authentication result: " + (user != null ? "success" : "failed"));
        
        if (user != null && user.getRole().equals(role.toUpperCase())) {
            System.out.println("Login successful - Opening " + role + " dashboard");
            // Clear fields
            usernameField.setText("");
            passwordField.setText("");

            // Open appropriate dashboard
            this.dispose();

            JFrame dashboard = null;
            switch (role.toUpperCase()) {
                case "STUDENT":
                    dashboard = new StudentHome(user, courseDatabase, enrollmentDatabase);
                    break;
                case "INSTRUCTOR":
                    dashboard = new InstructorHome(user, userDatabase, courseDatabase, enrollmentDatabase);
                    break;
                case "ADMIN":
                    dashboard = new AdminHome(user, userDatabase, courseDatabase, enrollmentDatabase);
                    break;
            }
            
            if (dashboard != null) {
                dashboard.setVisible(true);
            }
        } else {
            System.out.println("Login failed - Invalid credentials or role mismatch");
            JOptionPane.showMessageDialog(this,
                "Invalid credentials or role mismatch",
                "Login Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Course Evaluation System");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(600, 400);
            frame.setLocationRelativeTo(null);
            
            UserDatabase userDatabase = new UserDatabase();
            CourseDatabase courseDatabase = new CourseDatabase();
            EnrollmentDatabase enrollmentDatabase = new EnrollmentDatabase();
            
            LoginPage loginPage = new LoginPage(userDatabase, courseDatabase, enrollmentDatabase);
            frame.add(loginPage);
            frame.setVisible(true);
        });
    }
} 