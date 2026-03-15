package edu.bsu.cs;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public class CanvasService {
    private User user;
    public List<Course> courses;

    public CanvasService(User user) {
        this.user = user;
    }

    public void accessCourses() throws IOException, URISyntaxException {
        CourseParser courseParser = new CourseParser(new CanvasConnector(user).fetchCourses());
        courses = courseParser.getCourses();
    }
}
