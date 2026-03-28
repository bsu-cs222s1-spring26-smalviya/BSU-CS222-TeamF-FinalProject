package com.wiseplanner.console;

import com.wiseplanner.model.Announcement;
import com.wiseplanner.model.Assignment;
import com.wiseplanner.model.Course;
import com.wiseplanner.util.AnnouncementParser;

import java.util.List;

public class CanvasOutputFormatter {
    public String getCoursesOutput(List<Course> courses) {
        StringBuilder output = new StringBuilder();
        System.out.println("**********************************************************************");
        System.out.println("*                               Courses                              *");
        System.out.println("**********************************************************************");
        System.out.println("Index    ID                  |  Course Name");
        System.out.println("----------------------------------------------------------------------");
        for (int i = 0; i < courses.size(); i++) {
            output.append(i + 1 + "\t" + courses.get(i).getId() + "\t" + "|  " + courses.get(i).getName() + "\n");
        }
        return output.toString();
    }

    public String getAssignmentsOutput(List<Assignment> assignments) {
        StringBuilder output = new StringBuilder();
        output.append("**********************************************************************\n");
        output.append("*                            Assignments                             *\n");
        output.append("**********************************************************************\n");
        output.append(String.format("%-20s | %-25s | %s   %s   %s   %s\n", "Name", "Due Date", "Description", "Workflow State", "Late", "Missing"));
        output.append("----------------------------------------------------------------------\n");
        for (Assignment a : assignments) {
            String cleanDescription = stripHtml(a.getDescription());
            String dueDate = (a.getDue_at() == null || a.getDue_at().equals("null"))
                    ? "No Due Date"
                    : a.getDue_at();
            output.append(String.format("%-20s | %-25s | %s | %s | %s | %s\n",
                    a.getName(), dueDate, cleanDescription,
                    a.getSubmission().getWorkflow_state(),
                    a.getSubmission().getLate(),
                    a.getSubmission().getMissing()));
        }
        return output.toString();
    }

    private String stripHtml(String html) {
        if (html == null || html.equals("null")) {
            return "No Description";
        }

        String noTags = html.replaceAll("<[^>]*>", "");
        String cleaned = noTags.replaceAll("\\s+", " ").trim();
        if (cleaned.length() > 80) {
            return cleaned.substring(0, 77) + "...";
        }

        return cleaned;
    }

    public String getAnnouncementsOutput(List<Announcement> announcements) {
        StringBuilder output = new StringBuilder();
        output.append("**********************************************************************\n");
        output.append("*                            Announcements                             *\n");
        output.append("**********************************************************************\n");
        output.append(String.format("%s | %s | %s |  %s\n", "ID", "Title", "Message", "Posted at"));
        for (Announcement i : announcements) {
            String cleanMessage = stripHtml(i.getMessage());
            output.append(String.format("%s | %s | %s |  %s\n",
                    i.getId(), i.getTitle(), cleanMessage, i.getPosted_at()));
        }
        return output.toString();
    }
}
