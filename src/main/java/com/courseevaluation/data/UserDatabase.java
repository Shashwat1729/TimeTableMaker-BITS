package com.courseevaluation.data;

import com.courseevaluation.models.User;
import com.courseevaluation.models.Student;
import com.courseevaluation.models.Instructor;
import com.courseevaluation.models.Admin;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class UserDatabase {
    private static final String USER_FILE = "data/users.csv";
    private List<User> users;

    public UserDatabase() {
        users = new ArrayList<>();
        loadUsers();
    }

    private void loadUsers() {
        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals("STUDENT")) {
                    users.add(new Student(parts[1], parts[2], parts[3], parts[4], parts[5], parts[6], Integer.parseInt(parts[7])));
                } else if (parts[0].equals("INSTRUCTOR")) {
                    users.add(new Instructor(parts[1], parts[2], parts[3], parts[4], parts[5], parts[6]));
                } else if (parts[0].equals("ADMIN")) {
                    users.add(new Admin(parts[1], parts[2], parts[3], parts[4], parts[5], parts[6]));
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading users: " + e.getMessage());
        }
    }

    public void saveUsers() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(USER_FILE))) {
            for (User user : users) {
                writer.println(user.toString());
            }
        } catch (IOException e) {
            System.err.println("Error saving users: " + e.getMessage());
        }
    }

    public User findUser(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    public void addUser(User user) {
        users.add(user);
        saveUsers();
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }
} 