package com.wiseplanner.gui.controller;

import com.wiseplanner.exception.DeleteException;
import com.wiseplanner.model.Task;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Objects;

public class TasksController extends BaseController {

    @FXML
    private Button addButton;

    @FXML
    private BorderPane borderPane;

    @FXML
    private TableColumn<Task, String> contentColumn;

    @FXML
    private TableColumn<Task, String> deadlineColumn;

    @FXML
    private TableColumn<Task, Void> deleteColumn;

    @FXML
    private TableColumn<Task, Void> modifyColumn;

    @FXML
    private TableView<Task> tasksTable;

    @FXML
    private TableColumn<Task, String> titleColumn;

    @FXML
    private Label titleLabel;

    private final ObservableList<Task> tasks = FXCollections.observableArrayList();
    private boolean tableConfigured = false;

    private enum PlaceholderMode {
        LOADING, EMPTY, FAILED
    }

    @FXML
    private void initialize() {
        configureTable();
        addButton.setOnAction(event -> openTaskDetail(TaskDetailController.Mode.ADD, null));
    }

    private void setPlaceholder(PlaceholderMode mode) {
        if (mode.equals(PlaceholderMode.LOADING)) {
            Image image = new Image(getClass().getResourceAsStream("/images/loading.png"));
            ImageView imageView = new ImageView(image);
            imageView.setFitHeight(48);
            imageView.setFitWidth(48);
            imageView.setSmooth(true);
            RotateTransition rotateTransition = new RotateTransition(Duration.seconds(1), imageView);
            rotateTransition.setByAngle(360);
            rotateTransition.setCycleCount(Animation.INDEFINITE);
            rotateTransition.setInterpolator(Interpolator.LINEAR);
            rotateTransition.play();
            Label statusLabel = new Label("Loading...");
            VBox vBox = new VBox(15);
            vBox.setAlignment(Pos.CENTER);
            vBox.getChildren().addAll(imageView, statusLabel);
            tasksTable.setPlaceholder(vBox);
        }
        if (mode.equals(PlaceholderMode.EMPTY)) {
            Image image = new Image(getClass().getResourceAsStream("/images/empty.png"));
            ImageView imageView = new ImageView(image);
            imageView.setFitHeight(48);
            imageView.setFitWidth(48);
            imageView.setSmooth(true);
            Label statusLabel = new Label("Nothing here...");
            VBox vBox = new VBox(15);
            vBox.setAlignment(Pos.CENTER);
            vBox.getChildren().addAll(imageView, statusLabel);
            tasksTable.setPlaceholder(vBox);
        }
        if (mode.equals(PlaceholderMode.FAILED)) {
            Image image = new Image(getClass().getResourceAsStream("/images/failed.png"));
            ImageView imageView = new ImageView(image);
            imageView.setFitHeight(48);
            imageView.setFitWidth(48);
            imageView.setSmooth(true);
            Label statusLabel = new Label("Failed to load");
            VBox vBox = new VBox(15);
            vBox.setAlignment(Pos.CENTER);
            vBox.getChildren().addAll(imageView, statusLabel);
            tasksTable.setPlaceholder(vBox);
        }
    }

    public void loadTasks() {
        setPlaceholder(PlaceholderMode.LOADING);
        runAsync(
                () -> {
                    kernel.task().loadTask();
                    return kernel.task().getTaskList();
                },
                result -> {
                    tasks.setAll(kernel.task().getTaskList());
                    tasksTable.setItems(tasks);
                    setPlaceholder(PlaceholderMode.EMPTY);
                },
                error -> {
                    setPlaceholder(PlaceholderMode.FAILED);
                    Alert alert = new Alert(Alert.AlertType.ERROR, error.getMessage(), ButtonType.OK);
                    alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                    alert.showAndWait();
                }
        );
    }

    public void configureTable() {
        if (tableConfigured) {
            return;
        }
        tasksTable.setItems(tasks);
        tasksTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        tasksTable.setRowFactory(table -> {
            TableRow<Task> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    openTaskDetail(TaskDetailController.Mode.VIEW, row.getItem());
                }
            });
            return row;
        });
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        contentColumn.setCellValueFactory(new PropertyValueFactory<>("content"));
        deadlineColumn.setCellValueFactory(new PropertyValueFactory<>("deadline"));

        modifyColumn.setCellFactory(createActionCellFactory("Modify", task -> openTaskDetail(TaskDetailController.Mode.MODIFY, task)));
        deleteColumn.setCellFactory(createActionCellFactory("Delete", task -> {
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirm Delete");
            confirmAlert.setHeaderText("Delete this task?");
            confirmAlert.setContentText(task.getTitle());
            confirmAlert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        kernel.task().deleteTask(task);
                        tasks.remove(task);
                    } catch (DeleteException e) {
                        Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
                        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                        alert.showAndWait();
                    }
                }
            });
        }));

        tasksTable.getColumns().setAll(titleColumn, contentColumn, deadlineColumn, modifyColumn, deleteColumn);
        tableConfigured = true;
    }

    private Callback<TableColumn<Task, Void>, TableCell<Task, Void>> createActionCellFactory(String text,
                                                                                             java.util.function.Consumer<Task> action) {
        return param -> new TableCell<>() {
            private final Button actionButton = new Button(text);

            {
                actionButton.setOnAction(event -> {
                    Task task = getTableView().getItems().get(getIndex());
                    action.accept(task);
                    tasksTable.refresh();
                });
                actionButton.setMaxWidth(Double.MAX_VALUE);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(actionButton);
                }
            }
        };
    }

    private void openTaskDetail(TaskDetailController.Mode mode, Task task) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    Objects.requireNonNull(getClass().getClassLoader().getResource("fxml/TaskDetail.fxml")));
            Parent root = loader.load();
            TaskDetailController taskDetailController = loader.getController();
            taskDetailController.setKernel(kernel);
            taskDetailController.setOnTaskCreated(tasks::add);

            if (mode == TaskDetailController.Mode.ADD) {
                taskDetailController.setupForAdd();
            } else if (mode == TaskDetailController.Mode.MODIFY && task != null) {
                taskDetailController.setupForModify(task);
            } else if (mode == TaskDetailController.Mode.VIEW && task != null) {
                taskDetailController.setupForView(task);
            }

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(mode == TaskDetailController.Mode.ADD ? "Add Task" : "Task Detail");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            tasksTable.refresh();
        } catch (IOException ignored) {
        }
    }
}