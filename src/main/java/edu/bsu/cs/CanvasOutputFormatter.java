package edu.bsu.cs;

import java.util.List;

public class CanvasOutputFormatter {
    public String getCoursesOutput(List<Course> courses) {
        StringBuilder output = new StringBuilder();
        output.append("Course\n" +
                "ID\tName\n");
        for (Course i : courses) {
            output.append(i.getId() + "\t" + i.getName() + "\n");
        }
        return output.toString();
    }

    public String getAssignmentsOutput(List<Assignment> assignments) {
        StringBuilder output = new StringBuilder();
        output.append("Assignments\n");
        output.append("ID\tName\tDue Date\tDescription\n");
        for (Assignment a : assignments) {
            output.append(a.getId() + "\t" + a.getName() + "\t" + a.getDueAt() + "\t" + a.getDescription() + "\n");
        }
        return output.toString();
    }
}
