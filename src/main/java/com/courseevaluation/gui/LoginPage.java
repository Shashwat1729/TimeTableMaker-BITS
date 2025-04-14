package com.courseevaluation.gui;

import com.courseevaluation.data.*;
import com.courseevaluation.models.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class LoginPage extends JFrame {
    private UserDatabase userDatabase;
    private CourseDatabase courseDatabase;
    private EnrollmentDatabase enrollmentDatabase;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleComboBox;
    private JButton loginButton;

    public LoginPage(UserDatabase userDatabase, CourseDatabase courseDatabase, 
                    EnrollmentDatabase enrollmentDatabase) {
        this.userDatabase = userDatabase;
        this.courseDatabase = courseDatabase;
        this.enrollmentDatabase = enrollmentDatabase;

        setTitle("Course Evaluation System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);
        setResizable(false);

        initializeComponents();
        setVisible(true);
    }

    private void initializeComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Create components
        roleComboBox = new JComboBox<>(new String[]{"Student", "Instructor", "Admin"});
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        loginButton = createStyledButton("Login");

        // Style components
        Font labelFont = new Font("Arial", Font.BOLD, 14);
        Color labelColor = new Color(50, 50, 50); // Dark gray for labels

        JLabel roleLabel = new JLabel("Role:");
        JLabel usernameLabel = new JLabel("Username:");
        JLabel passwordLabel = new JLabel("Password:");

        for (JLabel label : new JLabel[]{roleLabel, usernameLabel, passwordLabel}) {
            label.setFont(labelFont);
            label.setForeground(labelColor);
        }

        // Add key listeners for Enter key
        KeyAdapter enterKeyListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    handleLogin();
                }
            }
        };

        usernameField.addKeyListener(enterKeyListener);
        passwordField.addKeyListener(enterKeyListener);

        // Layout components
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        add(roleLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add(roleComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        add(usernameLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        add(passwordLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add(passwordField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        add(loginButton, gbc);

        // Add action listener to login button
        loginButton.addActionListener(e -> handleLogin());
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(50, 50, 50)); // Dark gray for button
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        return button;
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String selectedRole = (String) roleComboBox.getSelectedItem();

        User user = userDatabase.findUser(username);
        if (user != null && user.getPassword().equals(password)) {
            // Check if the user's role matches the selected role
            boolean roleMatches = false;
            if (selectedRole.equals("Student") && user instanceof Student) {
                roleMatches = true;
            } else if (selectedRole.equals("Instructor") && user instanceof Instructor) {
                roleMatches = true;
            } else if (selectedRole.equals("Admin") && user instanceof Admin) {
                roleMatches = true;
            }

            if (roleMatches) {
                this.dispose();
                if (user instanceof Student) {
                    new StudentHome((Student) user, courseDatabase, enrollmentDatabase, userDatabase);
                } else if (user instanceof Instructor) {
                    new InstructorHome(userDatabase, courseDatabase, enrollmentDatabase, (Instructor) user);
                } else if (user instanceof Admin) {
                    new AdminHome(userDatabase, courseDatabase, enrollmentDatabase);
                }
            } else {
                JOptionPane.showMessageDialog(this,
                    "Invalid role for this user",
                    "Login Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this,
                "Invalid username or password",
                "Login Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
} 