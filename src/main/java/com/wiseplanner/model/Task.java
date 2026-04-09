package com.wiseplanner.model;

import java.util.Objects;

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

@Override
public boolean equals(Object object) {
    if (this == object) {
        return true;
    }
    if (!(object instanceof Task task)) {
        return false;
    }
    return Objects.equals(deadline, task.deadline)
            && Objects.equals(title, task.title)
            && Objects.equals(content, task.content);
}

@Override
public int hashCode() {
    return Objects.hash(deadline, title, content);
}
}