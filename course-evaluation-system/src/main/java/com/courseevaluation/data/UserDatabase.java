package com.courseevaluation.data;

import com.courseevaluation.models.*;
import com.courseevaluation.utils.FileUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UserDatabase {
    private List<User> users;
    private static final String USERS_FILE = "data/users.txt";

    public UserDatabase() {
        users = new ArrayList<>();
        loadUsers();
    }

    private void loadUsers() {
        List<String> lines = FileUtil.readLines(USERS_FILE);
        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts.length < 5) continue;

            String role = parts[0];
            String username = parts[1];
            String password = parts[2];
            String name = parts[3];
            String department = parts[4];

            switch (role) {
                case "STUDENT":
                    users.add(new Student(username, password, name, department,
                                       username, "Computer Science", 1));
                    break;
                case "INSTRUCTOR":
                    users.add(new Instructor(username, password, name, department,
                                          username, "Computer Science"));
                    break;
                case "ADMIN":
                    users.add(new Admin(username, password, name, department,
                                     username, "FULL"));
                    break;
            }
        }
    }

    public List<User> getUsers() {
        return users;
    }

    public List<User> getUsersByRole(String role) {
        return users.stream()
                   .filter(user -> user.getRole().equals(role))
                   .collect(Collectors.toList());
    }

    public User findUser(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username) && user.validatePassword(password)) {
                return user;
            }
        }
        return null;
    }

    public boolean authenticate(String username, String password) {
        return findUser(username, password) != null;
    }

    public void addUser(User user) {
        users.add(user);
    }

    public void deleteUser(String username) {
        users.removeIf(user -> user.getUsername().equals(username));
    }
} 