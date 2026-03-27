package com.wiseplanner.model;

public class Task {
    private String timestamp;
    private String title;
    private String content;

    public Task(String timestamp, String title, String content) {
        this.timestamp = timestamp;
        this.title = title;
        this.content = content;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getTimestamp() {
        return this.timestamp;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return this.title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return this.content;
    }

    @Override
    public String toString() {
        return String.format("%s %s %s\n",
                timestamp, title, content);
    }
}