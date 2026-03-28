package com.wiseplanner.core;

import com.wiseplanner.model.User;
import com.wiseplanner.service.CanvasService;
import com.wiseplanner.service.TaskManager;

public class WisePlannerKernel {
    private User user;
    private CanvasService canvasService;
    private TaskManager taskManager;

    public WisePlannerKernel(User user) {
        this.user = user;
        canvasService = new CanvasService(user);
        taskManager = new TaskManager();
    }

    public CanvasService canvas() {
        return canvasService;
    }

    public TaskManager task() {
        return taskManager;
    }
}
