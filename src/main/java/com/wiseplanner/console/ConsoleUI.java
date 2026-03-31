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

    private void handleLogin() {
        System.out.println("Please enter your name: ");
        String name = scanner.nextLine();
        System.out.println("Please enter your Canvas LMS Access Token");
        String canvasToken = scanner.nextLine();
        wisePlannerKernel.user().setUser(name, canvasToken);
    }


    private int safeReadInt() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1; //
        }
    }

    public void show() {

        try {
            if (!wisePlannerKernel.user().isLogin()) {
                handleLogin();
            }
        } catch (FileCorruptionException | FileReadException e) {
            System.err.println("[Error] " + e.getMessage());
            handleLogin(); //
        } catch (FileWriteException e) {
            System.err.println("[Error] " + e.getMessage());
        }

        try {
            wisePlannerKernel.initialize();
        } catch (FileReadException e) {
            System.err.println("[Error] " + e.getMessage());
        }

        // Console User Interface
        while (true) {
            System.out.println("\n**********************************************************************");
            System.out.println("*                             Main Menu                              *");
            System.out.println("**********************************************************************");
            System.out.println("(1) Courses\n(2) Tasks\n(0) Exit\nPlease enter your choice:");

            int choice = safeReadInt();
            switch (choice) {
                case 0: // Exit
                    return;

                case 1: // Courses
                    try {
                        wisePlannerKernel.canvas().updateCourses();
                        System.out.print(canvasOutputFormatter.getCoursesOutput(wisePlannerKernel.canvas().getCourses()));
                    } catch (NetworkException e) {
                        System.err.println("[Error] " + e.getMessage());
                    }

                    System.out.println("Please enter the index of the course you want to view (0 to cancel):");
                    int courseIndex = safeReadInt();

                    if (courseIndex <= 0 || courseIndex > wisePlannerKernel.canvas().getCourses().size()) {
                        System.out.println("Invalid course index.");
                        break;
                    }

                    System.out.println("(1) View Assignments\n(2) View Announcements\n(0) Back\nPlease enter your choice:");
                    int choice_course = safeReadInt();

                    var selectedCourse = wisePlannerKernel.canvas().getCourses().get(courseIndex - 1);
                    switch (choice_course) {
                        case 0: // Back
                            break;
                        case 1: // View Assignments
                            try {
                                wisePlannerKernel.canvas().updateAssignments(selectedCourse);
                                System.out.print(canvasOutputFormatter.getAssignmentsOutput(selectedCourse.getAssignments()));
                            } catch (NetworkException e) {
                                System.err.println("[Error] " + e.getMessage());
                            }
                            break;
                        case 2: // View Announcements
                            try {
                                wisePlannerKernel.canvas().updateAnnouncements(selectedCourse);
                                System.out.println(canvasOutputFormatter.getAnnouncementsOutput(selectedCourse.getAnnouncements()));
                            } catch (NetworkException e) {
                                System.err.println("[Error] " + e.getMessage());
                            }
                            break;
                    }
                    break;

                case 2: // Tasks
                    System.out.println("(1) View Tasks\n(2) Add Task\n(3) Delete Task\n(0) Back\nPlease enter your choice:");
                    int choice_task = safeReadInt();
                    switch (choice_task) {
                        case 0: // Back
                            break;
                        case 1: // View Tasks
                            System.out.println(taskOutputFormatter.getTaskOutput(wisePlannerKernel.task().getTaskList()));
                            break;
                        case 2: // Add Task
                            System.out.println("Please enter task deadline:");
                            String deadline = scanner.nextLine();
                            System.out.println("Please enter task title:");
                            String title = scanner.nextLine();
                            System.out.println("Please enter task content:");
                            String content = scanner.nextLine();
                            try {
                                wisePlannerKernel.task().addTask(deadline, title, content);
                            } catch (FileWriteException e) {
                                System.err.println("[Error] " + e.getMessage());
                            }
                            break;
                        case 3: // Delete Task
                            System.out.println("Please enter the index:");
                            int index = safeReadInt();
                            try {

                                if (index > 0 && index <= wisePlannerKernel.task().getTaskList().size()) {
                                    wisePlannerKernel.task().deleteTask(index - 1);
                                } else {
                                    System.err.println("[Error] Index out of range.");
                                }
                            } catch (FileWriteException e) {
                                System.err.println("[Error] " + e.getMessage());
                            }
                            break;
                    }
                    break;

                default:
                    System.out.println("Invalid input, please try again.");
                    break;
            }
        }
    }
}