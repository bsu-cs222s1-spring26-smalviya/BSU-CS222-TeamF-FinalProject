package com.wiseplanner.console;

import com.wiseplanner.exception.FileCorruptionException;
import com.wiseplanner.exception.FileReadException;
import com.wiseplanner.exception.FileWriteException;
import com.wiseplanner.exception.NetworkException;
import com.wiseplanner.model.Assignment;
import com.wiseplanner.service.UserManager;
import com.wiseplanner.service.WisePlannerKernel;

import java.io.*;
import java.util.List;
import java.util.Scanner;

public class ConsoleUI {
    Scanner scanner = new Scanner(System.in);
    WisePlannerKernel wisePlannerKernel;
    CanvasOutputFormatter canvasOutputFormatter = new CanvasOutputFormatter();
    TaskOutputFormatter taskOutputFormatter = new TaskOutputFormatter();

    public void show() {
        UserManager userManager = new UserManager();
        try {
            if (userManager.isLogin()) {
                wisePlannerKernel = new WisePlannerKernel(userManager.getUser());
            } else {
                System.out.println("Please enter your name: ");
                String name = scanner.nextLine();
                System.out.println("Please enter your Canvas LMS Access Token");
                String canvasToken = scanner.nextLine();
                userManager.setUser(name, canvasToken);
                wisePlannerKernel = new WisePlannerKernel(userManager.getUser());
            }
        } catch (FileCorruptionException | FileReadException | FileWriteException e) {
            System.err.println("[Error] " + e.getMessage());
            System.out.println("Please enter your name: ");
            String name = scanner.nextLine();
            System.out.println("Please enter your Canvas LMS Access Token");
            String canvasToken = scanner.nextLine();
            userManager.setUser(name, canvasToken);
            wisePlannerKernel = new WisePlannerKernel(userManager.getUser());
        }

        //Console User Interface
        while (true) {
            System.out.println("**********************************************************************");
            System.out.println("*                             Main Menu                              *");
            System.out.println("**********************************************************************");
            System.out.println("(1) Courses");
            System.out.println("(2) Task");
            System.out.println("(3) Assignments");
            System.out.println("(0) Exit");
            System.out.println("Please enter your choice");
            int choice = Integer.parseInt(scanner.nextLine());
            switch (choice) {
                case 0:
                    return;
                case 1:
                    try {
                        System.out.print(canvasOutputFormatter.getCoursesOutput(wisePlannerKernel.canvasService.getCourses()));
                    } catch (NetworkException e) {
                        System.err.println("[Error] " + e.getMessage());
                    }
                    System.out.println("Enter a Course ID to view its assignments, or 0 to go back:");
                    String courseId = scanner.nextLine();
                    if (!courseId.equals("0")) {
                        try {
                            List<Assignment> assignments = wisePlannerKernel.canvasService.getAssignments(courseId);
                            System.out.print(canvasOutputFormatter.getAssignmentsOutput(assignments));
                        } catch (NetworkException e) {
                            System.err.println("[Error] " + e.getMessage());
                        }
                    }
                    break;
                case 2:
                    System.out.println("(1) View Tasks");
                    System.out.println("(2) Add Task");
                    System.out.println("(3) Delete Task");
                    System.out.println("(0) Back");
                    System.out.println("Please enter your choice");
                    int choice_task = Integer.parseInt(scanner.nextLine());
                    switch (choice_task) {
                        case 0:
                            break;
                        case 1:
                            System.out.println(taskOutputFormatter.getTaskOutput(wisePlannerKernel.taskManager.getTaskList()));
                            break;
                        case 2:
                            System.out.println("Please enter task start time");
                            String timestamp = scanner.nextLine();
                            System.out.println("Please enter task title");
                            String title = scanner.nextLine();
                            System.out.println("Please enter task content");
                            String content = scanner.nextLine();
                            wisePlannerKernel.taskManager.addTask(timestamp, title, content);
                            break;
                        case 3:
                            System.out.println("Please enter the index");
                            int index = Integer.parseInt(scanner.nextLine());
                            try {
                                wisePlannerKernel.taskManager.deleteTask(index - 1);
                            } catch (IndexOutOfBoundsException e) {
                                System.err.println("[Error] " + e.getMessage());
                            }
                            break;
                    }
                    break;
                case 3:
                    System.out.println("Enter a Course ID to view its assignments:");
                    String directCourseId = scanner.nextLine();
                    try {
                        List<Assignment> assignments = wisePlannerKernel.canvasService.getAssignments(directCourseId);
                        System.out.print(canvasOutputFormatter.getAssignmentsOutput(assignments));
                    } catch (NetworkException e) {
                        System.err.println("[Error] " + e.getMessage());
                    }
                    break;
                default:
                    continue;
            }
        }
    }
}