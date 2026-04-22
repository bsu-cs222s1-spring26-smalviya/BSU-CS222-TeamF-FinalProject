package com.wiseplanner.model;

import java.util.Objects;

public class Task {
    private String deadline;
    private String title;
    private String content;
    private String courseId;
    private boolean done;

    public Task(String deadline, String title, String content) {
        this.deadline = deadline;
        this.title    = title;
        this.content  = content;
        this.done     = false;
    }

    public String getDeadline()              { return deadline; }
    public void   setDeadline(String d)      { this.deadline = d; }

    public String getTitle()                 { return title; }
    public void   setTitle(String t)         { this.title = t; }

    public String getContent()               { return content; }
    public void   setContent(String c)       { this.content = c; }

    public String getCourseId()              { return courseId; }
    public void   setCourseId(String id)     { this.courseId = id; }

    public boolean isDone()                  { return done; }
    public void    setDone(boolean done)     { this.done = done; }

    @Override
    public String toString() {
        return String.format("%s %s %s%n", deadline, title, content);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task t)) return false;
        return Objects.equals(deadline, t.deadline)
                && Objects.equals(title, t.title)
                && Objects.equals(content, t.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(deadline, title, content);
    }
}