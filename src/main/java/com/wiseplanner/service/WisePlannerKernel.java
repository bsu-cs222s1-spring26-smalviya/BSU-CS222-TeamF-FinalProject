package com.wiseplanner.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public class WisePlannerKernel {
    public User user;
    public CanvasService canvasService;
    public TaskManager taskManager;

    public WisePlannerKernel(User user) {
        this.user = user;
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
