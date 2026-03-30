package com.wiseplanner.model;

public class Task {
    private String deadline;
    private String title;
    private String content;

    public Task(String deadline, String title, String content) {
        this.deadline = deadline;
        this.title = title;
        this.content = content;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getDeadline() {
        return this.deadline;
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
                deadline, title, content);
    }
}