package com.wiseplanner.gui.controller;

import com.wiseplanner.model.Course;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

public class CourseContextController extends BaseController {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private Button announcementsButton;

    @FXML
    private Button assignmentsBtn;

    @FXML
    private Label courseNameLabel;

    @FXML
    private StackPane courseContextPane;

    private MainWindowController mainWindowController;
    private Course course;

    public void setMainWindowController(MainWindowController mainWindowController) {
        this.mainWindowController = mainWindowController;
    }

    public void setCourse(Course course) {
        this.course = course;
        renderCourse();
    }

    private void renderCourse() {
        if (courseNameLabel != null && course != null) {
            courseNameLabel.setText(course.getName());
        }
    }

    @FXML
    void onAnnouncementsBtnClick(ActionEvent event) {

    }

    @FXML
    void onAssignmentsBtnClick(ActionEvent event) {

    }

}
