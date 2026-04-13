package com.wiseplanner.gui.controller;

import com.wiseplanner.model.Course;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.Objects;

public class CourseContextController extends BaseController {

    @FXML
    private BorderPane borderPane;

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
    void onAnnouncementsBtnClick(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getClassLoader().getResource("fxml/Announcements.fxml")));
        Parent node = loader.load();
        AnnouncementsController controller = loader.getController();
        controller.setKernel(kernel);
        controller.setCourse(course);
        controller.loadAnnouncements();
        changeContext(node);
    }

    @FXML
    void onAssignmentsBtnClick(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                Objects.requireNonNull(getClass().getClassLoader().getResource("fxml/Assignments.fxml"))
        );
        Parent node = loader.load();
        AssignmentsController controller = loader.getController();
        controller.setKernel(kernel);
        controller.setCourse(course);
        controller.loadAssignments();
        changeContext(node);
    }

    public void changeContext(Parent node) {
        stretchToFill(node);
        courseContextPane.getChildren().setAll(node);
    }
}
