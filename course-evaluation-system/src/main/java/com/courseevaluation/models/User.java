package com.courseevaluation.models;

public class User {
    private String username;
    private String password;
    private String role;
    protected String name;
    protected String department;

    public User(String username, String password, String role, String name, String department) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.name = name;
        this.department = department;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public String getName() {
        return name;
    }

    public String getDepartment() {
        return department;
    }

    public boolean validatePassword(String inputPassword) {
        return this.password.equals(inputPassword);
    }
} 