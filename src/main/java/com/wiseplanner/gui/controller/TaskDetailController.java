package com.wiseplanner.gui.controller;

import com.wiseplanner.core.WisePlannerKernel;
import com.wiseplanner.exception.FileWriteException;
import com.wiseplanner.model.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
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
                try {
                    kernel.task().addTask(formattedDeadline, title, content);
                } catch (FileWriteException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
                    alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                    alert.showAndWait();
                }
                if (onTaskCreated != null) {
                    int lastIndex = kernel.task().getTaskList().size() - 1;
                    try {
                        onTaskCreated.accept(kernel.task().getTaskList().get(lastIndex));
                    } catch (IndexOutOfBoundsException e) {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Internal Program Error (Index Out of Bounds)", ButtonType.OK);
                        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                        alert.showAndWait();
                    }
                }
            } else if (mode == Mode.MODIFY && selectedTask != null) {
                selectedTask.setDeadline(formattedDeadline);
                selectedTask.setTitle(title);
                selectedTask.setContent(content);
                try {
                    kernel.task().saveTask();
                } catch (FileWriteException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
                    alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                    alert.showAndWait();
                }
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

        deadlineDate.setDisable(false);
        deadlineHour.setDisable(false);
        deadlineMinute.setDisable(false);
        deadlineDate.setMouseTransparent(!editable);
        deadlineHour.setMouseTransparent(!editable);
        deadlineMinute.setMouseTransparent(!editable);
        deadlineDate.setFocusTraversable(editable);
        deadlineHour.setFocusTraversable(editable);
        deadlineMinute.setFocusTraversable(editable);
        deadlineDate.setOpacity(1.0);
        deadlineHour.setOpacity(1.0);
        deadlineMinute.setOpacity(1.0);

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
