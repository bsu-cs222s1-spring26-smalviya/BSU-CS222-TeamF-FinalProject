package com.wiseplanner.service;

import com.wiseplanner.exception.NetworkException;
import com.wiseplanner.model.Course;
import com.wiseplanner.model.User;
import com.wiseplanner.util.parser.AnnouncementParser;
import com.wiseplanner.util.parser.AssignmentParser;
import com.wiseplanner.util.CanvasConnector;
import com.wiseplanner.util.parser.CourseParser;

import java.util.List;

public class CanvasService {
    private User user;
    private List<Course> courses;

    public CanvasService(User user) {
        this.user = user;
    }

    public void updateAll() throws NetworkException {
        updateCourses();
        for (Course i : courses) {
            updateAssignments(i);
            updateAnnouncements(i);
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
        List<String> jsonData = new CanvasConnector(user).fetchAssignments(course);
        AssignmentParser parser = new AssignmentParser(jsonData);


        course.setAssignments(parser.getAssignments());
    }

    public void updateAnnouncements(Course course) throws NetworkException {
        String jsonData = new CanvasConnector(user).fetchAnnouncements(course);
        AnnouncementParser announcementParser = new AnnouncementParser(jsonData);
        course.setAnnouncements(announcementParser.getAnnouncements());
    }
}
