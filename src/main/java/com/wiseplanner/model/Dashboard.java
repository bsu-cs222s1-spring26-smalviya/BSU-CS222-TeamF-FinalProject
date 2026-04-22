package com.wiseplanner.model;

import java.util.List;

public class Dashboard {
    private String greeting;
    private List<Assignment> todaysAssignments;
    private List<Announcement> todaysAnnouncements;
    private List<Task> todoList;
    private String geminiInsights;
    private String geminiGradeAnalysis;
    private List<Course> allCourses;

    public String getGreeting()                          { return greeting; }
    public void   setGreeting(String g)                  { this.greeting = g; }

    public List<Assignment> getTodaysAssignments()       { return todaysAssignments; }
    public void setTodaysAssignments(List<Assignment> l) { this.todaysAssignments = l; }

    public List<Announcement> getTodaysAnnouncements()        { return todaysAnnouncements; }
    public void setTodaysAnnouncements(List<Announcement> l)  { this.todaysAnnouncements = l; }

    public List<Task> getTodoList()                      { return todoList; }
    public void setTodoList(List<Task> l)                { this.todoList = l; }

    public String getGeminiInsights()                    { return geminiInsights; }
    public void   setGeminiInsights(String s)            { this.geminiInsights = s; }

    public String getGeminiGradeAnalysis()               { return geminiGradeAnalysis; }
    public void   setGeminiGradeAnalysis(String s)       { this.geminiGradeAnalysis = s; }

    public List<Course> getAllCourses()                   { return allCourses; }
    public void setAllCourses(List<Course> courses)      { this.allCourses = courses; }
}