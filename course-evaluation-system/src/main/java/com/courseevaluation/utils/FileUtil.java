package com.courseevaluation.utils;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {
    private static final String DATA_DIR = "data";
    
    public static List<String> readLines(String filePath) {
        List<String> lines = new ArrayList<>();
        try {
            // First try to read from the filesystem
            File file = new File(filePath);
            if (file.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (!line.trim().isEmpty()) {
                            lines.add(line.trim());
                        }
                    }
                }
            } else {
                // If file doesn't exist in filesystem, try to read from JAR resources
                URL url = FileUtil.class.getClassLoader().getResource(filePath);
                if (url != null) {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            if (!line.trim().isEmpty()) {
                                lines.add(line.trim());
                            }
                        }
                    }
                } else {
                    // Try reading from the data directory
                    file = new File(DATA_DIR, new File(filePath).getName());
                    if (file.exists()) {
                        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                            String line;
                            while ((line = reader.readLine()) != null) {
                                if (!line.trim().isEmpty()) {
                                    lines.add(line.trim());
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    public static void writeLines(String filePath, List<String> lines) {
        // Ensure the data directory exists
        File dataDir = new File(DATA_DIR);
        if (!dataDir.exists()) {
            dataDir.mkdir();
        }
        
        // Write to the external data directory
        File file = new File(DATA_DIR, new File(filePath).getName());
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void appendLine(String filePath, String line) {
        // Ensure the data directory exists
        File dataDir = new File(DATA_DIR);
        if (!dataDir.exists()) {
            dataDir.mkdir();
        }
        
        // Write to the external data directory
        File file = new File(DATA_DIR, new File(filePath).getName());
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(line);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean fileExists(String filePath) {
        // Check if file exists in filesystem
        File file = new File(filePath);
        if (file.exists()) {
            return true;
        }
        
        // Check if file exists in JAR resources
        URL url = FileUtil.class.getClassLoader().getResource(filePath);
        if (url != null) {
            return true;
        }
        
        // Check if file exists in data directory
        file = new File(DATA_DIR, new File(filePath).getName());
        return file.exists();
    }
} 