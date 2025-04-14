package com.courseevaluation.models;

import java.util.*;
import java.util.stream.Collectors;

public class Timetable {
    private List<TimeSlot> timeSlots;
    private static final String[] DAYS = {"Mon", "Tue", "Wed", "Thu", "Fri"};
    private static final String[] TIME_SLOTS = {
        "09:00-10:30", "10:30-12:00", "12:00-13:30",
        "13:30-15:00", "15:00-16:30", "16:30-18:00"
    };

    public Timetable() {
        this.timeSlots = new ArrayList<>();
    }

    public void addTimeSlot(TimeSlot timeSlot) {
        if (!hasConflict(timeSlot)) {
            timeSlots.add(timeSlot);
        }
    }

    public boolean hasConflict(TimeSlot newSlot) {
        return timeSlots.stream().anyMatch(slot -> slot.overlaps(newSlot));
    }

    public List<TimeSlot> getTimeSlots() {
        return new ArrayList<>(timeSlots);
    }

    public List<TimeSlot> getTimeSlotsByDay(String day) {
        return timeSlots.stream()
            .filter(slot -> slot.getDay().equals(day))
            .collect(Collectors.toList());
    }

    public static Timetable generateTimetable(List<Course> courses, List<String> preferences) {
        Timetable timetable = new Timetable();
        List<Course> availableCourses = new ArrayList<>(courses);
        
        // Shuffle courses to get different arrangements
        Collections.shuffle(availableCourses);
        
        // Sort by preferences
        availableCourses.sort((c1, c2) -> {
            boolean c1Preferred = preferences.contains(c1.getCourseCode());
            boolean c2Preferred = preferences.contains(c2.getCourseCode());
            if (c1Preferred && !c2Preferred) return -1;
            if (!c1Preferred && c2Preferred) return 1;
            return 0;
        });

        // Try to schedule each course
        for (Course course : availableCourses) {
            boolean scheduled = false;
            
            // First try the course's preferred schedule
            if (course.getSchedule() != null && !course.getSchedule().isEmpty()) {
                String[] scheduleParts = course.getSchedule().split(" ");
                String day = scheduleParts[0];
                String timeSlot = scheduleParts[1];
                
                TimeSlot slot = new TimeSlot(day, timeSlot.split("-")[0], timeSlot.split("-")[1], course);
                if (!timetable.hasConflict(slot)) {
                    timetable.addTimeSlot(slot);
                    scheduled = true;
                }
            }
            
            // If preferred schedule didn't work, try alternative slots
            if (!scheduled) {
                List<String> availableDays = new ArrayList<>(Arrays.asList(DAYS));
                Collections.shuffle(availableDays);
                
                for (String day : availableDays) {
                    List<String> availableTimeSlots = new ArrayList<>(Arrays.asList(TIME_SLOTS));
                    Collections.shuffle(availableTimeSlots);
                    
                    for (String timeSlot : availableTimeSlots) {
                        String[] times = timeSlot.split("-");
                        TimeSlot slot = new TimeSlot(day, times[0], times[1], course);
                        if (!timetable.hasConflict(slot)) {
                            timetable.addTimeSlot(slot);
                            scheduled = true;
                            break;
                        }
                    }
                    if (scheduled) break;
                }
            }
        }

        return timetable;
    }

    public static List<Timetable> generateMultipleTimetables(List<Course> courses, List<String> preferences, int count) {
        List<Timetable> timetables = new ArrayList<>();
        Set<String> generatedSchedules = new HashSet<>();
        int attempts = 0;
        int maxAttempts = count * 10; // Prevent infinite loop

        while (timetables.size() < count && attempts < maxAttempts) {
            Timetable timetable = generateTimetable(courses, preferences);
            String scheduleKey = timetable.getTimeSlots().stream()
                .map(slot -> slot.getDay() + ":" + slot.getStartTime() + "-" + slot.getEndTime() + ":" + slot.getCourse().getCourseCode())
                .sorted()
                .collect(Collectors.joining("|"));

            if (!generatedSchedules.contains(scheduleKey)) {
                generatedSchedules.add(scheduleKey);
                timetables.add(timetable);
            }
            attempts++;
        }

        return timetables;
    }
} 