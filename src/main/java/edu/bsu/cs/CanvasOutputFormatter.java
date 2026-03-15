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
}
