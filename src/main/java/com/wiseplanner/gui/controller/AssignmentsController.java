package com.wiseplanner.gui.controller;

import com.wiseplanner.model.Assignment;
import com.wiseplanner.model.Course;
import com.wiseplanner.model.Submission;
import com.wiseplanner.model.Task;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Objects;

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

    private enum PlaceholderMode {
        LOADING, EMPTY, FAILED
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public void loadAssignments() {
        setPlaceholder(PlaceholderMode.LOADING);
        runAsync(
                () -> {
                    kernel.canvas().updateAssignments(course);
                    return course.getAssignments();
                },
                result -> {
                    assignments.setAll(result);
                    assignmentsTable.setItems(assignments);
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
        assignmentsTable.setItems(assignments);
        assignmentsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

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
        assignmentsTable.setRowFactory(table -> {
            TableRow<Assignment> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    FXMLLoader loader = new FXMLLoader(
                            Objects.requireNonNull(getClass().getClassLoader().getResource("fxml/AssignmentDetail.fxml")));
                    Parent root = null;
                    try {
                        root = loader.load();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    AssignmentDetailController assignmentDetailController = loader.getController();
                    assignmentDetailController.setContent(row.getItem());
                    Stage stage = new Stage();
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.setTitle("Assignment Detail");
                    stage.setScene(new Scene(root));
                    stage.showAndWait();
                }
            });
            return row;
        });
        tableConfigured = true;
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
            assignmentsTable.setPlaceholder(vBox);
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
            assignmentsTable.setPlaceholder(vBox);
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
            assignmentsTable.setPlaceholder(vBox);
        }
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
