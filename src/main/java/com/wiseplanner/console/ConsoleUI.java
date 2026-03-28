package com.wiseplanner.console;

import com.wiseplanner.exception.FileCorruptionException;
import com.wiseplanner.exception.FileReadException;
import com.wiseplanner.exception.FileWriteException;
import com.wiseplanner.exception.NetworkException;
import com.wiseplanner.service.UserManager;
import com.wiseplanner.core.WisePlannerKernel;

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
            System.out.println("(2) Tasks");
            System.out.println("(0) Exit");
            System.out.println("Please enter your choice");
            int choice = Integer.parseInt(scanner.nextLine());
            switch (choice) {
                // Exit
                case 0:
                    return;
                // Courses
                case 1:
                    try {
                        wisePlannerKernel.canvas().updateCourses();
                        System.out.print(canvasOutputFormatter.getCoursesOutput(wisePlannerKernel.canvas().getCourses()));
                    } catch (NetworkException e) {
                        System.err.println("[Error] " + e.getMessage());
                    }
                    System.out.println("Please enter the index of the course you want to view");
                    int courseIndex = Integer.parseInt(scanner.nextLine());
                    System.out.println("(1) View Assignment");
                    System.out.println("(0) Back");
                    System.out.println("Please enter your choice");
                    int choice_course = Integer.parseInt(scanner.nextLine());
                    switch (choice_course) {
                        // Back
                        case 0:
                            break;
                        // View Assignment
                        case 1:
                            try {
                                wisePlannerKernel.canvas().updateAssignments(wisePlannerKernel.canvas().getCourses().get(courseIndex - 1));
                                System.out.print(canvasOutputFormatter.getAssignmentsOutput(wisePlannerKernel.canvas().getCourses().get(courseIndex - 1).getAssignments()));
                            } catch (NetworkException e) {
                                System.err.println("[Error] " + e.getMessage());
                            }
                            break;
                    }
                    break;
                //Tasks
                case 2:
                    System.out.println("(1) View Tasks");
                    System.out.println("(2) Add Task");
                    System.out.println("(3) Delete Task");
                    System.out.println("(0) Back");
                    System.out.println("Please enter your choice");
                    int choice_task = Integer.parseInt(scanner.nextLine());
                    switch (choice_task) {
                        // Back
                        case 0:
                            break;
                        // View Tasks
                        case 1:
                            System.out.println(taskOutputFormatter.getTaskOutput(wisePlannerKernel.task().getTaskList()));
                            break;
                        // Add Task
                        case 2:
                            System.out.println("Please enter task start time");
                            String timestamp = scanner.nextLine();
                            System.out.println("Please enter task title");
                            String title = scanner.nextLine();
                            System.out.println("Please enter task content");
                            String content = scanner.nextLine();
                            wisePlannerKernel.task().addTask(timestamp, title, content);
                            break;
                        // Delete Task
                        case 3:
                            System.out.println("Please enter the index");
                            int index = Integer.parseInt(scanner.nextLine());
                            try {
                                wisePlannerKernel.task().deleteTask(index - 1);
                            } catch (IndexOutOfBoundsException e) {
                                System.err.println("[Error] " + e.getMessage());
                            }
                            break;
                    }
                    break;
            }
        }
    }
}