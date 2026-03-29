package com.wiseplanner.gui.controller;

import com.wiseplanner.model.Course;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class CoursesController extends BaseController {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private TableView<Course> coursesTable;

    @FXML
    private TableColumn<Course, String> idColumn;

    @FXML
    private TableColumn<Course, String> nameColumn;

    private MainWindowController mainWindowController;
    private final ObservableList<Course> courses = FXCollections.observableArrayList();
    private boolean tableConfigured = false;

    @FXML
    private void initialize() {
        configureTable();
    }

    public void setMainWindowController(MainWindowController mainWindowController) {
        this.mainWindowController = mainWindowController;
    }

    public void loadCourses() {
        kernel.canvas().updateCourses();
        courses.setAll(kernel.canvas().getCourses());
        coursesTable.setItems(courses);
    }

    private void configureTable() {
        if (tableConfigured) {
            return;
        }

        coursesTable.setItems(courses);
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        coursesTable.getColumns().setAll(idColumn, nameColumn);
        coursesTable.setRowFactory(tv -> {
            TableRow<Course> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 1 && (!row.isEmpty())) {
                    Course selectedCourse = row.getItem();
                    try {
                        navigateToContext(selectedCourse);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            return row;
        });
        tableConfigured = true;
    }

    public void navigateToContext(Course course) throws IOException {
        mainWindowController.showCourseContextPage(course);
    }
}
