package com.wiseplanner.console;

import com.wiseplanner.service.Assignment;
import com.wiseplanner.service.Course;

import java.util.List;

public class CanvasOutputFormatter {
    public String getCoursesOutput(List<Course> courses) {
        StringBuilder output = new StringBuilder();
        System.out.println("**********************************************************************");
        System.out.println("*                               Courses                              *");
        System.out.println("**********************************************************************");
        System.out.println("ID                  |  Course Name");
        System.out.println("----------------------------------------------------------------------");
        for (Course i : courses) {
            output.append(i.getId() + "\t" + "|  " + i.getName() + "\n");
        }
        return output.toString();
    }

    public String getAssignmentsOutput(List<Assignment> assignments) {
        StringBuilder output = new StringBuilder();
        output.append("**********************************************************************\n");
        output.append("*                            Assignments                             *\n");
        output.append("**********************************************************************\n");
        output.append(String.format("%-20s | %-25s | %s\n", "Name", "Due Date", "Description"));
        output.append("----------------------------------------------------------------------\n");
        for (Assignment a : assignments) {
            String cleanDescription = stripHtml(a.getDescription());
            String dueDate = (a.getDue_at() == null || a.getDue_at().equals("null"))
                    ? "No Due Date"
                    : a.getDue_at();
            output.append(String.format("%-20s | %-25s | %s\n",
                    a.getName(), dueDate, cleanDescription));
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
}
