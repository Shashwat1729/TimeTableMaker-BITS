package com.courseevaluation.main;

import com.courseevaluation.data.*;
import com.courseevaluation.gui.LoginPage;
import javax.swing.SwingUtilities;

public class CourseEvaluationSystem {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UserDatabase userDatabase = new UserDatabase();
            CourseDatabase courseDatabase = new CourseDatabase();
            EnrollmentDatabase enrollmentDatabase = new EnrollmentDatabase(courseDatabase);
            
            LoginPage loginPage = new LoginPage(userDatabase, courseDatabase, enrollmentDatabase);
            loginPage.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
            loginPage.setSize(600, 400);
            loginPage.setLocationRelativeTo(null);
            loginPage.setVisible(true);
        });
    }
} 