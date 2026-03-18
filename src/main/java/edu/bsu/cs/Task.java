package edu.bsu.cs;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Task {
    private String title;
    private String note;
    private String courseName;
    private String timestamp;

    public Task(String title, String note, String courseName) {
        this.title = title;
        this.note = note;
        this.courseName = courseName;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public String getTitle() { return title; }
    public String getNote() { return note; }
    public String getCourseName() { return courseName; }
    public String getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return String.format("[%s] Course: %s | Task: %s | Note: %s",
                timestamp, courseName, title, note);
    }
}