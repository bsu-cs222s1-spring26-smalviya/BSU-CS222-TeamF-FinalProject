package com.wiseplanner.gui.controller;

import com.wiseplanner.core.WisePlannerKernel;
import com.wiseplanner.exception.FileWriteException;
import com.wiseplanner.model.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.function.Consumer;

public class TaskDetailController {
    private static final DateTimeFormatter DEADLINE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public enum Mode {
        ADD,
        MODIFY,
        VIEW
    }

    @FXML
    private Button cancelButton;

    @FXML
    private TextArea contentTextArea;

    @FXML
    private DatePicker deadlineDate;

    @FXML
    private Spinner<Integer> deadlineHour;

    @FXML
    private Spinner<Integer> deadlineMinute;

    @FXML
    private Button okButton;

    @FXML
    private Label formTitleLabel;

    @FXML
    private TextField titleTextField;

    private WisePlannerKernel kernel;
    private Consumer<Task> onTaskCreated;
    private Mode mode = Mode.ADD;
    private Task selectedTask;

    @FXML
    private void initialize() {
        LocalTime now = LocalTime.now();
        deadlineHour.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, now.getHour()));
        deadlineMinute.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, now.getMinute()));
        deadlineDate.setValue(LocalDate.now());
    }

    public void setKernel(WisePlannerKernel kernel) {
        this.kernel = kernel;
    }

    public void setOnTaskCreated(Consumer<Task> onTaskCreated) {
        this.onTaskCreated = onTaskCreated;
    }

    public void setupForAdd() {
        mode = Mode.ADD;
        selectedTask = null;
        applyModeState();
    }

    public void setupForModify(Task task) {
        mode = Mode.MODIFY;
        selectedTask = task;
        fillTaskContent(task);
        applyModeState();
    }

    public void setupForView(Task task) {
        mode = Mode.VIEW;
        selectedTask = task;
        fillTaskContent(task);
        applyModeState();
    }

    @FXML
    void onCancelButtonClick(ActionEvent event) {
        closeWindow();
    }

    @FXML
    void onOKButtonClick(ActionEvent event) {
        if (mode == Mode.VIEW) {
            closeWindow();
            return;
        }

        String title = titleTextField.getText() == null ? "" : titleTextField.getText().trim();
        String content = contentTextArea.getText() == null ? "" : contentTextArea.getText().trim();
        LocalDate selectedDate = deadlineDate.getValue();

        if (title.isEmpty() || selectedDate == null) {
            return;
        }

        int hour = deadlineHour.getValue();
        int minute = deadlineMinute.getValue();
        String formattedDeadline = DEADLINE_FORMATTER.format(selectedDate.atTime(hour, minute));

        try {
            if (mode == Mode.ADD) {
                kernel.task().addTask(formattedDeadline, title, content);
                if (onTaskCreated != null) {
                    int lastIndex = kernel.task().getTaskList().size() - 1;
                    onTaskCreated.accept(kernel.task().getTaskList().get(lastIndex));
                }
            } else if (mode == Mode.MODIFY && selectedTask != null) {
                selectedTask.setDeadline(formattedDeadline);
                selectedTask.setTitle(title);
                selectedTask.setContent(content);
                kernel.task().saveTask();
            }
            closeWindow();
        } catch (FileWriteException ignored) {
        }
    }

    private void fillTaskContent(Task task) {
        titleTextField.setText(task.getTitle());
        contentTextArea.setText(task.getContent());
        try {
            LocalDateTime dateTime = LocalDateTime.parse(task.getDeadline(), DEADLINE_FORMATTER);
            deadlineDate.setValue(dateTime.toLocalDate());
            deadlineHour.getValueFactory().setValue(dateTime.getHour());
            deadlineMinute.getValueFactory().setValue(dateTime.getMinute());
        } catch (DateTimeParseException ignored) {
            deadlineDate.setValue(LocalDate.now());
        }
    }

    private void applyModeState() {
        boolean editable = mode != Mode.VIEW;
        titleTextField.setEditable(editable);
        contentTextArea.setEditable(editable);
        deadlineDate.setDisable(!editable);
        deadlineHour.setDisable(!editable);
        deadlineMinute.setDisable(!editable);

        cancelButton.setManaged(mode != Mode.VIEW);
        cancelButton.setVisible(mode != Mode.VIEW);

        if (mode == Mode.ADD) {
            formTitleLabel.setText("Add Task");
            okButton.setText("OK");
        } else if (mode == Mode.MODIFY) {
            formTitleLabel.setText("Modify Task");
            okButton.setText("OK");
        } else {
            formTitleLabel.setText("View Task");
            okButton.setText("OK");
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) okButton.getScene().getWindow();
        stage.close();
    }
}