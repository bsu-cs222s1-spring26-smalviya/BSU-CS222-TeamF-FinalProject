package com.wiseplanner.gui.controller;

import com.wiseplanner.core.WisePlannerKernel;
import com.wiseplanner.model.Course;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Objects;

public class MainWindowController extends BaseController {

    @FXML
    private BorderPane borderPane;

    @FXML
    private Button coursesButton;

    @FXML
    private VBox navigationBar;

    @FXML
    private StackPane pagePane;

    @FXML
    private Button settingsButton;

    @FXML
    private Button tasksButton;

    @FXML
    private HBox topBar;

    @FXML
    void onCoursesButtonClick(ActionEvent event) throws IOException {
        showCoursesPage();
    }

    @FXML
    void onSettingsButtonClick(ActionEvent event) {

    }

    @FXML
    void onTasksButtonClick(ActionEvent event) {

    }

    public void setKernel(WisePlannerKernel kernel) {
        super.setKernel(kernel);
        kernel.initialize();
    }

    public void changePage(Parent node) {
        pagePane.getChildren().setAll(node);
    }

    public void showCoursesPage() throws IOException {
        FXMLLoader loader = new FXMLLoader(
                Objects.requireNonNull(getClass().getClassLoader().getResource("fxml/Courses.fxml"))
        );
        Parent node = loader.load();
        CoursesController controller = loader.getController();
        controller.setKernel(kernel);
        controller.setMainWindowController(this);
        controller.loadCourses();
        changePage(node);
    }

    public void showCourseContextPage(Course course) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                Objects.requireNonNull(getClass().getClassLoader().getResource("fxml/CourseContext.fxml"))
        );
        Parent node = loader.load();
        CourseContextController controller = loader.getController();
        controller.setKernel(kernel);
        controller.setMainWindowController(this);
        controller.setCourse(course);
        changePage(node);
    }
}
