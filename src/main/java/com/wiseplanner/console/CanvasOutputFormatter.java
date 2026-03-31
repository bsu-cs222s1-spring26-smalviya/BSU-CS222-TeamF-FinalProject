package com.wiseplanner.console;

import com.wiseplanner.model.Announcement;
import com.wiseplanner.model.Assignment;
import com.wiseplanner.model.Course;

import java.util.List;

public class CanvasOutputFormatter {

    private static final String LINE_SEPARATOR = "----------------------------------------------------------------------------------------------------\n";
    private static final String HEADER_BORDER = "****************************************************************************************************\n";

    public String getCoursesOutput(List<Course> courses) {
        StringBuilder sb = new StringBuilder();
        sb.append(HEADER_BORDER);
        sb.append(String.format("* %-96s *\n", "Courses"));
        sb.append(HEADER_BORDER);

        sb.append(String.format("%-6s | %-12s | %-40s\n", "Index", "ID", "Course Name"));
        sb.append(LINE_SEPARATOR);

        for (int i = 0; i < courses.size(); i++) {
            Course c = courses.get(i);
            sb.append(String.format("%-6d | %-12s | %-40s\n",
                    (i + 1), c.getId(), c.getName()));
        }
        return sb.toString();
    }

    public String getAssignmentsOutput(List<Assignment> assignments) {
        StringBuilder sb = new StringBuilder();
        sb.append(HEADER_BORDER);
        sb.append(String.format("* %-96s *\n", "Assignments"));
        sb.append(HEADER_BORDER);

        sb.append(String.format("%-25s | %-20s | %-10s | %-6s | %-7s | %s\n",
                "Name", "Due Date", "Status", "Late", "Missing", "Description"));
        sb.append(LINE_SEPARATOR);

        for (Assignment a : assignments) {
            String dueDate = (a.getDue_at() == null || a.getDue_at().equalsIgnoreCase("null"))
                    ? "No Due Date"
                    : a.getDue_at();

            String workflow = a.getSubmission() != null ? a.getSubmission().getWorkflow_state() : "unsubmitted";
            boolean isLate = a.getSubmission() != null && a.getSubmission().getLate();
            boolean isMissing = a.getSubmission() != null && a.getSubmission().getMissing();

            sb.append(String.format("%-25.25s | %-20.20s | %-10s | %-6s | %-7s | %s\n",
                    a.getName(),
                    dueDate,
                    workflow,
                    isLate ? "YES" : "NO",
                    isMissing ? "YES" : "NO",
                    stripHtml(a.getDescription())));
        }
        return sb.toString();
    }

    public String getAnnouncementsOutput(List<Announcement> announcements) {
        StringBuilder sb = new StringBuilder();
        sb.append(HEADER_BORDER);
        sb.append(String.format("* %-96s *\n", "Announcements"));
        sb.append(HEADER_BORDER);
        sb.append(String.format("%-10s | %-25s | %-20s | %s\n", "ID", "Title", "Posted At", "Message"));
        sb.append(LINE_SEPARATOR);

        for (Announcement i : announcements) {
            sb.append(String.format("%-10s | %-25.25s | %-20s | %s\n",
                    i.getId(), i.getTitle(), i.getPosted_at(), stripHtml(i.getMessage())));
        }
        return sb.toString();
    }

    private String stripHtml(String html) {
        if (html == null || html.equalsIgnoreCase("null") || html.isBlank()) {
            return "No Content";
        }

        String cleaned = html.replaceAll("<[^>]*>", "");
        cleaned = cleaned.replaceAll("&nbsp;", " ");
        cleaned = cleaned.replaceAll("\\s+", " ").trim();
        if (cleaned.length() > 60) {
            return cleaned.substring(0, 57) + "...";
        }
        return cleaned;
    }
}