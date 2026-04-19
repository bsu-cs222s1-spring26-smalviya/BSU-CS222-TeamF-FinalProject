package com.wiseplanner.service;

import com.wiseplanner.exception.NetworkException;
import com.wiseplanner.model.*;
import com.wiseplanner.util.GeminiConnector;
import com.wiseplanner.util.parser.DashboardParser;

import java.util.ArrayList;
import java.util.List;

public class DashboardService {

    private final CanvasService canvasService;
    private final TaskManager taskManager;
    private final UserManager userManager;
    private final GeminiConnector geminiConnector;
    private final DashboardParser dashboardParser;

    public DashboardService(CanvasService canvasService, TaskManager taskManager, UserManager userManager) {
        this.canvasService = canvasService;
        this.taskManager = taskManager;
        this.userManager = userManager;
        this.geminiConnector = new GeminiConnector();
        this.dashboardParser = new DashboardParser();
    }

    public Dashboard getDashboard() {
        String userName = userManager.getUser().getName();

        // --- Module 1: Canvas data (each module has its own try/catch) ---
        List<Assignment> allAssignments = new ArrayList<>();
        List<Announcement> allAnnouncements = new ArrayList<>();
        String canvasError = null;

        try {
            canvasService.updateCourses();
            for (Course course : canvasService.getCourses()) {
                canvasService.updateAssignments(course);
                canvasService.updateAnnouncements(course);
                if (course.getAssignments() != null)   allAssignments.addAll(course.getAssignments());
                if (course.getAnnouncements() != null) allAnnouncements.addAll(course.getAnnouncements());
            }
        } catch (NetworkException e) {
            canvasError = "[Error] Could not load Canvas data: " + e.getMessage();
        }

        if (canvasError != null) {
            allAssignments = new ArrayList<>();
            allAnnouncements = new ArrayList<>();
            allAnnouncements.add(new Announcement("ERR", "Canvas Sync Error", canvasError, null));
        }

        // --- Module 2: Tasks (already loaded from disk) ---
        List<Task> todoList = taskManager.getTaskList();

        // --- Module 3: Gemini grade analysis ---
        String geminiGradeAnalysis;
        try {
            geminiGradeAnalysis = fetchGradeAnalysis(allAssignments);
        } catch (NetworkException e) {
            geminiGradeAnalysis = "[Error] Could not load grade analysis: " + e.getMessage();
        }

        // --- Module 4: Gemini daily insights ---
        String geminiInsights;
        try {
            geminiInsights = fetchDailyInsights(allAssignments, todoList);
        } catch (NetworkException e) {
            geminiInsights = "[Error] Could not load daily insights: " + e.getMessage();
        }

        return dashboardParser.buildDashboard(
                userName, allAssignments, allAnnouncements,
                todoList, geminiInsights, geminiGradeAnalysis);
    }

    private String fetchGradeAnalysis(List<Assignment> assignments) throws NetworkException {
        if (!geminiConnector.isConfigured()) return "[Info] Set GEMINI_API_KEY to enable AI grade analysis.";
        if (assignments.isEmpty())           return "[Info] No assignments found to analyse.";

        StringBuilder summary = new StringBuilder("Here is a student's current assignment status:\n");
        int count = 0;
        for (Assignment a : assignments) {
            if (count++ >= 20) break;
            String status  = a.getSubmission() != null ? a.getSubmission().getWorkflow_state() : "unsubmitted";
            boolean late    = a.getSubmission() != null && a.getSubmission().getLate();
            boolean missing = a.getSubmission() != null && a.getSubmission().getMissing();
            String due = (a.getDue_at() == null || a.getDue_at().equalsIgnoreCase("null"))
                    ? "no due date" : a.getDue_at().substring(0, 10);
            summary.append("- ").append(a.getName()).append(": ").append(status)
                    .append(late ? " (LATE)" : "").append(missing ? " (MISSING)" : "")
                    .append(", due: ").append(due).append("\n");
        }
        summary.append("\nGive a brief 2-3 sentence grade analysis with key concerns and encouragement. Be concise.");
        return geminiConnector.generate(summary.toString());
    }

    private String fetchDailyInsights(List<Assignment> assignments, List<Task> tasks) throws NetworkException {
        if (!geminiConnector.isConfigured()) return "[Info] Set GEMINI_API_KEY to enable AI daily insights.";

        StringBuilder prompt = new StringBuilder("You are a helpful student planner assistant.\n");
        prompt.append("A student has the following pending tasks today:\n");
        if (tasks.isEmpty()) {
            prompt.append("- No personal tasks.\n");
        } else {
            int count = 0;
            for (Task t : tasks) {
                if (count++ >= 10) break;
                prompt.append("- ").append(t.getTitle())
                        .append(" (deadline: ").append(t.getDeadline()).append(")\n");
            }
        }
        long dueToday = assignments.stream().filter(a -> {
            String due = a.getDue_at(), today = java.time.LocalDate.now().toString();
            return due != null && due.length() >= 10 && due.substring(0, 10).equals(today);
        }).count();
        prompt.append("They have ").append(dueToday).append(" Canvas assignment(s) due today.\n");
        prompt.append("Give 2-3 sentences of motivational daily insight and a practical tip. Be upbeat and concise.");
        return geminiConnector.generate(prompt.toString());
    }
}