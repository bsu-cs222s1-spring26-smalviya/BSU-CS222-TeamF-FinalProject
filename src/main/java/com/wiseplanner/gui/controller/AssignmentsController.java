package com.wiseplanner.gui.controller;

import com.wiseplanner.model.Assignment;
import com.wiseplanner.model.Course;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

public class AssignmentsController extends BaseController {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private TableView<Assignment> assignmentsTable;

    @FXML
    private TableColumn<Assignment, String> descriptionColumn;

    @FXML
    private TableColumn<Assignment, String> dueColumn;

    @FXML
    private TableColumn<Assignment, String> idColumn;

    @FXML
    private TableColumn<Assignment, String> nameColumn;

    private Course course;
    private final ObservableList<Assignment> assignments = FXCollections.observableArrayList();
    private boolean tableConfigured = false;

    @FXML
    private void initialize() {
        configureTable();
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public void loadAssignments() {
        kernel.canvas().updateAssignments(course);
        assignments.setAll(course.getAssignments());
        assignmentsTable.setItems(assignments);
    }

    public void configureTable() {
        if (tableConfigured) {
            return;
        }
        assignmentsTable.setItems(assignments);
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        dueColumn.setCellValueFactory(new PropertyValueFactory<>("due_at"));
        descriptionColumn.setCellFactory(column -> new TableCell<Assignment, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(stripHtml(item));
                }
            }
        });
        assignmentsTable.getColumns().setAll(idColumn, nameColumn, descriptionColumn, dueColumn);
        tableConfigured = true;
    }

    private String stripHtml(String html) {
        if (html == null || html.equals("null")) {
            return "No Description";
        }

        String noTags = html.replaceAll("<[^>]*>", "");
        String cleaned = noTags.replaceAll("\\s+", " ").trim();
        if (cleaned.length() > 80) {
            return cleaned.substring(0, 77) + "...";
        }

        return cleaned;
    }
}
