package com.wiseplanner.gui.controller;

import com.wiseplanner.exception.DeleteException;
import com.wiseplanner.model.Course;
import com.wiseplanner.model.Task;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class TasksController extends BaseController {

    @FXML private Button addButton;
    @FXML private VBox   taskListBox;
    @FXML private ComboBox<String> courseFilterCombo;

    private List<Course> courses = new ArrayList<>();
    private Map<String, String> courseIdToName = new LinkedHashMap<>();
    private String currentFilter = null;

    @FXML
    private void initialize() {
        courseFilterCombo.setOnAction(e -> {
            String sel = courseFilterCombo.getValue();
            if (sel == null || "All Courses".equals(sel)) {
                currentFilter = null;
            } else if ("Unassigned".equals(sel)) {
                currentFilter = "UNASSIGNED";
            } else {
                currentFilter = courseIdToName.entrySet().stream()
                        .filter(en -> en.getValue().equals(sel))
                        .map(Map.Entry::getKey).findFirst().orElse(null);
            }
            renderTasks();
        });
    }

    @FXML void onAddButtonClick(ActionEvent e) { openTaskDetail(TaskDetailController.Mode.ADD, null); }

    public void loadTasks() {
        try {
            kernel.canvas().updateCourses();
            courses = kernel.canvas().getCourses();
            courseIdToName.clear();
            for (Course c : courses)
                courseIdToName.put(String.valueOf(c.getId()),
                        c.getCourse_code() != null ? c.getCourse_code() : c.getName());
        } catch (Exception ignored) {}

        List<String> opts = new ArrayList<>();
        opts.add("All Courses");
        opts.addAll(courseIdToName.values());
        opts.add("Unassigned");
        courseFilterCombo.setItems(FXCollections.observableArrayList(opts));
        courseFilterCombo.getSelectionModel().selectFirst();

        try { kernel.task().loadTask(); } catch (Exception ignored) {}
        renderTasks();
    }

    private void renderTasks() {
        taskListBox.getChildren().clear();
        List<Task> all = kernel.task().getTaskList();
        if (all == null || all.isEmpty()) {
            taskListBox.getChildren().add(styledLabel("No tasks yet. Click + Add Task to create one.")); return;
        }
        List<Task> filtered = all.stream().filter(t -> {
            if (currentFilter == null) return true;
            if ("UNASSIGNED".equals(currentFilter)) return t.getCourseId() == null || t.getCourseId().isBlank();
            return currentFilter.equals(t.getCourseId());
        }).collect(Collectors.toList());

        if (filtered.isEmpty()) {
            taskListBox.getChildren().add(styledLabel("No tasks for this filter.")); return;
        }

        Map<String, List<Task>> grouped = new LinkedHashMap<>();
        for (Task t : filtered) {
            String key = (t.getCourseId() == null || t.getCourseId().isBlank()) ? "UNASSIGNED" : t.getCourseId();
            grouped.computeIfAbsent(key, k -> new ArrayList<>()).add(t);
        }

        for (Map.Entry<String, List<Task>> entry : grouped.entrySet()) {
            String groupKey = entry.getKey();
            String groupName = "UNASSIGNED".equals(groupKey) ? "Unassigned"
                    : courseIdToName.getOrDefault(groupKey, groupKey);
            Label header = new Label(groupName);
            header.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #2D3B45; -fx-padding: 8 0 4 0;");
            taskListBox.getChildren().add(header);
            for (Task t : entry.getValue()) taskListBox.getChildren().add(makeTaskCard(t));
        }
    }

    private HBox makeTaskCard(Task t) {
        CheckBox cb = new CheckBox();
        cb.setSelected(t.isDone());
        Label title = new Label(t.getTitle());
        title.setWrapText(true);
        title.setStyle(t.isDone()
                ? "-fx-font-size: 13px; -fx-text-fill: #aaa; -fx-strikethrough: true;"
                : "-fx-font-size: 13px; -fx-text-fill: #2D3B45; -fx-font-weight: bold;");
        String courseLabel = t.getCourseId() != null
                ? courseIdToName.getOrDefault(t.getCourseId(), "") : "";
        Label meta = new Label((t.getDeadline() != null ? t.getDeadline() : "")
                + (!courseLabel.isBlank() ? "  |  " + courseLabel : ""));
        meta.setStyle("-fx-font-size: 11px; -fx-text-fill: #6B7280;");
        cb.setOnAction(e -> {
            t.setDone(cb.isSelected());
            title.setStyle(cb.isSelected()
                    ? "-fx-font-size: 13px; -fx-text-fill: #aaa; -fx-strikethrough: true;"
                    : "-fx-font-size: 13px; -fx-text-fill: #2D3B45; -fx-font-weight: bold;");
            try { kernel.task().saveTask(); } catch (Exception ignored) {}
        });
        VBox textCol = new VBox(2, title, meta);
        HBox.setHgrow(textCol, Priority.ALWAYS);
        Button modBtn = new Button();
        modBtn.setGraphic(createIcon("/images/edit.png", 22));
        modBtn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        modBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-padding: 6;");
        modBtn.setOnAction(e -> openTaskDetail(TaskDetailController.Mode.MODIFY, t));
        Button delBtn = new Button();
        delBtn.setGraphic(createIcon("/images/delete.png", 22));
        delBtn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        delBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-padding: 6;");
        delBtn.setOnAction(e -> {
            Alert c = new Alert(Alert.AlertType.CONFIRMATION, "Delete \"" + t.getTitle() + "\"?", ButtonType.OK, ButtonType.CANCEL);
            c.showAndWait().ifPresent(r -> {
                if (r == ButtonType.OK) {
                    try { kernel.task().deleteTask(t); renderTasks(); }
                    catch (DeleteException ex) { new Alert(Alert.AlertType.ERROR, ex.getMessage(), ButtonType.OK).showAndWait(); }
                }
            });
        });
        HBox card = new HBox(10, cb, textCol, modBtn, delBtn);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle("-fx-background-color: white; -fx-border-color: #E5E7EB;"
                + "-fx-border-radius: 8; -fx-background-radius: 8;"
                + "-fx-padding: 10 14 10 14;"
                + "-fx-effect: dropshadow(gaussian,rgba(0,0,0,0.04),4,0,0,1);");
        card.setOnMouseClicked(e -> {
            Object target = e.getTarget();
            if (target != cb && target != modBtn && target != delBtn) {
                openTaskDetail(TaskDetailController.Mode.VIEW, t);
            }
        });
        return card;
    }

    private ImageView createIcon(String resourcePath, double size) {
        ImageView imageView = new ImageView(new Image(Objects.requireNonNull(
                getClass().getResourceAsStream(resourcePath))));
        imageView.setFitWidth(size);
        imageView.setFitHeight(size);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        return imageView;
    }

    private void openTaskDetail(TaskDetailController.Mode mode, Task task) {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(
                    getClass().getClassLoader().getResource("fxml/TaskDetail.fxml")));
            Parent root = loader.load();
            TaskDetailController ctrl = loader.getController();
            ctrl.setKernel(kernel);
            ctrl.setCourses(courses);
            ctrl.setOnTaskCreated(t -> renderTasks());
            if (mode == TaskDetailController.Mode.ADD) ctrl.setupForAdd();
            else if (mode == TaskDetailController.Mode.MODIFY && task != null) ctrl.setupForModify(task);
            else if (mode == TaskDetailController.Mode.VIEW && task != null) ctrl.setupForView(task);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(mode == TaskDetailController.Mode.ADD ? "Add Task"
                    : mode == TaskDetailController.Mode.MODIFY ? "Edit Task" : "Task");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            renderTasks();
        } catch (IOException ignored) {}
    }

    private Label styledLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 13px;");
        return l;
    }
}
