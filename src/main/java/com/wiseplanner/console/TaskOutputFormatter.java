package com.wiseplanner.console;

import com.wiseplanner.model.Task;

import java.util.List;

public class TaskOutputFormatter {
    public String getTaskOutput(List<Task> taskList) {
        StringBuilder output = new StringBuilder();
        output.append("Task\n" +
                "Deadline\tTitle\tContent\n");
        for (Task i : taskList) {
            output.append(i);
        }
        return output.toString();
    }
}
