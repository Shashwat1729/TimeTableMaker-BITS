package com.courseevaluation.models;

public class TimeSlot {
    private String day;
    private String startTime;
    private String endTime;
    private Course course;

    public TimeSlot(String day, String startTime, String endTime, Course course) {
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.course = course;
    }

    public String getDay() {
        return day;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public Course getCourse() {
        return course;
    }

    public boolean overlaps(TimeSlot other) {
        if (!this.day.equals(other.day)) {
            return false;
        }

        int thisStart = convertTimeToMinutes(this.startTime);
        int thisEnd = convertTimeToMinutes(this.endTime);
        int otherStart = convertTimeToMinutes(other.startTime);
        int otherEnd = convertTimeToMinutes(other.endTime);

        return (thisStart < otherEnd && thisEnd > otherStart);
    }

    private int convertTimeToMinutes(String time) {
        String[] parts = time.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        return hours * 60 + minutes;
    }

    @Override
    public String toString() {
        return day + " " + startTime + "-" + endTime + " " + course.getCourseCode();
    }
} 