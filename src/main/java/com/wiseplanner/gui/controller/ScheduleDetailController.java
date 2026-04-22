package com.wiseplanner.gui.controller;

import com.wiseplanner.exception.FileWriteException;
import com.wiseplanner.model.Schedule;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.stage.Window;
import javafx.stage.Stage;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

public class ScheduleDetailController extends BaseController {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private Button cancelButton;

    @FXML
    private Label dayOfWeeksLabel;

    @FXML
    private GridPane dayOfWeeksPane;

    @FXML
    private Spinner<Integer> endTimeHourSpinner;

    @FXML
    private Label endTimeLabel;

    @FXML
    private Spinner<Integer> endTimeMinuteSpinner;

    @FXML
    private CheckBox fridayBox;

    @FXML
    private TextField locationField;

    @FXML
    private Label locationLabel;

    @FXML
    private CheckBox mondayBox;

    @FXML
    private TextField nameField;

    @FXML
    private Label nameLabel;

    @FXML
    private Button okButton;

    @FXML
    private TextField professorField;

    @FXML
    private Label professorLabel;

    @FXML
    private CheckBox saturdayBox;

    @FXML
    private Spinner<Integer> startTimeHourSpinner;

    @FXML
    private Label startTimeLabel;

    @FXML
    private Spinner<Integer> startTimeMinuteSpinner;

    @FXML
    private CheckBox sundayBox;

    @FXML
    private CheckBox thursdayBox;

    @FXML
    private Label titleLabel;

    @FXML
    private CheckBox tuesdayBox;

    @FXML
    private CheckBox wednesdayBox;

    @FXML
    void onCancelButtonClick(ActionEvent event) {
        closeWindow();
    }

    @FXML
    void onOkButtonClick(ActionEvent event) {
        if (currentMode == ViewMode.ADD) {
            addSchedule();
        } else if (currentMode == ViewMode.MODIFY) {
            modifySchedule();
        }
        closeWindow();
    }

    @FXML
    private void initialize() {
        startTimeHourSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 12));
        startTimeMinuteSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0));
        endTimeHourSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 12));
        endTimeMinuteSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0));
    }

    private ViewMode currentMode;
    private Schedule currentSchedule;

    public enum ViewMode {
        ADD, MODIFY, VIEW
    }

    public void setMode(ViewMode mode) {
        this.currentMode = mode;
        switch (mode) {
            case ADD:
                setWindowTitle("Add Schedule");
                titleLabel.setText("Add Schedule");
                okButton.setText("OK");
                break;
            case MODIFY:
                setWindowTitle("Modify Schedule");
                titleLabel.setText("Modify Schedule");
                okButton.setText("OK");
                break;
            case VIEW:
                setWindowTitle("View Schedule");
                titleLabel.setText("View Schedule");
                nameField.setEditable(false);
                mondayBox.setDisable(true);
                mondayBox.setOpacity(1.0);
                tuesdayBox.setDisable(true);
                tuesdayBox.setOpacity(1.0);
                wednesdayBox.setDisable(true);
                wednesdayBox.setOpacity(1.0);
                thursdayBox.setDisable(true);
                thursdayBox.setOpacity(1.0);
                fridayBox.setDisable(true);
                fridayBox.setOpacity(1.0);
                saturdayBox.setDisable(true);
                saturdayBox.setOpacity(1.0);
                sundayBox.setDisable(true);
                sundayBox.setOpacity(1.0);
                startTimeHourSpinner.setDisable(true);
                startTimeHourSpinner.setOpacity(1.0);
                startTimeMinuteSpinner.setDisable(true);
                startTimeMinuteSpinner.setOpacity(1.0);
                endTimeHourSpinner.setDisable(true);
                endTimeHourSpinner.setOpacity(1.0);
                endTimeMinuteSpinner.setDisable(true);
                endTimeMinuteSpinner.setOpacity(1.0);
                professorField.setEditable(false);
                locationField.setEditable(false);
                cancelButton.setVisible(false);
                cancelButton.setManaged(false);
                okButton.setText("Close");
                break;
        }
    }

    private void setWindowTitle(String title) {
        if (okButton.getScene() != null && okButton.getScene().getWindow() instanceof Stage stage) {
            stage.setTitle(title);
            return;
        }
        okButton.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                Window window = newScene.getWindow();
                if (window instanceof Stage stage) {
                    stage.setTitle(title);
                } else {
                    newScene.windowProperty().addListener((windowObs, oldWindow, newWindow) -> {
                        if (newWindow instanceof Stage delayedStage) {
                            delayedStage.setTitle(title);
                        }
                    });
                }
            }
        });
    }

    private void addSchedule() {
        Set<DayOfWeek> dayOfWeekSet = new HashSet<>();
        if (mondayBox.isSelected()) dayOfWeekSet.add(DayOfWeek.MONDAY);
        if (tuesdayBox.isSelected()) dayOfWeekSet.add(DayOfWeek.TUESDAY);
        if (wednesdayBox.isSelected()) dayOfWeekSet.add(DayOfWeek.WEDNESDAY);
        if (thursdayBox.isSelected()) dayOfWeekSet.add(DayOfWeek.THURSDAY);
        if (fridayBox.isSelected()) dayOfWeekSet.add(DayOfWeek.FRIDAY);
        if (saturdayBox.isSelected()) dayOfWeekSet.add(DayOfWeek.SATURDAY);
        if (sundayBox.isSelected()) dayOfWeekSet.add(DayOfWeek.SUNDAY);
        LocalTime startTime = LocalTime.of(startTimeHourSpinner.getValue(), startTimeMinuteSpinner.getValue());
        LocalTime endTime = LocalTime.of(endTimeHourSpinner.getValue(), endTimeMinuteSpinner.getValue());
        if (nameField.getText().isBlank() || dayOfWeekSet.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Invalid input. Please check your input.", ButtonType.OK);
            alert.showAndWait();
        } else {
            try {
                kernel.schedule().addSchedule(
                        nameField.getText(),
                        dayOfWeekSet,
                        startTime,
                        endTime,
                        professorField.getText(),
                        locationField.getText()
                );
            } catch (FileWriteException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
                alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                alert.showAndWait();
            }
        }
    }

    public void setSchedule(Schedule schedule) {
        this.currentSchedule = schedule;
        nameField.setText(schedule.getName());
        for (DayOfWeek day : schedule.getDayOfWeeks()) {
            if (day == DayOfWeek.MONDAY) mondayBox.setSelected(true);
            if (day == DayOfWeek.TUESDAY) tuesdayBox.setSelected(true);
            if (day == DayOfWeek.WEDNESDAY) wednesdayBox.setSelected(true);
            if (day == DayOfWeek.THURSDAY) thursdayBox.setSelected(true);
            if (day == DayOfWeek.FRIDAY) fridayBox.setSelected(true);
            if (day == DayOfWeek.SATURDAY) saturdayBox.setSelected(true);
            if (day == DayOfWeek.SUNDAY) sundayBox.setSelected(true);
        }
        startTimeHourSpinner.getValueFactory().setValue(schedule.getStartTime().getHour());
        startTimeMinuteSpinner.getValueFactory().setValue(schedule.getStartTime().getMinute());
        endTimeHourSpinner.getValueFactory().setValue(schedule.getEndTime().getHour());
        endTimeMinuteSpinner.getValueFactory().setValue(schedule.getEndTime().getMinute());
        professorField.setText(schedule.getProfessor());
        locationField.setText(schedule.getLocation());
    }

    private void modifySchedule() {
        Set<DayOfWeek> dayOfWeekSet = new HashSet<>();
        if (mondayBox.isSelected()) dayOfWeekSet.add(DayOfWeek.MONDAY);
        if (tuesdayBox.isSelected()) dayOfWeekSet.add(DayOfWeek.TUESDAY);
        if (wednesdayBox.isSelected()) dayOfWeekSet.add(DayOfWeek.WEDNESDAY);
        if (thursdayBox.isSelected()) dayOfWeekSet.add(DayOfWeek.THURSDAY);
        if (fridayBox.isSelected()) dayOfWeekSet.add(DayOfWeek.FRIDAY);
        if (saturdayBox.isSelected()) dayOfWeekSet.add(DayOfWeek.SATURDAY);
        if (sundayBox.isSelected()) dayOfWeekSet.add(DayOfWeek.SUNDAY);
        LocalTime startTime = LocalTime.of(startTimeHourSpinner.getValue(), startTimeMinuteSpinner.getValue());
        LocalTime endTime = LocalTime.of(endTimeHourSpinner.getValue(), endTimeMinuteSpinner.getValue());
        if (nameField.getText().isBlank() || dayOfWeekSet.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Invalid input. Please check your input.", ButtonType.OK);
            alert.showAndWait();
        } else {
            currentSchedule.setName(nameField.getText());
            currentSchedule.setDayOfWeeks(dayOfWeekSet);
            currentSchedule.setStartTime(startTime);
            currentSchedule.setEndTime(endTime);
            currentSchedule.setProfessor(professorField.getText());
            currentSchedule.setLocation(locationField.getText());
            try {
                kernel.schedule().saveSchedule();
            } catch (FileWriteException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
                alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                alert.showAndWait();
            }
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) okButton.getScene().getWindow();
        stage.close();
    }
}
