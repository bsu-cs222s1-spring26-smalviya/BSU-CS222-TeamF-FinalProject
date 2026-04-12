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
import java.util.function.Consumer;

public class AddTaskController {
    private static final DateTimeFormatter DEADLINE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @FXML
    private Button cancelButton;

    @FXML
    private Label contentLabel;

    @FXML
    private TextArea contentTextArea;

    @FXML
    private DatePicker deadlineDate;

    @FXML
    private Spinner<Integer> deadlineHour;

    @FXML
    private Label deadlineLabel;

    @FXML
    private Spinner<Integer> deadlineMinute;

    @FXML
    private Button okButton;

    @FXML
    private Label titleLabel;

    @FXML
    private TextField titleTextField;

    private WisePlannerKernel kernel;
    private Consumer<Task> onTaskCreated;

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

        @FXML
        void onCancelButtonClick(ActionEvent event) {
            closeWindow();
        }

        @FXML
        void onOKButtonClick(ActionEvent event) {
            String title = titleTextField.getText() == null ? "" : titleTextField.getText().trim();
            String content = contentTextArea.getText() == null ? "" : contentTextArea.getText().trim();
            LocalDate selectedDate = deadlineDate.getValue();

            if (title.isEmpty() || selectedDate == null) {
                return;
            }

            int hour = ((Integer) deadlineHour.getValue());
            int minute = ((Integer) deadlineMinute.getValue());
            LocalDateTime deadlineTime = selectedDate.atTime(hour, minute);
            String formattedDeadline = DEADLINE_FORMATTER.format(deadlineTime);

            try {
                kernel.task().addTask(formattedDeadline, title, content);
                if (onTaskCreated != null) {
                    int lastIndex = kernel.task().getTaskList().size() - 1;
                    onTaskCreated.accept(kernel.task().getTaskList().get(lastIndex));
                }
                closeWindow();
            } catch (FileWriteException ignored) {
            }
        }

        private void closeWindow() {
            Stage stage = (Stage) cancelButton.getScene().getWindow();
            stage.close();
        }

    }