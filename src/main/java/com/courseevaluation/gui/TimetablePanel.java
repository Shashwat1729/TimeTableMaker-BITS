package com.courseevaluation.gui;

import com.courseevaluation.models.Course;
import javax.swing.*;
import java.awt.*;
import java.util.*;

public class TimetablePanel extends JPanel {
    private static final String[] DAYS = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
    private static final String[] TIME_SLOTS = {
        "09:00-10:30", "10:30-12:00", "12:00-13:30", "13:30-15:00", "15:00-16:30"
    };
    private java.util.List<Course> courses;
    private Map<String, Map<String, Course>> timeTable;

    public TimetablePanel(java.util.List<Course> courses) {
        this.courses = courses != null ? courses : new ArrayList<>();
        this.timeTable = new HashMap<>();
        for (String day : DAYS) {
            timeTable.put(day, new HashMap<>());
        }
        organizeCourses();
        initializeUI();
    }

    private void organizeCourses() {
        for (Course course : courses) {
            String schedule = course.getSchedule();
            if (schedule != null && !schedule.trim().isEmpty()) {
                String[] parts = schedule.split(" ");
                if (parts.length == 2) {
                    String day = convertDay(parts[0]);
                    String time = parts[1];
                    if (day != null && isValidTimeSlot(time)) {
                        timeTable.get(day).put(time, course);
                    }
                }
            }
        }
    }

    private String convertDay(String shortDay) {
        Map<String, String> dayMap = new HashMap<>();
        dayMap.put("Mon", "Monday");
        dayMap.put("Tue", "Tuesday");
        dayMap.put("Wed", "Wednesday");
        dayMap.put("Thu", "Thursday");
        dayMap.put("Fri", "Friday");
        return dayMap.get(shortDay);
    }

    private boolean isValidTimeSlot(String time) {
        return Arrays.asList(TIME_SLOTS).contains(time);
    }

    private void initializeUI() {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(1, 1, 1, 1);

        // Add header row
        gbc.gridy = 0;
        gbc.gridx = 0;
        addHeaderCell("Time/Day", gbc);
        
        for (int i = 0; i < DAYS.length; i++) {
            gbc.gridx = i + 1;
            addHeaderCell(DAYS[i], gbc);
        }

        // Add time slots and course cells
        for (int i = 0; i < TIME_SLOTS.length; i++) {
            gbc.gridy = i + 1;
            gbc.gridx = 0;
            addHeaderCell(TIME_SLOTS[i], gbc);

            for (int j = 0; j < DAYS.length; j++) {
                gbc.gridx = j + 1;
                Course course = timeTable.get(DAYS[j]).get(TIME_SLOTS[i]);
                if (course != null) {
                    addCourseCell(course, gbc);
                } else {
                    addEmptyCell(gbc);
                }
            }
        }
    }

    private void addHeaderCell(String text, GridBagConstraints gbc) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setOpaque(true);
        label.setBackground(new Color(240, 240, 240));
        label.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        label.setFont(new Font("Arial", Font.BOLD, 12));
        add(label, gbc);
    }

    private void addCourseCell(Course course, GridBagConstraints gbc) {
        JPanel coursePanel = new JPanel();
        coursePanel.setLayout(new BoxLayout(coursePanel, BoxLayout.Y_AXIS));
        coursePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        coursePanel.setBackground(Color.WHITE);

        addCenteredLabel(course.getCourseCode(), Font.BOLD, 12, coursePanel);
        addCenteredLabel(course.getTitle(), Font.PLAIN, 11, coursePanel);
        addCenteredLabel("(" + course.getInstructor() + ")", Font.ITALIC, 11, coursePanel);

        add(coursePanel, gbc);
    }

    private void addCenteredLabel(String text, int fontStyle, int fontSize, JPanel panel) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", fontStyle, fontSize));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(label);
        panel.add(Box.createVerticalStrut(2));
    }

    private void addEmptyCell(GridBagConstraints gbc) {
        JPanel emptyPanel = new JPanel();
        emptyPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        emptyPanel.setBackground(Color.WHITE);
        emptyPanel.setPreferredSize(new Dimension(150, 80));
        add(emptyPanel, gbc);
    }

    public void setCourses(java.util.List<Course> courses) {
        this.courses = courses;
        this.timeTable.clear();
        for (String day : DAYS) {
            timeTable.put(day, new HashMap<>());
        }
        organizeCourses();
        removeAll();
        initializeUI();
        revalidate();
        repaint();
    }
} 