package com.wiseplanner.console;

import com.wiseplanner.exception.FileCorruptionException;
import com.wiseplanner.exception.FileReadException;
import com.wiseplanner.exception.FileWriteException;
import com.wiseplanner.exception.NetworkException;
import com.wiseplanner.core.WisePlannerKernel;
import com.wiseplanner.service.ScheduleManager;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class ConsoleUI {
    Scanner scanner = new Scanner(System.in);
    WisePlannerKernel wisePlannerKernel = new WisePlannerKernel();
    CanvasOutputFormatter canvasOutputFormatter = new CanvasOutputFormatter();
    TaskOutputFormatter taskOutputFormatter = new TaskOutputFormatter();
    ScheduleOutputFormatter scheduleOutputFormatter = new ScheduleOutputFormatter();

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
            System.out.println("(1) Courses\n(2) Tasks\n(3) Schedules\n(0) Exit\nPlease enter your choice:");

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
                case 3: // Schedules
                    System.out.println("(1) View Schedules\n(2) Add Schedule\n(3) Delete Schedule\n(0) Back\nPlease enter your choice:");
                    int choice_schedule = safeReadInt();
                    switch (choice_schedule) {
                        case 0: // Back
                            break;
                        case 1: // View Schedules
                            System.out.println(scheduleOutputFormatter.getScheduleOutput(wisePlannerKernel.schedule().getScheduleList()));
                            break;
                        case 2: // Add Schedule
                            System.out.println("Please enter schedule name:");
                            String name = scanner.nextLine();
                            System.out.println("Please enter start time:");
                            String startTimeStr = scanner.nextLine();
                            LocalTime startTime = LocalTime.parse(startTimeStr);
                            System.out.println("Please enter end time:");
                            String endTimeStr = scanner.nextLine();
                            LocalTime endTime = LocalTime.parse(endTimeStr);
                            System.out.println("Please enter day of weeks:");
                            System.out.println("1.Monday 2.Tuesday 3.Wednesday 4.Thursday 5.Friday 6.Saturday 7.Sunday 0.exit");
                            Set<DayOfWeek> dayOfWeeks = new HashSet<>();
                            while (true) {
                                System.out.print("> ");
                                int choice_day = Integer.parseInt(scanner.nextLine());
                                if (choice_day == 0) {
                                    break;
                                }
                                if (choice_day >= 1 && choice_day <= 7) {
                                    dayOfWeeks.add(DayOfWeek.of(choice_day));
                                }
                            }
                            System.out.println("Please enter professor name:");
                            String professor = scanner.nextLine();
                            System.out.println("Please enter location:");
                            String location = scanner.nextLine();
                            try {
                                if (professor.isBlank() || location.isBlank()) {
                                    wisePlannerKernel.schedule().addSchedule(name, dayOfWeeks, startTime, endTime);
                                } else {
                                    wisePlannerKernel.schedule().addSchedule(name, dayOfWeeks, startTime, endTime, professor, location);
                                }
                            } catch (FileWriteException e) {
                                System.err.println("[Error] " + e.getMessage());
                            }
                            break;
                        case 3: // Delete Schedule
                            System.out.println("Please enter the index:");
                            int index = safeReadInt();
                            try {
                                if (index > 0 && index <= wisePlannerKernel.schedule().getScheduleList().size()) {
                                    wisePlannerKernel.schedule().deleteSchedule(index - 1);
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