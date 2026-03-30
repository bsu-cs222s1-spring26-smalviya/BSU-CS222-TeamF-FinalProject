package com.wiseplanner.console;

import com.wiseplanner.exception.FileCorruptionException;
import com.wiseplanner.exception.FileReadException;
import com.wiseplanner.exception.FileWriteException;
import com.wiseplanner.exception.NetworkException;
import com.wiseplanner.core.WisePlannerKernel;

import java.util.Scanner;

public class ConsoleUI {
    Scanner scanner = new Scanner(System.in);
    WisePlannerKernel wisePlannerKernel = new WisePlannerKernel();
    CanvasOutputFormatter canvasOutputFormatter = new CanvasOutputFormatter();
    TaskOutputFormatter taskOutputFormatter = new TaskOutputFormatter();

    public void show() {
        try {
            if (!wisePlannerKernel.user().isLogin()) {
                System.out.println("Please enter your name: ");
                String name = scanner.nextLine();
                System.out.println("Please enter your Canvas LMS Access Token");
                String canvasToken = scanner.nextLine();
                wisePlannerKernel.user().setUser(name, canvasToken);
            }
        } catch (FileCorruptionException | FileReadException e) {
            System.err.println("[Error] " + e.getMessage());
            System.out.println("Please enter your name: ");
            String name = scanner.nextLine();
            System.out.println("Please enter your Canvas LMS Access Token");
            String canvasToken = scanner.nextLine();
            wisePlannerKernel.user().setUser(name, canvasToken);
        } catch (FileWriteException e) {
            System.err.println("[Error] " + e.getMessage());
        }
        try {
            wisePlannerKernel.initialize();
        } catch (FileReadException e) {
            System.err.println("[Error] " + e.getMessage());
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
                    System.out.println("(1) View Assignments");
                    System.out.println("(2) View Announcements");
                    System.out.println("(0) Back");
                    System.out.println("Please enter your choice");
                    int choice_course = Integer.parseInt(scanner.nextLine());
                    switch (choice_course) {
                        // Back
                        case 0:
                            break;
                        // View Assignments
                        case 1:
                            try {
                                wisePlannerKernel.canvas().updateAssignments(wisePlannerKernel.canvas().getCourses().get(courseIndex - 1));
                                System.out.print(canvasOutputFormatter.getAssignmentsOutput(wisePlannerKernel.canvas().getCourses().get(courseIndex - 1).getAssignments()));
                            } catch (NetworkException e) {
                                System.err.println("[Error] " + e.getMessage());
                            }
                            break;
                        // View Announcements
                        case 2:
                            try {
                                wisePlannerKernel.canvas().updateAnnouncements(wisePlannerKernel.canvas().getCourses().get(courseIndex - 1));
                                System.out.println(canvasOutputFormatter.getAnnouncementsOutput(wisePlannerKernel.canvas().getCourses().get(courseIndex - 1).getAnnouncements()));
                            } catch (NetworkException e) {
                                System.err.println("[Error] " + e.getMessage());
                            }

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
                            System.out.println("Please enter task deadline");
                            String deadline = scanner.nextLine();
                            System.out.println("Please enter task title");
                            String title = scanner.nextLine();
                            System.out.println("Please enter task content");
                            String content = scanner.nextLine();
                            try {
                                wisePlannerKernel.task().addTask(deadline, title, content);
                            } catch (FileWriteException e) {
                                System.err.println("[Error] " + e.getMessage());
                            }
                            break;
                        // Delete Task
                        case 3:
                            System.out.println("Please enter the index");
                            int index = Integer.parseInt(scanner.nextLine());
                            try {
                                wisePlannerKernel.task().deleteTask(index - 1);
                            } catch (IndexOutOfBoundsException | FileWriteException e) {
                                System.err.println("[Error] " + e.getMessage());
                            }
                            break;
                    }
                    break;
            }
        }
    }
}