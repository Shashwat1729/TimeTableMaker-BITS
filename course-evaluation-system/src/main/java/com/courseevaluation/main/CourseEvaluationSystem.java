package com.courseevaluation.main;

import com.courseevaluation.data.UserDatabase;
import com.courseevaluation.data.CourseDatabase;
import com.courseevaluation.data.EnrollmentDatabase;
import com.courseevaluation.gui.LoginPage;

import javax.swing.*;

public class CourseEvaluationSystem {
    public static void main(String[] args) {
        // Set the look and feel to the system default
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Initialize databases
        UserDatabase userDatabase = new UserDatabase();
        CourseDatabase courseDatabase = new CourseDatabase();
        EnrollmentDatabase enrollmentDatabase = new EnrollmentDatabase();

        // Create and show the login page
        SwingUtilities.invokeLater(() -> {
            LoginPage loginPage = new LoginPage(userDatabase, courseDatabase, enrollmentDatabase);
            loginPage.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            loginPage.setSize(600, 400);
            loginPage.setLocationRelativeTo(null);
            loginPage.setVisible(true);
        });
    }
} 