package com.courseevaluation.models;

public class Admin extends User {
    private String adminId;
    private String accessLevel;

    public Admin(String username, String password, String name, String department,
                String adminId, String accessLevel) {
        super(username, password, "ADMIN", name, department);
        this.adminId = adminId;
        this.accessLevel = accessLevel;
    }

    public String getAdminId() {
        return adminId;
    }

    public String getAccessLevel() {
        return accessLevel;
    }

    @Override
    public String toString() {
        return String.format("ADMIN,%s,%s,%s,%s,%s,%s",
            getUsername(), getPassword(), getName(), getDepartment(), adminId, accessLevel);
    }
} 