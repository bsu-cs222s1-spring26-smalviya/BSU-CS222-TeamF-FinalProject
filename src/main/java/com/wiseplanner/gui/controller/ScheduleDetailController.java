package com.wiseplanner.gui.controller;

import com.wiseplanner.model.Schedule;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
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
        Stage stage = (Stage) okButton.getScene().getWindow();
        switch (mode) {
            case ADD:
                this.currentMode = ViewMode.ADD;
                stage.setTitle("Add Schedule");
                titleLabel.setText("Add Schedule");
                break;
            case MODIFY:
                this.currentMode = ViewMode.MODIFY;
                stage.setTitle("View Schedule");
                titleLabel.setText("View Schedule");
                break;
            case VIEW:
                this.currentMode = ViewMode.VIEW;
                break;
        }
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
        if (!nameField.getText().isBlank() || !dayOfWeekSet.isEmpty()) {
            kernel.schedule().addSchedule(
                    nameField.getText(),
                    dayOfWeekSet,
                    startTime,
                    endTime,
                    professorField.getText(),
                    locationField.getText()
            );
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Invalid input. Please check your input.", ButtonType.OK);
            alert.showAndWait();
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
        if (!nameField.getText().isBlank() && !dayOfWeekSet.isEmpty()) {
            currentSchedule.setName(nameField.getText());
            currentSchedule.setDayOfWeeks(dayOfWeekSet);
            currentSchedule.setStartTime(startTime);
            currentSchedule.setEndTime(endTime);
            currentSchedule.setProfessor(professorField.getText());
            currentSchedule.setLocation(locationField.getText());
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Invalid input. Please check your input.", ButtonType.OK);
            alert.showAndWait();
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) okButton.getScene().getWindow();
        stage.close();
    }
}
