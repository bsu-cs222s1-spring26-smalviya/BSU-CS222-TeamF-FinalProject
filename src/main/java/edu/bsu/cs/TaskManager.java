package edu.bsu.cs;

import java.util.ArrayList;
import java.util.List;

public class TaskManager {
    private List<Task> taskList = new ArrayList<>();

    public void addTask(String timestamp, String title, String content) throws Exception {
        if (title == null || title.trim().isEmpty()) {
            throw new Exception("Error: Task title cannot be empty!");
        }
        Task task = new Task(timestamp, title, content);
        taskList.add(task);
    }

    public void deleteTask(int index) throws IndexOutOfBoundsException {
        if (index < 0 || index >= taskList.size()) {
            throw new IndexOutOfBoundsException("Error: Invalid task index.");
        }
        taskList.remove(index);
    }

    public List<Task> getTaskList() {
        return taskList;
    }
}