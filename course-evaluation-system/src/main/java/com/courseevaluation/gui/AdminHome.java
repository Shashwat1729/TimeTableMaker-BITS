package com.courseevaluation.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;
import com.courseevaluation.data.UserDatabase;
import com.courseevaluation.data.CourseDatabase;
import com.courseevaluation.data.EnrollmentDatabase;
import com.courseevaluation.models.*;
import com.courseevaluation.utils.FileUtil;
import com.courseevaluation.utils.DateTimeUtil;
import com.courseevaluation.utils.Validator;

public class AdminHome extends JFrame {
    private User admin;
    private UserDatabase userDatabase;
    private CourseDatabase courseDatabase;
    private EnrollmentDatabase enrollmentDatabase;
    private JTabbedPane tabbedPane;
    private DefaultTableModel userTableModel;
    private DefaultTableModel courseTableModel;
    private DefaultTableModel enrollmentTableModel;

    public AdminHome(User admin, UserDatabase userDatabase, CourseDatabase courseDatabase, 
                    EnrollmentDatabase enrollmentDatabase) {
        this.admin = admin;
        this.userDatabase = userDatabase;
        this.courseDatabase = courseDatabase;
        this.enrollmentDatabase = enrollmentDatabase;

        setTitle("Admin Dashboard - " + admin.getUsername());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        initializeComponents();
        loadData();
        setVisible(true);
    }

    private void initializeComponents() {
        // Create main panel with border layout
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Add tabbed pane to the center
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Users", createUsersPanel());
        tabbedPane.addTab("Courses", createCoursesPanel());
        tabbedPane.addTab("Enrollments", createEnrollmentsPanel());
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        // Add logout button to the top
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION);
                
            if (confirm == JOptionPane.YES_OPTION) {
                this.dispose();
                SwingUtilities.invokeLater(() -> {
                    LoginPage loginPage = new LoginPage(userDatabase, courseDatabase, enrollmentDatabase);
                    loginPage.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    loginPage.setSize(600, 400);
                    loginPage.setLocationRelativeTo(null);
                    loginPage.setVisible(true);
                });
            }
        });
        topPanel.add(logoutButton);
        mainPanel.add(topPanel, BorderLayout.NORTH);
        
        add(mainPanel);
    }

    private JPanel createUsersPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addUserButton = new JButton("Add User");
        JButton editUserButton = new JButton("Edit User");
        JButton deleteUserButton = new JButton("Delete User");
        
        addUserButton.addActionListener(e -> showAddUserDialog());
        editUserButton.addActionListener(e -> showEditUserDialog());
        deleteUserButton.addActionListener(e -> deleteSelectedUser());

        toolbar.add(addUserButton);
        toolbar.add(editUserButton);
        toolbar.add(deleteUserButton);
        panel.add(toolbar, BorderLayout.NORTH);

        // Users table
        String[] columns = {"Username", "Role", "Name", "Department"};
        userTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable userTable = new JTable(userTableModel);
        panel.add(new JScrollPane(userTable), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createCoursesPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addCourseButton = new JButton("Add Course");
        JButton editCourseButton = new JButton("Edit Course");
        JButton deleteCourseButton = new JButton("Delete Course");
        
        addCourseButton.addActionListener(e -> showAddCourseDialog());
        editCourseButton.addActionListener(e -> showEditCourseDialog());
        deleteCourseButton.addActionListener(e -> deleteSelectedCourse());

        toolbar.add(addCourseButton);
        toolbar.add(editCourseButton);
        toolbar.add(deleteCourseButton);
        panel.add(toolbar, BorderLayout.NORTH);

        // Courses table
        String[] columns = {"Course Code", "Title", "Instructor", "Credits", "Schedule", "Enrolled/Max", "Status"};
        courseTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable courseTable = new JTable(courseTableModel);
        panel.add(new JScrollPane(courseTable), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createEnrollmentsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addEnrollmentButton = new JButton("Add Enrollment");
        JButton removeEnrollmentButton = new JButton("Remove Enrollment");
        
        addEnrollmentButton.addActionListener(e -> showAddEnrollmentDialog());
        removeEnrollmentButton.addActionListener(e -> removeSelectedEnrollment());

        toolbar.add(addEnrollmentButton);
        toolbar.add(removeEnrollmentButton);
        panel.add(toolbar, BorderLayout.NORTH);

        // Enrollments table
        String[] columns = {"Student ID", "Course Code", "Enrollment Date", "Status"};
        enrollmentTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable enrollmentTable = new JTable(enrollmentTableModel);
        panel.add(new JScrollPane(enrollmentTable), BorderLayout.CENTER);

        return panel;
    }

    private void loadData() {
        // Load users
        userTableModel.setRowCount(0);
        List<User> students = userDatabase.getUsersByRole("STUDENT");
        List<User> instructors = userDatabase.getUsersByRole("INSTRUCTOR");
        
        for (User user : students) {
            if (user instanceof Student) {
                Student student = (Student) user;
                userTableModel.addRow(new Object[]{
                    student.getUsername(),
                    "STUDENT",
                    student.getName(),
                    student.getDepartment()
                });
            }
        }
        
        for (User user : instructors) {
            if (user instanceof Instructor) {
                Instructor instructor = (Instructor) user;
                userTableModel.addRow(new Object[]{
                    instructor.getUsername(),
                    "INSTRUCTOR",
                    instructor.getName(),
                    instructor.getDepartment()
                });
            }
        }

        // Load courses
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

        // Load enrollments
        enrollmentTableModel.setRowCount(0);
        for (Course course : courseDatabase.getAllCourses()) {
            List<Enrollment> enrollments = enrollmentDatabase.getEnrollmentsByCourse(course.getCourseCode());
            for (Enrollment enrollment : enrollments) {
                enrollmentTableModel.addRow(new Object[]{
                    enrollment.getStudentId(),
                    enrollment.getCourseCode(),
                    enrollment.getEnrollmentDate(),
                    enrollment.getStatus()
                });
            }
        }
    }

    private void showAddUserDialog() {
        JDialog dialog = new JDialog(this, "Add User", true);
        dialog.setLayout(new GridLayout(0, 2, 10, 10));
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JComboBox<String> roleCombo = new JComboBox<>(new String[]{"STUDENT", "INSTRUCTOR"});
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JTextField nameField = new JTextField();
        JTextField departmentField = new JTextField();
        JTextField idField = new JTextField();
        JTextField specialField = new JTextField(); // major/specialization
        JSpinner yearSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 4, 1));

        dialog.add(new JLabel("Role:"));
        dialog.add(roleCombo);
        dialog.add(new JLabel("Username:"));
        dialog.add(usernameField);
        dialog.add(new JLabel("Password:"));
        dialog.add(passwordField);
        dialog.add(new JLabel("Name:"));
        dialog.add(nameField);
        dialog.add(new JLabel("Department:"));
        dialog.add(departmentField);
        dialog.add(new JLabel("ID:"));
        dialog.add(idField);
        dialog.add(new JLabel("Major/Specialization:"));
        dialog.add(specialField);

        if (roleCombo.getSelectedItem().equals("STUDENT")) {
            dialog.add(new JLabel("Year:"));
            dialog.add(yearSpinner);
        }

        JButton addButton = new JButton("Add");
        addButton.addActionListener(e -> {
            String role = (String) roleCombo.getSelectedItem();
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String name = nameField.getText();
            String department = departmentField.getText();
            String id = idField.getText();
            String special = specialField.getText();

            if (role.equals("STUDENT")) {
                int year = (Integer) yearSpinner.getValue();
                Student student = new Student(username, password, name, department,
                                           id, special, year);
                userDatabase.addUser(student);
            } else {
                Instructor instructor = new Instructor(username, password, name, department,
                                                    id, special);
                userDatabase.addUser(instructor);
            }

            refreshUserTable();
            dialog.dispose();
        });

        dialog.add(addButton);
        dialog.setVisible(true);
    }

    private void showEditUserDialog() {
        // Similar to showAddUserDialog but with pre-filled fields
        // Implementation details omitted for brevity
    }

    private void deleteSelectedUser() {
        JPanel usersPanel = (JPanel) tabbedPane.getComponentAt(0);
        JScrollPane scrollPane = (JScrollPane) usersPanel.getComponent(1);
        JTable userTable = (JTable) scrollPane.getViewport().getView();
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow != -1) {
            String username = (String) userTable.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this user?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);
                
            if (confirm == JOptionPane.YES_OPTION) {
                userDatabase.deleteUser(username);
                refreshUserTable();
            }
        } else {
            JOptionPane.showMessageDialog(this,
                "Please select a user to delete",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
        }
    }

    private void showAddCourseDialog() {
        // Implementation details omitted for brevity
    }

    private void showEditCourseDialog() {
        // Implementation details omitted for brevity
    }

    private void deleteSelectedCourse() {
        // Implementation details omitted for brevity
    }

    private void showAddEnrollmentDialog() {
        // Implementation details omitted for brevity
    }

    private void removeSelectedEnrollment() {
        // Implementation details omitted for brevity
    }

    private void refreshUserTable() {
        userTableModel.setRowCount(0);
        List<User> students = userDatabase.getUsersByRole("STUDENT");
        List<User> instructors = userDatabase.getUsersByRole("INSTRUCTOR");
        
        for (User user : students) {
            if (user instanceof Student) {
                Student student = (Student) user;
                userTableModel.addRow(new Object[]{
                    student.getUsername(),
                    "STUDENT",
                    student.getName(),
                    student.getDepartment()
                });
            }
        }
        
        for (User user : instructors) {
            if (user instanceof Instructor) {
                Instructor instructor = (Instructor) user;
                userTableModel.addRow(new Object[]{
                    instructor.getUsername(),
                    "INSTRUCTOR",
                    instructor.getName(),
                    instructor.getDepartment()
                });
            }
        }
    }
} 