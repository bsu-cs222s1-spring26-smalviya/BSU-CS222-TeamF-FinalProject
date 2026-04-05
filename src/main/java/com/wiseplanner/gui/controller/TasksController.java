package com.wiseplanner.gui.controller;

import com.wiseplanner.model.Task;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;

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
    private TableView<Task> tasksTable;

    @FXML
    private TableColumn<Task, String> titleColumn;

    @FXML
    private Label titleLabel;

    private final ObservableList<Task> tasks = FXCollections.observableArrayList();
    private boolean tableConfigured = false;

    @FXML
    private void initialize() {
        configureTable();
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
        deleteColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Task, Void> call(TableColumn<Task, Void> param) {
                return new TableCell<>() {
                    private final Button deleteButton = new Button("Delete");

                    {
                        deleteButton.setOnAction(event -> {
                            Task task = getTableView().getItems().get(getIndex());
                            kernel.task().deleteTask(task);
                            tasks.remove(task);
                        });
                        deleteButton.setMaxWidth(Double.MAX_VALUE);
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(deleteButton);
                        }
                    }
                };
            }
        });
        tasksTable.getColumns().setAll(titleColumn, contentColumn, deadlineColumn, deleteColumn);
        tableConfigured = true;
    }
}
