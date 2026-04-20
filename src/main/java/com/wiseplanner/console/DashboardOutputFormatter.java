package com.wiseplanner.console;

import com.wiseplanner.model.*;
import java.util.List;

public class DashboardOutputFormatter {

    private static final int WIDTH = 100;
    private static final String DOUBLE_BORDER  = "═".repeat(WIDTH) + "\n";
    private static final String SINGLE_BORDER  = "─".repeat(WIDTH) + "\n";
    private static final String SECTION_DIVIDER = "┄".repeat(WIDTH) + "\n";

    public String getDashboardOutput(Dashboard dashboard) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n").append(DOUBLE_BORDER);
        sb.append(centred("  W I S E P L A N N E R   —   D A S H B O A R D  ")).append("\n");
        sb.append(DOUBLE_BORDER);

        sb.append(buildGreetingSection(dashboard.getGreeting()));      sb.append(SECTION_DIVIDER);
        sb.append(buildAiSection("📊  Grade Analysis (Gemini)", dashboard.getGeminiGradeAnalysis())); sb.append(SECTION_DIVIDER);
        sb.append(buildAiSection("💡  Daily Insights (Gemini)",  dashboard.getGeminiInsights()));     sb.append(SECTION_DIVIDER);
        sb.append(buildAssignmentsSection(dashboard.getTodaysAssignments()));   sb.append(SECTION_DIVIDER);
        sb.append(buildAnnouncementsSection(dashboard.getTodaysAnnouncements())); sb.append(SECTION_DIVIDER);
        sb.append(buildTodoSection(dashboard.getTodoList()));

        sb.append(DOUBLE_BORDER);
        return sb.toString();
    }

    private String buildGreetingSection(String greeting) {
        StringBuilder sb = new StringBuilder();
        sb.append(sectionHeader("👋  Greeting"));
        sb.append(greeting == null || greeting.isBlank()
                ? "  [Error] Could not generate greeting.\n"
                : "  " + greeting + "\n");
        return sb.toString();
    }

    private String buildAiSection(String title, String content) {
        StringBuilder sb = new StringBuilder();
        sb.append(sectionHeader(title));
        if (content == null || content.isBlank()) {
            sb.append("  [Error] No data available.\n");
        } else {
            for (String line : wordWrap(content, WIDTH - 4)) sb.append("  ").append(line).append("\n");
        }
        return sb.toString();
    }

    private String buildAssignmentsSection(List<Assignment> assignments) {
        StringBuilder sb = new StringBuilder();
        sb.append(sectionHeader("📅  Today's Assignments"));
        if (assignments == null) { sb.append("  [Error] Could not load assignments.\n"); return sb.toString(); }
        if (assignments.isEmpty()) { sb.append("  No assignments due today. Enjoy your day!\n"); return sb.toString(); }

        sb.append(String.format("  %-35s | %-12s | %-10s | %-6s | %-7s\n", "Assignment Name", "Due Time", "Status", "Late", "Missing"));
        sb.append("  ").append(SINGLE_BORDER.substring(2));

        for (Assignment a : assignments) {
            String dueAt  = a.getDue_at();
            String dueTime = (dueAt != null && !dueAt.equalsIgnoreCase("null") && dueAt.length() > 10)
                    ? dueAt.substring(11, 16) : "—";
            String status  = a.getSubmission() != null ? a.getSubmission().getWorkflow_state() : "unsubmitted";
            boolean late    = a.getSubmission() != null && a.getSubmission().getLate();
            boolean missing = a.getSubmission() != null && a.getSubmission().getMissing();
            sb.append(String.format("  %-35.35s | %-12s | %-10s | %-6s | %-7s\n",
                    a.getName(), dueTime, status, late ? "YES" : "NO", missing ? "YES" : "NO"));
        }
        return sb.toString();
    }

    private String buildAnnouncementsSection(List<Announcement> announcements) {
        StringBuilder sb = new StringBuilder();
        sb.append(sectionHeader("📢  Today's Announcements"));
        if (announcements == null) { sb.append("  [Error] Could not load announcements.\n"); return sb.toString(); }
        if (announcements.isEmpty()) { sb.append("  No new announcements today.\n"); return sb.toString(); }

        for (Announcement a : announcements) {
            if ("ERR".equals(a.getId())) { sb.append("  ").append(a.getMessage()).append("\n"); continue; }
            sb.append(String.format("  [%s]  %s\n", a.getPosted_at(), a.getTitle()));
            for (String line : wordWrap(stripHtml(a.getMessage()), WIDTH - 6)) sb.append("    ").append(line).append("\n");
        }
        return sb.toString();
    }

    private String buildTodoSection(List<Task> tasks) {
        StringBuilder sb = new StringBuilder();
        sb.append(sectionHeader("✅  To-Do List"));
        if (tasks == null || tasks.isEmpty()) { sb.append("  Your to-do list is empty. Add tasks from the Tasks menu.\n"); return sb.toString(); }

        sb.append(String.format("  %-4s | %-20s | %-15s | %s\n", "#", "Title", "Deadline", "Notes"));
        sb.append("  ").append(SINGLE_BORDER.substring(2));
        int idx = 1;
        for (Task t : tasks) {
            sb.append(String.format("  %-4d | %-20.20s | %-15s | %s\n",
                    idx++, t.getTitle(),
                    t.getDeadline() != null ? t.getDeadline() : "—",
                    t.getContent()  != null ? t.getContent()  : ""));
        }
        return sb.toString();
    }

    private String sectionHeader(String title) {
        String line = "  ┌─ " + title + " ";
        int remaining = Math.max(0, WIDTH - line.length() - 1);
        return line + "─".repeat(remaining) + "┐\n";
    }

    private String centred(String text) {
        int padding = (WIDTH - text.length()) / 2;
        return padding < 0 ? text : " ".repeat(padding) + text;
    }

    private String[] wordWrap(String text, int maxWidth) {
        if (text == null || text.isBlank()) return new String[]{""};
        if (text.length() <= maxWidth) return text.split("\n");
        java.util.List<String> lines = new java.util.ArrayList<>();
        for (String para : text.split("\n")) {
            if (para.length() <= maxWidth) { lines.add(para); continue; }
            StringBuilder cur = new StringBuilder();
            for (String word : para.split(" ")) {
                if (cur.length() + word.length() + 1 > maxWidth) { lines.add(cur.toString().trim()); cur = new StringBuilder(); }
                cur.append(word).append(" ");
            }
            if (!cur.toString().isBlank()) lines.add(cur.toString().trim());
        }
        return lines.toArray(new String[0]);
    }

    private String stripHtml(String html) {
        if (html == null || html.equalsIgnoreCase("null") || html.isBlank()) return "No content.";
        return html.replaceAll("<[^>]*>", "")
                .replaceAll("&nbsp;", " ").replaceAll("&amp;", "&")
                .replaceAll("&lt;", "<").replaceAll("&gt;", ">")
                .replaceAll("\\s+", " ").trim();
    }
}