package com.wiseplanner.util.parser;

import com.wiseplanner.model.Announcement;
import com.wiseplanner.model.Assignment;
import com.wiseplanner.model.Dashboard;
import com.wiseplanner.model.Task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DashboardParser {

    private static final int DATE_PREFIX_LENGTH = 10; // "2024-04-18" from "2024-04-18T23:59:00Z"

    public Dashboard buildDashboard(
            String userName,
            List<Assignment> allAssignments,
            List<Announcement> allAnnouncements,
            List<Task> todoList,
            String geminiInsights,
            String geminiGradeAnalysis
    ) {
        Dashboard dashboard = new Dashboard();
        dashboard.setGreeting(buildGreeting(userName));
        dashboard.setTodaysAssignments(filterAssignmentsDueToday(allAssignments));
        dashboard.setTodaysAnnouncements(filterAnnouncementsPostedToday(allAnnouncements));
        dashboard.setTodoList(todoList != null ? todoList : new ArrayList<>());
        dashboard.setGeminiInsights(geminiInsights);
        dashboard.setGeminiGradeAnalysis(geminiGradeAnalysis);
        return dashboard;
    }

    private String buildGreeting(String userName) {
        int hour = java.time.LocalTime.now().getHour();
        String timeGreeting;
        if      (hour >= 5  && hour < 12) timeGreeting = "Good morning";
        else if (hour >= 12 && hour < 17) timeGreeting = "Good afternoon";
        else if (hour >= 17 && hour < 21) timeGreeting = "Good evening";
        else                              timeGreeting = "Good night";

        String todayFormatted = LocalDate.now()
                .format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy"));
        return timeGreeting + ", " + userName + "! Today is " + todayFormatted + ".";
    }

    private List<Assignment> filterAssignmentsDueToday(List<Assignment> assignments) {
        if (assignments == null) return new ArrayList<>();
        String today = LocalDate.now().toString(); // e.g. "2024-04-18"
        List<Assignment> result = new ArrayList<>();
        for (Assignment a : assignments) {
            String dueAt = a.getDue_at();
            if (dueAt != null
                    && !dueAt.equalsIgnoreCase("null")
                    && dueAt.length() >= DATE_PREFIX_LENGTH
                    && dueAt.substring(0, DATE_PREFIX_LENGTH).equals(today)) {
                result.add(a);
            }
        }
        return result;
    }

    private List<Announcement> filterAnnouncementsPostedToday(List<Announcement> announcements) {
        if (announcements == null) return new ArrayList<>();
        String today = LocalDate.now().toString();
        List<Announcement> result = new ArrayList<>();
        for (Announcement a : announcements) {
            String postedAt = a.getPosted_at();
            if (postedAt != null
                    && !postedAt.equalsIgnoreCase("null")
                    && postedAt.length() >= DATE_PREFIX_LENGTH
                    && postedAt.substring(0, DATE_PREFIX_LENGTH).equals(today)) {
                result.add(a);
            }
        }
        return result;
    }
}