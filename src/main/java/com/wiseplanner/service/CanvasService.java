package com.wiseplanner.service;

import com.wiseplanner.exception.NetworkException;
import com.wiseplanner.model.Assignment;
import com.wiseplanner.model.Course;
import com.wiseplanner.model.User;

import java.util.List;

public class CanvasService {
    private User user;
    private List<Course> courses;

    public CanvasService(User user) {
        this.user = user;
    }

    public void updateAll() {
        updateCourses();
        for (Course i : courses) {
            updateAssignments(i);
        }
    }

    public void updateCourses() throws NetworkException {
        CourseParser courseParser = new CourseParser(new CanvasConnector(user).fetchCourses());
        courses = courseParser.getCourses();
    }

    public List<Course> getCourses() {
        return courses;
    }

    public void updateAssignments(Course course) throws NetworkException {
        String jsonData = new CanvasConnector(user).fetchAssignments(course);
        AssignmentParser parser = new AssignmentParser(jsonData);

        // Sort by due date (nulls last) BUG: Crash when no due date.
//        assignments.sort((a, b) -> {
//            if (a.getDue_at().equals("null")) return 1;
//            if (b.getDue_at().equals("null")) return -1;
//            return a.getDue_at().compareTo(b.getDue_at());
//        });

        course.setAssignments(parser.getAssignments());
    }
}
