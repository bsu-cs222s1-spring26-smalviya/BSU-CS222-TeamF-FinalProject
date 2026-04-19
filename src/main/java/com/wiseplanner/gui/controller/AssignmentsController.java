package com.wiseplanner.gui.controller;

import com.wiseplanner.model.Assignment;
import com.wiseplanner.model.Course;
import com.wiseplanner.model.Submission;
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
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class AssignmentsController extends BaseController {

    @FXML private AnchorPane anchorPane;
    @FXML private TableView<Assignment> assignmentsTable;
    @FXML private TableColumn<Assignment, String> descriptionColumn;
    @FXML private TableColumn<Assignment, String> dueColumn;
    @FXML private TableColumn<Assignment, String> nameColumn;
    @FXML private TableColumn<Assignment, String> workflow_stateColumn;

    private Course course;
    private final ObservableList<Assignment> assignments = FXCollections.observableArrayList();
    private boolean tableConfigured = false;

    @FXML
    private void initialize() { configureTable(); }

    private enum PlaceholderMode { LOADING, EMPTY, FAILED }

    public void setCourse(Course course) { this.course = course; }

    public void loadAssignments() {
        setPlaceholder(PlaceholderMode.LOADING);
        runAsync(
                () -> {
                    kernel.canvas().updateAssignments(course);
                    return sortedAssignments(course.getAssignments());
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


    private List<Assignment> sortedAssignments(List<Assignment> list) {
        if (list == null) return List.of();
        return list.stream()
                .sorted(Comparator
                        .<Assignment, Integer>comparing(a -> isUnsubmitted(a) ? 0 : 1)
                        .thenComparing(a -> {
                            String due = a.getDue_at();
                            return (due == null || due.equalsIgnoreCase("null")) ? "" : due;
                        }, Comparator.reverseOrder()))
                .toList();
    }

    private boolean isUnsubmitted(Assignment a) {
        Submission s = a.getSubmission();
        return s == null || s.getWorkflow_state() == null
                || s.getWorkflow_state().equalsIgnoreCase("unsubmitted");
    }

    public void configureTable() {
        if (tableConfigured) return;
        assignmentsTable.setItems(assignments);
        assignmentsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        descriptionColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : stripHtml(item));
            }
        });

        dueColumn.setCellValueFactory(cellData -> {
            String due = cellData.getValue().getDue_at();
            if (due == null || due.equalsIgnoreCase("null"))
                return new SimpleStringProperty("No due date");
            return new SimpleStringProperty(formatDate(due));
        });

        workflow_stateColumn.setCellValueFactory(cellData -> {
            Submission s = cellData.getValue().getSubmission();
            if (s == null || s.getWorkflow_state() == null)
                return new SimpleStringProperty("unsubmitted");
            String state = s.getWorkflow_state();
            if (s.getLate())    state += " · LATE";
            if (s.getMissing()) state += " · MISSING";
            return new SimpleStringProperty(state);
        });

        workflow_stateColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                if (item.startsWith("unsubmitted"))
                    setStyle("-fx-text-fill: #e67e22; -fx-font-weight: bold;");
                else if (item.contains("LATE"))
                    setStyle("-fx-text-fill: #c0392b; -fx-font-weight: bold;");
                else if (item.contains("MISSING"))
                    setStyle("-fx-text-fill: #8e44ad; -fx-font-weight: bold;");
                else
                    setStyle("-fx-text-fill: #27ae60;");
            }
        });

        assignmentsTable.getColumns().setAll(nameColumn, descriptionColumn, dueColumn, workflow_stateColumn);

        assignmentsTable.setRowFactory(table -> {
            TableRow<Assignment> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    try {
                        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(
                                getClass().getClassLoader().getResource("fxml/AssignmentDetail.fxml")));
                        Parent root = loader.load();
                        AssignmentDetailController ctrl = loader.getController();
                        ctrl.setContent(row.getItem());
                        Stage stage = new Stage();
                        stage.initModality(Modality.APPLICATION_MODAL);
                        stage.setTitle("Assignment Detail");
                        stage.setScene(new Scene(root));
                        stage.showAndWait();
                    } catch (IOException e) { throw new RuntimeException(e); }
                }
            });
            return row;
        });
        tableConfigured = true;
    }

    private void setPlaceholder(PlaceholderMode mode) {
        String imgPath   = switch (mode) { case LOADING -> "/images/loading.png"; case EMPTY -> "/images/empty.png"; case FAILED -> "/images/failed.png"; };
        String labelText = switch (mode) { case LOADING -> "Loading..."; case EMPTY -> "Nothing here..."; case FAILED -> "Failed to load"; };
        ImageView iv = new ImageView(new Image(getClass().getResourceAsStream(imgPath)));
        iv.setFitHeight(48); iv.setFitWidth(48); iv.setSmooth(true);
        if (mode == PlaceholderMode.LOADING) {
            RotateTransition rt = new RotateTransition(Duration.seconds(1), iv);
            rt.setByAngle(360); rt.setCycleCount(Animation.INDEFINITE);
            rt.setInterpolator(Interpolator.LINEAR); rt.play();
        }
        VBox vBox = new VBox(15, iv, new Label(labelText));
        vBox.setAlignment(Pos.CENTER);
        assignmentsTable.setPlaceholder(vBox);
    }

    private String stripHtml(String html) {
        if (html == null || html.equals("null")) return "No Description";
        String c = html.replaceAll("<[^>]*>", "").replaceAll("\\s+", " ").trim();
        return c.length() > 80 ? c.substring(0, 77) + "..." : c;
    }

    private String formatDate(String iso) {
        try {
            String[] months = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
            String[] p = iso.substring(0, 10).split("-");
            String time = iso.length() > 16 ? " " + iso.substring(11, 16) : "";
            return months[Integer.parseInt(p[1]) - 1] + " " + p[2] + ", " + p[0] + time;
        } catch (Exception e) { return iso; }
    }
}