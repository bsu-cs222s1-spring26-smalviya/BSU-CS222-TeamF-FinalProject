package edu.bsu.cs;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public class WisePlannerKernel {
    User user;
    CanvasService canvasService;
    TaskManager taskManager;

    public WisePlannerKernel(String name, String canvasToken) {
        user = new User(name, canvasToken);
        canvasService = new CanvasService(user);
        taskManager = new TaskManager();
    }

    public List<Course> getCourses() throws IOException, URISyntaxException {
        canvasService.updateCourses();
        return canvasService.getCourses();
    }

    public List<Assignment> getAssignments(String courseId) throws IOException, URISyntaxException {
        return canvasService.getAssignments(courseId);
    }
}
