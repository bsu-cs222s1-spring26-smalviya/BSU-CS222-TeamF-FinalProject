package com.wiseplanner.service;

import com.wiseplanner.exception.NetworkException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public class CanvasService {
    private User user;
    private List<Course> courses;
    private List<Assignment> assignments;

    public CanvasService(User user) {
        this.user = user;
    }

    public List<Course> getCourses() throws NetworkException {
        CourseParser courseParser = new CourseParser(new CanvasConnector(user).fetchCourses());
        courses = courseParser.getCourses();
        return this.courses;
    }

    public List<Assignment> getAssignments(String courseId) throws NetworkException {
        String jsonData = new CanvasConnector(user).fetchAssignments(courseId);
        AssignmentParser parser = new AssignmentParser(jsonData);
        assignments = parser.getAssignments();

        // Sort by due date (nulls last) BUG: Crash when no due date.
//        assignments.sort((a, b) -> {
//            if (a.getDue_at().equals("null")) return 1;
//            if (b.getDue_at().equals("null")) return -1;
//            return a.getDue_at().compareTo(b.getDue_at());
//        });

        return this.assignments;
    }
}
