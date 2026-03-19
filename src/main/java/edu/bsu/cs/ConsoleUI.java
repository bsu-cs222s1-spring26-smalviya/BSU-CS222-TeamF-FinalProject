package edu.bsu.cs;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Scanner;

public class ConsoleUI {
    Scanner scanner = new Scanner(System.in);

    public void show() throws IOException, URISyntaxException {
        System.out.println("Please enter your name");
        String name = scanner.nextLine();
        System.out.println("Please enter your Canvas LMS Access Token");
        String canvasToken = scanner.nextLine();
        WisePlannerKernel wisePlannerKernel = new WisePlannerKernel(name, canvasToken);
        CanvasOutputFormatter canvasOutputFormatter = new CanvasOutputFormatter();
        TaskOutputFormatter taskOutputFormatter = new TaskOutputFormatter();

        while (true) {
            System.out.println("Main Menu");
            System.out.println("(1) Courses");
            System.out.println("(2) Task");
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
                            try {
                                wisePlannerKernel.taskManager.addTask(timestamp, title, content);
                            } catch (Exception e) {
                                System.out.println(e.getMessage());
                            }
                            break;
                        case 3:
                            System.out.println("Please enter the index");
                            int index = Integer.parseInt(scanner.nextLine());
                            wisePlannerKernel.taskManager.deleteTask(index - 1);
                            break;
                    }
                    break;
                default:
                    continue;
            }
        }
    }
}