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
        while (true) {
            System.out.println("Main Menu");
            System.out.println("(1) Courses");
            System.out.println("(0) Exit");
            System.out.println("Please enter your choice");
            int choice = Integer.parseInt(scanner.nextLine());
            switch (choice) {
                case 0:
                    return;
                case 1:
                    System.out.print(canvasOutputFormatter.getCoursesOutput(wisePlannerKernel.getCourses()));
                default:
                    continue;
            }
        }

    }
}
