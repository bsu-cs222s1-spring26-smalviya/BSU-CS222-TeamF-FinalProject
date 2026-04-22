package com.wiseplanner.gui.controller;

import com.wiseplanner.core.WisePlannerKernel;
import com.wiseplanner.exception.FileWriteException;
import com.wiseplanner.model.Course;
import com.wiseplanner.model.Task;
import javafx.collections.FXCollections;
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
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TaskDetailController {

    private static final DateTimeFormatter DEADLINE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public enum Mode { ADD, MODIFY, VIEW }

    @FXML private Label     formTitleLabel;
    @FXML private TextField titleTextField;
    @FXML private TextArea  contentTextArea;
    @FXML private DatePicker deadlineDate;
    @FXML private Spinner<Integer> deadlineHour;
    @FXML private Spinner<Integer> deadlineMinute;
    @FXML private ComboBox<String> courseCombo;
    @FXML private Button    okButton;
    @FXML private Button    cancelButton;

    private WisePlannerKernel  kernel;
    private Consumer<Task>     onTaskCreated;
    private Mode               mode = Mode.ADD;
    private Task               selectedTask;
    private List<Course>       courses  = new ArrayList<>();
    private final List<String> courseIds = new ArrayList<>();

    @FXML
    private void initialize() {
        LocalTime now = LocalTime.now();
        deadlineHour.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, now.getHour()));
        deadlineMinute.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, now.getMinute()));
        deadlineDate.setValue(LocalDate.now());
    }

    public void setKernel(WisePlannerKernel k)      { this.kernel = k; }
    public void setOnTaskCreated(Consumer<Task> cb) { this.onTaskCreated = cb; }

    public void setCourses(List<Course> courses) {
        this.courses = courses != null ? courses : new ArrayList<>();
        List<String> names = new ArrayList<>();
        names.add("None (Unassigned)");
        courseIds.clear();
        courseIds.add(null);
        for (Course c : this.courses) {
            names.add(c.getCourse_code() != null ? c.getCourse_code() : c.getName());
            courseIds.add(String.valueOf(c.getId()));
        }
        courseCombo.setItems(FXCollections.observableArrayList(names));
        courseCombo.getSelectionModel().selectFirst();
    }

    public void setupForAdd()             { mode = Mode.ADD;    selectedTask = null; applyMode(); }
    public void setupForModify(Task task) { mode = Mode.MODIFY; selectedTask = task; fill(task); applyMode(); }
    public void setupForView(Task task)   { mode = Mode.VIEW;   selectedTask = task; fill(task); applyMode(); }

    @FXML void onCancelButtonClick(ActionEvent e) { close(); }

    @FXML
    void onOKButtonClick(ActionEvent e) {
        if (mode == Mode.VIEW) { close(); return; }
        String title   = titleTextField.getText() == null ? "" : titleTextField.getText().trim();
        String content = contentTextArea.getText() == null ? "" : contentTextArea.getText().trim();
        LocalDate date = deadlineDate.getValue();
        if (title.isEmpty() || date == null) return;
        String deadline = DEADLINE_FORMATTER.format(date.atTime(deadlineHour.getValue(), deadlineMinute.getValue()));
        int idx = courseCombo.getSelectionModel().getSelectedIndex();
        String courseId = (idx >= 0 && idx < courseIds.size()) ? courseIds.get(idx) : null;
        try {
            if (mode == Mode.ADD) {
                kernel.task().addTask(deadline, title, content);
                List<Task> list = kernel.task().getTaskList();
                Task created = list.get(list.size() - 1);
                created.setCourseId(courseId);
                kernel.task().saveTask();
                if (onTaskCreated != null) onTaskCreated.accept(created);
            } else if (mode == Mode.MODIFY && selectedTask != null) {
                selectedTask.setTitle(title);
                selectedTask.setContent(content);
                selectedTask.setDeadline(deadline);
                selectedTask.setCourseId(courseId);
                kernel.task().saveTask();
                if (onTaskCreated != null) onTaskCreated.accept(selectedTask);
            }
            close();
        } catch (FileWriteException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR, ex.getMessage(), ButtonType.OK);
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.showAndWait();
        }
    }

    private void fill(Task t) {
        titleTextField.setText(t.getTitle());
        contentTextArea.setText(t.getContent());
        try {
            LocalDateTime dt = LocalDateTime.parse(t.getDeadline(), DEADLINE_FORMATTER);
            deadlineDate.setValue(dt.toLocalDate());
            deadlineHour.getValueFactory().setValue(dt.getHour());
            deadlineMinute.getValueFactory().setValue(dt.getMinute());
        } catch (DateTimeParseException ignored) { deadlineDate.setValue(LocalDate.now()); }
        if (t.getCourseId() != null) {
            for (int i = 0; i < courseIds.size(); i++) {
                if (t.getCourseId().equals(courseIds.get(i))) {
                    courseCombo.getSelectionModel().select(i); break;
                }
            }
        }
    }

    private void applyMode() {
        boolean editable = mode != Mode.VIEW;
        titleTextField.setEditable(editable);
        contentTextArea.setEditable(editable);
        deadlineDate.setMouseTransparent(!editable);
        deadlineHour.setMouseTransparent(!editable);
        deadlineMinute.setMouseTransparent(!editable);
        courseCombo.setMouseTransparent(!editable);
        cancelButton.setVisible(mode != Mode.VIEW);
        cancelButton.setManaged(mode != Mode.VIEW);
        formTitleLabel.setText(mode == Mode.ADD ? "Add Task" : mode == Mode.MODIFY ? "Edit Task" : "Task");
        okButton.setText(mode == Mode.VIEW ? "Close" : "Save");
    }

    private void close() { ((Stage) okButton.getScene().getWindow()).close(); }
}