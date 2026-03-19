package edu.bsu.cs;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public class CanvasService {
    private User user;
    private List<Course> courses;

    public CanvasService(User user) {
        this.user = user;
    }

    public void updateCourses() throws IOException, URISyntaxException {
        CourseParser courseParser = new CourseParser(new CanvasConnector(user).fetchCourses());
        courses = courseParser.getCourses();
    }

    public List<Course> getCourses() {
        return this.courses;
    }

    public List<Assignment> getAssignments(String courseId) throws IOException, URISyntaxException {
        String jsonData = new CanvasConnector(user).fetchAssignments(courseId);
        AssignmentParser parser = new AssignmentParser(jsonData);
        List<Assignment> assignments = parser.getAssignments();

        // Sort by due date (nulls last)
        assignments.sort((a, b) -> {
            if (a.getDueAt().equals("null")) return 1;
            if (b.getDueAt().equals("null")) return -1;
            return a.getDueAt().compareTo(b.getDueAt());
        });

        return assignments;
    }
}
