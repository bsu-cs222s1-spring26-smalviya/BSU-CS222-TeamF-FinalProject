package com.wiseplanner.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

public class DashboardTest {

    private Dashboard dashboard = new Dashboard();

    @Test
    public void setGreeting() {
        dashboard.setGreeting("Good morning, Alice!");
        Assertions.assertEquals("Good morning, Alice!", dashboard.getGreeting());
    }

    @Test
    public void getGreeting_defaultIsNull() {
        Assertions.assertNull(dashboard.getGreeting());
    }

    @Test
    public void setTodaysAssignments() {
        List<Assignment> assignments = new ArrayList<>();
        assignments.add(new Assignment("1", "Lab 3", "Sorting algorithms", "2024-04-18T23:59:00Z"));
        dashboard.setTodaysAssignments(assignments);
        Assertions.assertEquals(1, dashboard.getTodaysAssignments().size());
        Assertions.assertEquals("Lab 3", dashboard.getTodaysAssignments().get(0).getName());
    }

    @Test
    public void getTodaysAssignments_defaultIsNull() {
        Assertions.assertNull(dashboard.getTodaysAssignments());
    }

    @Test
    public void setTodaysAnnouncements() {
        List<Announcement> announcements = new ArrayList<>();
        announcements.add(new Announcement("10", "Midterm update", "Exam moved to Friday", "2024-04-18T09:00:00Z"));
        dashboard.setTodaysAnnouncements(announcements);
        Assertions.assertEquals(1, dashboard.getTodaysAnnouncements().size());
        Assertions.assertEquals("Midterm update", dashboard.getTodaysAnnouncements().get(0).getTitle());
    }

    @Test
    public void getTodaysAnnouncements_defaultIsNull() {
        Assertions.assertNull(dashboard.getTodaysAnnouncements());
    }

    @Test
    public void setTodoList() {
        List<Task> tasks = new ArrayList<>();
        tasks.add(new Task("2024-04-20", "Study for finals", "Chapter 5-8"));
        dashboard.setTodoList(tasks);
        Assertions.assertEquals(1, dashboard.getTodoList().size());
        Assertions.assertEquals("Study for finals", dashboard.getTodoList().get(0).getTitle());
    }

    @Test
    public void getTodoList_defaultIsNull() {
        Assertions.assertNull(dashboard.getTodoList());
    }

    @Test
    public void setGeminiInsights() {
        dashboard.setGeminiInsights("You have a busy day ahead!");
        Assertions.assertEquals("You have a busy day ahead!", dashboard.getGeminiInsights());
    }

    @Test
    public void getGeminiInsights_defaultIsNull() {
        Assertions.assertNull(dashboard.getGeminiInsights());
    }

    @Test
    public void setGeminiGradeAnalysis() {
        dashboard.setGeminiGradeAnalysis("Two assignments are missing.");
        Assertions.assertEquals("Two assignments are missing.", dashboard.getGeminiGradeAnalysis());
    }

    @Test
    public void getGeminiGradeAnalysis_defaultIsNull() {
        Assertions.assertNull(dashboard.getGeminiGradeAnalysis());
    }

    @Test
    public void setGreeting_overwritesPreviousValue() {
        dashboard.setGreeting("Good morning!");
        dashboard.setGreeting("Good evening!");
        Assertions.assertEquals("Good evening!", dashboard.getGreeting());
    }

    @Test
    public void setTodoList_replacesEntireList() {
        List<Task> first = new ArrayList<>();
        first.add(new Task("2024-04-18", "Task A", "content"));
        dashboard.setTodoList(first);

        List<Task> second = new ArrayList<>();
        second.add(new Task("2024-04-19", "Task B", "content"));
        second.add(new Task("2024-04-20", "Task C", "content"));
        dashboard.setTodoList(second);

        Assertions.assertEquals(2, dashboard.getTodoList().size());
        Assertions.assertEquals("Task B", dashboard.getTodoList().get(0).getTitle());
    }
}