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
import java.util.Stack;

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
    private Button schedulesButton;

    @FXML
    private Button settingsButton;

    @FXML
    private Button tasksButton;

    @FXML
    private HBox topBar;

    @FXML
    private Button backButton;

    // Stack works like a history — last page in, first page out
    private final Stack<Parent> history = new Stack<>();

    @FXML
    void onCoursesButtonClick(ActionEvent event) throws IOException {
        history.clear(); // clear history when navigating from the sidebar
        showCoursesPage();
    }

    @FXML
    void onSchedulesButtonClick(ActionEvent event) throws IOException {
        history.clear();
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getClassLoader().getResource("fxml/Schedules.fxml")));
        Parent node = loader.load();
        SchedulesController controller = loader.getController();
        controller.setKernel(kernel);
        controller.loadSchedules();
        changePage(node);
    }

    @FXML
    void onSettingsButtonClick(ActionEvent event) throws IOException {
        history.clear();
        FXMLLoader loader = new FXMLLoader(
                Objects.requireNonNull(getClass().getClassLoader().getResource("fxml/Settings.fxml"))
        );
        Parent node = loader.load();
        SettingsController controller = loader.getController();
        controller.setKernel(kernel);
        controller.setMainWindowController(this);
        changePage(node);
    }

    @FXML
    void onTasksButtonClick(ActionEvent event) throws IOException {
        history.clear();
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getClassLoader().getResource("fxml/Tasks.fxml")));
        Parent node = loader.load();
        TasksController controller = loader.getController();
        controller.setKernel(kernel);
        controller.loadTasks();
        changePage(node);
    }

    @FXML
    void onBackButtonClick(ActionEvent event) {
        if (!history.isEmpty()) {
            Parent previousPage = history.pop();
            pagePane.getChildren().setAll(previousPage);
            updateBackButton();
        }
    }

    public void setKernel(WisePlannerKernel kernel) {
        super.setKernel(kernel);
        kernel.initialize();
        updateBackButton();
    }

    // Call this when navigating forward — saves current page to history
    public void changePage(Parent node) {
        if (!pagePane.getChildren().isEmpty()) {
            history.push((Parent) pagePane.getChildren().get(0));
        }
        pagePane.getChildren().setAll(node);
        updateBackButton();
    }

    // Hides the back button when there's nothing to go back to
    private void updateBackButton() {
        if (backButton != null) {
            backButton.setVisible(!history.isEmpty());
        }
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