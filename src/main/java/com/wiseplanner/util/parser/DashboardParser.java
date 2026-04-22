package com.wiseplanner.util.parser;

import com.wiseplanner.model.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class DashboardParser {

    private static final int DATE_PREFIX_LENGTH = 10;

    private static final String[] QUOTES = {
            "The secret of getting ahead is getting started. — Mark Twain",
            "It always seems impossible until it's done. — Nelson Mandela",
            "Believe you can and you're halfway there. — Theodore Roosevelt",
            "You don't have to be great to start, but you have to start to be great. — Zig Ziglar",
            "Hard work beats talent when talent doesn't work hard. — Tim Notke",
            "Success is the sum of small efforts, repeated day in and day out. — Robert Collier",
            "The expert in anything was once a beginner. — Helen Hayes",
            "Don't watch the clock; do what it does. Keep going. — Sam Levenson",
            "Push yourself, because no one else is going to do it for you.",
            "Great things never come from comfort zones.",
            "Dream it. Wish it. Do it.",
            "Success doesn't just find you. You have to go out and get it.",
            "The harder you work for something, the greater you'll feel when you achieve it.",
            "Don't stop when you're tired. Stop when you're done.",
            "Wake up with determination. Go to bed with satisfaction."
    };

    public Dashboard buildDashboard(
            String userName,
            List<Assignment> allAssignments,
            List<Announcement> allAnnouncements,
            List<Task> todoList,
            String geminiInsights,
            String geminiGradeAnalysis,
            List<Course> allCourses
    ) {
        Dashboard dashboard = new Dashboard();
        dashboard.setGreeting(buildGreeting(userName));
        dashboard.setTodaysAssignments(filterAssignmentsDueToday(allAssignments));
        dashboard.setTodaysAnnouncements(sortedAnnouncements(allAnnouncements));
        dashboard.setTodoList(todoList != null ? todoList : new ArrayList<>());
        dashboard.setGeminiInsights(geminiInsights);
        dashboard.setGeminiGradeAnalysis(geminiGradeAnalysis);
        dashboard.setAllCourses(allCourses != null ? allCourses : new ArrayList<>());
        return dashboard;
    }


    public Dashboard buildDashboard(
            String userName,
            List<Assignment> allAssignments,
            List<Announcement> allAnnouncements,
            List<Task> todoList,
            String geminiInsights,
            String geminiGradeAnalysis
    ) {
        return buildDashboard(userName, allAssignments, allAnnouncements,
                todoList, geminiInsights, geminiGradeAnalysis, new ArrayList<>());
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

        int quoteIndex = LocalDate.now().getDayOfYear() % QUOTES.length;
        return timeGreeting + ", " + userName + "! Today is " + todayFormatted
                + ".\n\n💬 " + QUOTES[quoteIndex];
    }

    private List<Assignment> filterAssignmentsDueToday(List<Assignment> assignments) {
        if (assignments == null) return new ArrayList<>();
        String today = LocalDate.now().toString();
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

    private List<Announcement> sortedAnnouncements(List<Announcement> announcements) {
        if (announcements == null) return new ArrayList<>();
        return announcements.stream()
                .filter(a -> !"ERR".equals(a.getId()))
                .sorted(Comparator.comparing(
                        a -> (a.getPosted_at() == null ? "" : a.getPosted_at()),
                        Comparator.reverseOrder()))
                .limit(10)
                .collect(Collectors.toCollection(ArrayList::new));
    }
}