package com.wiseplanner.service;

import com.wiseplanner.model.User;

public class WisePlannerKernel {
    public User user;
    public CanvasService canvasService;
    public TaskManager taskManager;

    public WisePlannerKernel(User user) {
        this.user = user;
        canvasService = new CanvasService(user);
        taskManager = new TaskManager();
    }
}
