package com.wiseplanner.model;

import java.util.List;

public class Dashboard {
    private String greeting;
    private List<Assignment> todaysAssignments;
    private List<Announcement> todaysAnnouncements;
    private List<Task> todoList;
    private String geminiInsights;
    private String geminiGradeAnalysis;

    public String getGreeting() { return greeting; }
    public void setGreeting(String greeting) { this.greeting = greeting; }

    public List<Assignment> getTodaysAssignments() { return todaysAssignments; }
    public void setTodaysAssignments(List<Assignment> todaysAssignments) { this.todaysAssignments = todaysAssignments; }

    public List<Announcement> getTodaysAnnouncements() { return todaysAnnouncements; }
    public void setTodaysAnnouncements(List<Announcement> todaysAnnouncements) { this.todaysAnnouncements = todaysAnnouncements; }

    public List<Task> getTodoList() { return todoList; }
    public void setTodoList(List<Task> todoList) { this.todoList = todoList; }

    public String getGeminiInsights() { return geminiInsights; }
    public void setGeminiInsights(String geminiInsights) { this.geminiInsights = geminiInsights; }

    public String getGeminiGradeAnalysis() { return geminiGradeAnalysis; }
    public void setGeminiGradeAnalysis(String geminiGradeAnalysis) { this.geminiGradeAnalysis = geminiGradeAnalysis; }
}