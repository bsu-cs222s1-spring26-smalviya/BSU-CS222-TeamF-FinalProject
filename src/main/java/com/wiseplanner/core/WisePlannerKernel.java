package com.wiseplanner.core;

import com.wiseplanner.exception.FileCorruptionException;
import com.wiseplanner.exception.FileReadException;
import com.wiseplanner.model.User;
import com.wiseplanner.service.DashboardService;
import com.wiseplanner.service.*;

public class WisePlannerKernel {
    private UserManager userManager = new UserManager();
    private CanvasService canvasService;
    private DashboardService dashboardService;
    private TaskManager taskManager;
    private ScheduleManager scheduleManager;
    private static WisePlannerKernel instance;

    public static WisePlannerKernel getInstance() {
        if (instance == null) instance = new WisePlannerKernel();
        return instance;
    }

    public void initialize() throws FileReadException {
        canvasService = new CanvasService(userManager.getUser());
        taskManager = new TaskManager();
        taskManager.loadTask();
        scheduleManager = new ScheduleManager();
        scheduleManager.loadSchedule();
        dashboardService = new DashboardService(canvasService, taskManager, userManager);
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

    public ScheduleManager schedule() {
        return scheduleManager;
    }

    public DashboardService dashboard() {return dashboardService;}
}
