package com.wiseplanner.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wiseplanner.exception.DeleteException;
import com.wiseplanner.exception.FileReadException;
import com.wiseplanner.exception.FileWriteException;
import com.wiseplanner.model.Task;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class TaskManager {
    private static final String DATA_FOLDER = ".wiseplanner";
    private static final String TASK_DATA_FILE = "task.json";
    private Path taskDataPath;
    private String userPath;
    private List<Task> taskList;
    Gson gson = new Gson();

    public TaskManager() {
        this.userPath = System.getProperty("user.home");
        taskDataPath = Paths.get(userPath, DATA_FOLDER, TASK_DATA_FILE);
    }

    public void addTask(String deadline, String title, String content) throws FileWriteException {
        Task task = new Task(deadline, title, content);
        taskList.add(task);
        saveTask();
    }

    public void deleteTask(int index) throws IndexOutOfBoundsException, FileWriteException {
        if (index < 0 || index >= taskList.size()) {
            throw new IndexOutOfBoundsException("Task deletion failed, invalid task index.");
        }
        taskList.remove(index);
        saveTask();
    }

    public void deleteTask(Task task) throws DeleteException {
        if (taskList.remove(task)) {
            saveTask();
        } else {
            throw new DeleteException("Task deletion failed, task not found.");
        }
    }

    public List<Task> getTaskList() {
        return taskList;
    }

    public void loadTask() throws FileReadException {
        File taskDataFile = taskDataPath.toFile();
        if (taskDataFile.exists()) {
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(taskDataFile))) {
                Type listType = new TypeToken<List<Task>>() {
                }.getType();
                taskList = gson.fromJson(bufferedReader, listType);
                if (taskList == null) {
                    taskList = new ArrayList<>();
                }
            } catch (IOException e) {
                taskList = new ArrayList<>();
                throw (new FileReadException("File read failed, unable to read task data."));
            }
        } else {
            taskList = new ArrayList<>();
        }
    }

    public void saveTask() throws FileWriteException {
        File taskDataFile = taskDataPath.toFile();
        taskDataFile.getParentFile().mkdirs();
        Type listType = new TypeToken<List<Task>>() {
        }.getType();
        String taskJson = gson.toJson(taskList, listType);
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(taskDataFile))) {
            bufferedWriter.write(taskJson);
        } catch (IOException e) {
            throw (new FileWriteException("File write failed, unable to write task data."));
        }
    }
}