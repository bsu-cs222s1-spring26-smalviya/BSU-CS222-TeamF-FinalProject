package com.wiseplanner.service;

import com.wiseplanner.model.Task;

import java.util.ArrayList;
import java.util.List;

public class TaskManager {
    private List<Task> taskList = new ArrayList<>();

    public void addTask(String timestamp, String title, String content) {
        Task task = new Task(timestamp, title, content);
        taskList.add(task);
    }

    public void deleteTask(int index) throws IndexOutOfBoundsException {
        if (index < 0 || index >= taskList.size()) {
            throw new IndexOutOfBoundsException("Task deletion failed, invalid task index.");
        }
        taskList.remove(index);
    }

    public List<Task> getTaskList() {
        return taskList;
    }
}