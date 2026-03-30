package com.wiseplanner.gui.controller;

import com.wiseplanner.model.Assignment;
import com.wiseplanner.model.Course;
import com.wiseplanner.model.Submission;
import javafx.beans.property.SimpleStringProperty;
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

    @FXML
    private TableColumn<Assignment, String> workflow_stateColumn;

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
        workflow_stateColumn.setCellValueFactory(cellData -> {
            Assignment assignment = cellData.getValue();
            Submission submission = assignment.getSubmission();
            if (submission != null && submission.getWorkflow_state() != null) {
                return new SimpleStringProperty(submission.getWorkflow_state());
            } else {
                return new SimpleStringProperty("N/A");
            }
        });
        assignmentsTable.getColumns().setAll(idColumn, nameColumn, descriptionColumn, dueColumn, workflow_stateColumn);
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
