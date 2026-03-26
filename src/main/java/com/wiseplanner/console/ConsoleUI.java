package com.wiseplanner.console;

import com.google.gson.Gson;
import com.wiseplanner.service.Assignment;
import com.wiseplanner.service.User;
import com.wiseplanner.service.WisePlannerKernel;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

public class ConsoleUI {
    Scanner scanner = new Scanner(System.in);
    public static final String DATA_FOLDER = ".wiseplanner";
    public static final String USER_DATA_FILE = "user.json";
    WisePlannerKernel wisePlannerKernel;
    CanvasOutputFormatter canvasOutputFormatter = new CanvasOutputFormatter();
    TaskOutputFormatter taskOutputFormatter = new TaskOutputFormatter();
    Gson gson = new Gson();

    public void show() throws IOException, URISyntaxException {

        //Initialize User Data
        String userPath = System.getProperty("user.home");
        Path userDataPath = Paths.get(userPath, DATA_FOLDER, USER_DATA_FILE);
        File userDataFile = userDataPath.toFile();
        User user = null;
        if (userDataFile.exists()) {
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(userDataFile))) {
                user = gson.fromJson(bufferedReader, User.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Please enter your name: ");
            String name = scanner.nextLine();
            System.out.println("Please enter your Canvas LMS Access Token");
            String canvasToken = scanner.nextLine();
            user = new User(name, canvasToken);
            userDataFile.getParentFile().mkdirs();
            String userJson = gson.toJson(user);
            try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(userDataFile))) {
                bufferedWriter.write(userJson);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        wisePlannerKernel = new WisePlannerKernel(user);

        //Console User Interface
        while (true) {
            System.out.println("Main Menu");
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
                    System.out.print(canvasOutputFormatter.getCoursesOutput(wisePlannerKernel.getCourses()));
                    System.out.println("Enter a Course ID to view its assignments, or 0 to go back:");
                    String courseId = scanner.nextLine();
                    if (!courseId.equals("0")) {
                        try {
                            List<Assignment> assignments = wisePlannerKernel.getAssignments(courseId);
                            System.out.print(canvasOutputFormatter.getAssignmentsOutput(assignments));
                        } catch (Exception e) {
                            System.out.println("Error: Could not load assignments. " + e.getMessage());
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
                case 3:
                    System.out.println("Enter a Course ID to view its assignments:");
                    String directCourseId = scanner.nextLine();
                    try {
                        List<Assignment> assignments = wisePlannerKernel.getAssignments(directCourseId);
                        System.out.print(canvasOutputFormatter.getAssignmentsOutput(assignments));
                    } catch (Exception e) {
                        System.out.println("Error: Could not load assignments. " + e.getMessage());
                    }
                    break;
                default:
                    continue;
            }
        }
    }
}