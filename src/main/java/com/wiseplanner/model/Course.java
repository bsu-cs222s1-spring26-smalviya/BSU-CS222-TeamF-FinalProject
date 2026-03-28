package com.wiseplanner.model;

import java.util.List;

public class Course {
    private String id;
    private String name;
    private List<Assignment> assignments;
    private List<Announcement> announcements;

    public Course(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setAssignments(List<Assignment> assignments) {
        this.assignments = assignments;
    }

    public List<Assignment> getAssignments() {
        return assignments;
    }

    public void setAnnouncements(List<Announcement> announcements) {
        this.announcements = announcements;
    }

    public List<Announcement> getAnnouncements() {
        return announcements;
    }
}
