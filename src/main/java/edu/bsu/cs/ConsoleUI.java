package edu.bsu.cs;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Scanner;

public class ConsoleUI {
    Scanner scanner = new Scanner(System.in);
    TaskManager taskManager = new TaskManager();

    public void show() throws IOException, URISyntaxException {
        System.out.println("Please enter your name");
        String name = scanner.nextLine();
        System.out.println("Please enter your Canvas LMS Access Token");
        String canvasToken = scanner.nextLine();
        WisePlannerKernel wisePlannerKernel = new WisePlannerKernel(name, canvasToken);
        CanvasOutputFormatter canvasOutputFormatter = new CanvasOutputFormatter();

        while (true) {
            System.out.println("Main Menu");
            System.out.println("(1) Courses");
            System.out.println("(2) Add Task");
            System.out.println("(3) View Tasks");
            System.out.println("(4) Delete Task");
            System.out.println("(0) Exit");
            System.out.println("Please enter your choice");
            int choice = Integer.parseInt(scanner.nextLine());
            switch (choice) {
                case 0:
                    return;
                case 1:
                    System.out.print(canvasOutputFormatter.getCoursesOutput(wisePlannerKernel.getCourses()));
                    break;
                case 2:
                    System.out.println("Enter task title");
                    String title = scanner.nextLine();
                    System.out.println("Enter task note");
                    String note = scanner.nextLine();
                    System.out.println("Enter course name");
                    String course = scanner.nextLine();
                    try {
                        taskManager.addTask(title, note, course);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                case 3:
                    if (taskManager.getAllTasks().isEmpty()) {
                        System.out.println("No tasks");
                    } else {
                        for (int i = 0; i < taskManager.getAllTasks().size(); i++) {
                            System.out.println(i + ": " + taskManager.getAllTasks().get(i).toString());
                        }
                    }
                    break;
                case 4:
                    System.out.println("Enter index to delete");
                    try {
                        int index = Integer.parseInt(scanner.nextLine());
                        taskManager.deleteTask(index);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                default:
                    continue;
            }
        }
    }
}