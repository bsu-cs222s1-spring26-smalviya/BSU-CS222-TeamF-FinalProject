package com.wiseplanner.gui.controller;

import com.wiseplanner.model.Schedule;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class SchedulesController extends BaseController {

    @FXML
    private Button addButton;

    @FXML
    private Label schedulesLabel;

    @FXML
    private AnchorPane schedulesTable;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private Slider zoomSlider;

    private final DoubleProperty hourHeight = new SimpleDoubleProperty(80.0);
    private final int START_HOUR = 7;
    private final int END_HOUR = 21;
    private final double TIME_COLUMN_WIDTH = 60.0;

    @FXML
    public void initialize() {
        zoomSlider.setMin(40.0);
        zoomSlider.setMax(250.0);
        zoomSlider.setValue(80.0);
        hourHeight.bind(zoomSlider.valueProperty());
    }

    @FXML
    void onAddButtonClick(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getClassLoader().getResource("fxml/ScheduleDetail.fxml")));
        Parent root = loader.load();
        ScheduleDetailController controller = loader.getController();
        controller.setKernel(kernel);
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(root));
        controller.setMode(ScheduleDetailController.ViewMode.ADD);
        stage.showAndWait();
        drawTable();
    }

    public void loadSchedules() {
        kernel.schedule().loadSchedule();
        schedulesTable.widthProperty().addListener((obs, oldVal, newVal) -> drawTable());
        hourHeight.addListener((obs, oldVal, newVal) -> drawTable());
        drawTable();
    }

    private void drawTable() {
        schedulesTable.getChildren().clear();
        drawBackground();
        renderSchedules();
    }

    private void drawBackground() {
        //draw time label and timeline
        double totalWidth = Math.max(schedulesTable.getWidth(), 600);
        double currentHourHeight = hourHeight.get();
        for (int i = 0; i <= (END_HOUR - START_HOUR); i++) {
            double y = i * currentHourHeight;
            int hour = START_HOUR + i;
            Label timeLabel = new Label(String.format("%02d:00", hour));
            timeLabel.setLayoutY(y - 8);
            timeLabel.setLayoutX(5);
            timeLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #888888");

            Line line = new Line(TIME_COLUMN_WIDTH, y, totalWidth, y);
            line.setStroke(Color.web("#E0E0E0"));
            line.setStrokeWidth(1);
            line.endXProperty().bind(schedulesTable.widthProperty());
            schedulesTable.getChildren().addAll(timeLabel, line);
        }
        //Draw vertical line
        double dayWidth = (totalWidth - TIME_COLUMN_WIDTH) / 7;
        for (int i = 0; i <= 7; i++) {
            double x = TIME_COLUMN_WIDTH + i * dayWidth;
            Line vLine = new Line(x, 0, x, (END_HOUR - START_HOUR) * currentHourHeight);
            vLine.setStroke(Color.web("#F0F0F0"));
            vLine.endYProperty().bind(hourHeight.multiply(END_HOUR - START_HOUR));
            schedulesTable.getChildren().add(vLine);
        }
        //Update Table height
        schedulesTable.setPrefHeight((END_HOUR - START_HOUR) * currentHourHeight);
    }

    private void renderSchedules() {
        for (Schedule schedule : kernel.schedule().getScheduleList()) {
            for (DayOfWeek day : schedule.getDayOfWeeks()) {
                createCourseNode(schedule, day);
            }
        }
    }

    private void createCourseNode(Schedule schedule, DayOfWeek day) {
        String normalStyle = "-fx-background-color: derive(#4A90E2, 80%); -fx-background-radius: 4; -fx-border-color: #4A90E2; -fx-border-width: 0 0 0 4;";
        String selectedStyle = "-fx-background-color: derive(#4A90E2, 80%); -fx-background-radius: 4; -fx-border-color: #4A90E2; -fx-border-width: 2; -fx-border-radius: 4;";
        VBox node = new VBox();
        node.getStyleClass().add("course-card");
        node.setStyle(normalStyle);
        Label name = new Label(schedule.getName());
        name.setStyle("-fx-font-weight: bold; -fx-padding: 5;");
        Label info = new Label(schedule.getStartTime() + " - " + schedule.getEndTime() + "\n" +
                (schedule.getProfessor() != null ? schedule.getProfessor() : "") + "\n" +
                (schedule.getLocation() != null ? schedule.getLocation() : ""));
        info.setStyle("-fx-font-size: 10px; -fx-padding: 0 5 5 5;");
        node.getChildren().addAll(name, info);
        // Initialize Layout
        double startOffsetHours = schedule.getStartTime().getHour() - START_HOUR + schedule.getStartTime().getMinute() / 60.0;
        double durationHours = ChronoUnit.MINUTES.between(schedule.getStartTime(), schedule.getEndTime()) / 60.0;
        node.setLayoutY(startOffsetHours * hourHeight.get());
        node.setPrefHeight(durationHours * hourHeight.get());
        double totalWidth = Math.max(schedulesTable.getWidth(), 600);
        double dayWidth = (totalWidth - TIME_COLUMN_WIDTH) / 7;
        int dayIndex = day.getValue() - 1;
        node.setLayoutX(TIME_COLUMN_WIDTH + dayIndex * dayWidth + 2);
        node.setPrefWidth(dayWidth - 4);
        // Right-click Menu
        ContextMenu contextMenu = new ContextMenu();
        MenuItem modifyItem = new MenuItem("Modify");
        MenuItem deleteItem = new MenuItem("Delete");
        modifyItem.setOnAction(event -> {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getClassLoader().getResource("fxml/ScheduleDetail.fxml")));
            Parent root = null;
            try {
                root = loader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            ScheduleDetailController controller = loader.getController();
            controller.setKernel(kernel);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            controller.setMode(ScheduleDetailController.ViewMode.MODIFY);
            controller.setSchedule(schedule);
            stage.showAndWait();
            drawTable();
        });
        deleteItem.setOnAction(event -> {
            kernel.schedule().deleteSchedule(schedule);
            drawTable();
        });
        contextMenu.getItems().addAll(modifyItem, deleteItem);
        node.setOnContextMenuRequested(event -> {
            contextMenu.show(node, event.getScreenX(), event.getScreenY());
            node.setStyle(selectedStyle);
        });
        contextMenu.setOnHidden(e -> node.setStyle(normalStyle));
        schedulesTable.getChildren().add(node);
    }
}
