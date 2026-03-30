package com.wiseplanner.core;

import com.wiseplanner.exception.FileCorruptionException;
import com.wiseplanner.exception.FileReadException;
import com.wiseplanner.model.User;
import com.wiseplanner.service.CanvasService;
import com.wiseplanner.service.TaskManager;
import com.wiseplanner.service.UserManager;

public class WisePlannerKernel {
    private UserManager userManager = new UserManager();
    private CanvasService canvasService;
    private TaskManager taskManager;
    private static WisePlannerKernel instance;

    public static WisePlannerKernel getInstance() {
        if (instance == null) instance = new WisePlannerKernel();
        return instance;
    }

    public void initialize() throws FileReadException {
        canvasService = new CanvasService(userManager.getUser());
        taskManager = new TaskManager();
        taskManager.loadTask();
    }

    public UserManager user() {
        return userManager;
    }

    public CanvasService canvas() {
        return canvasService;
    }

    public TaskManager task() {
        return taskManager;
    }
}
