package com.wiseplanner.model;

public class Assignment {
    private String id;
    private String name;
    private String description;
    private String due_at;

    public Assignment(String id, String name, String description, String due_at) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.due_at = due_at;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getDue_at() {
        return due_at;
    }
}