package edu.bsu.cs;

public class Assignment {
    private String id;
    private String name;
    private String description;
    private String dueAt;

    public Assignment(String id, String name, String description, String dueAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.dueAt = dueAt;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getDueAt() { return dueAt; }
}