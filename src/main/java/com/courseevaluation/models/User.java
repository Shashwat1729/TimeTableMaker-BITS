package com.courseevaluation.models;

public abstract class User {
    private String username;
    private String password;
    private String role;
    private String name;
    private String department;
    private String status = "ACTIVE"; // Default status

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    @Override
    public String toString() {
        return String.format("%s,%s,%s,%s,%s,%s", username, password, role, name, department, status);
    }
} 