package com.wiseplanner.gui.controller;

import com.wiseplanner.model.Task;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.util.Objects;

public class TasksController extends BaseController {

    @FXML
    private Button addButton;

    @FXML
    private AnchorPane anchorPane;

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

    @FXML
    private TableColumn<Task, Void> viewColumn;

    private final ObservableList<Task> tasks = FXCollections.observableArrayList();
    private boolean tableConfigured = false;

    @FXML
    private void initialize() {
        configureTable();
        addButton.setOnAction(event -> openTaskDetail(TaskDetailController.Mode.ADD, null));
    }

    public void loadTasks() {
        kernel.task().loadTask();
        tasks.setAll(kernel.task().getTaskList());
        tasksTable.setItems(tasks);
    }

    public void configureTable() {
        if (tableConfigured) {
            return;
        }
        tasksTable.setItems(tasks);
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        contentColumn.setCellValueFactory(new PropertyValueFactory<>("content"));
        deadlineColumn.setCellValueFactory(new PropertyValueFactory<>("deadline"));
        viewColumn.setCellFactory(createActionCellFactory("View", task -> openTaskDetail(TaskDetailController.Mode.VIEW, task)));
        modifyColumn.setCellFactory(createActionCellFactory("Modify", task -> openTaskDetail(TaskDetailController.Mode.MODIFY, task)));
        deleteColumn.setCellFactory(createActionCellFactory("Delete", task -> {
                Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                confirmAlert.setTitle("Confirm Delete");
                confirmAlert.setHeaderText("Delete this task?");
                confirmAlert.setContentText(task.getTitle());
                confirmAlert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        kernel.task().deleteTask(task);
                        tasks.remove(task);
                    }
                });
            }));

        tasksTable.getColumns().setAll(titleColumn, contentColumn, deadlineColumn, viewColumn, modifyColumn, deleteColumn);
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