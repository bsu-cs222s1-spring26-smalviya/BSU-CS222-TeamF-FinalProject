package com.wiseplanner.gui.controller;

import com.wiseplanner.model.Course;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.List;

public class CoursesController extends BaseController {

    @FXML
    private FlowPane cardsPane;

    private MainWindowController mainWindowController;

    private static final String[] CARD_COLORS = {
            "#4A90D9", "#E67E22", "#2ECC71", "#9B59B6",
            "#E74C3C", "#1ABC9C", "#F39C12", "#3498DB"
    };

    public void setMainWindowController(MainWindowController mainWindowController) {
        this.mainWindowController = mainWindowController;
    }

    public void loadCourses() {
        cardsPane.setDisable(true);
        runAsync(
                () -> {
                    kernel.canvas().updateCourses();
                    return kernel.canvas().getCourses();
                },
                result -> {
                    cardsPane.getChildren().clear();
                    for (int i = 0; i < result.size(); i++) {
                        Course course = result.get(i);
                        String color = CARD_COLORS[i % CARD_COLORS.length];
                        VBox card = createCourseCard(course, color);
                        cardsPane.getChildren().add(card);
                    }
                    cardsPane.setDisable(false);
                },
                error -> {

                }
        );

    }

    private VBox createCourseCard(Course course, String color) {
        Rectangle colorBanner = new Rectangle(220, 120);
        colorBanner.setFill(Color.web(color));
        colorBanner.setArcWidth(10);
        colorBanner.setArcHeight(10);


        Label codeLabel = new Label(
                course.getCourse_code() != null ? course.getCourse_code() : "N/A"
        );
        codeLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");

        StackPane banner = new StackPane(colorBanner, codeLabel);

        Label nameLabel = new Label(course.getName());
        nameLabel.setWrapText(true);
        nameLabel.setMaxWidth(200);
        nameLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");


        Label termLabel = new Label(
                "Term: " + (course.getEnrollment_term_id() != null ? course.getEnrollment_term_id() : "N/A")
        );
        termLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #666666;");


        VBox card = new VBox(8, banner, nameLabel, termLabel);
        card.setPrefWidth(220);
        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #dddddd;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 0 10 10 10;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 6, 0, 0, 2);"
        );
        card.setAlignment(Pos.TOP_LEFT);


        card.setOnMouseClicked(event -> {
            try {
                mainWindowController.showCourseContextPage(course);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });


        card.setOnMouseEntered(e -> card.setStyle(
                "-fx-background-color: #f0f7ff;" +
                        "-fx-border-color: #4A90D9;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 0 10 10 10;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 8, 0, 0, 3);" +
                        "-fx-cursor: hand;"
        ));
        card.setOnMouseExited(e -> card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #dddddd;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 0 10 10 10;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 6, 0, 0, 2);"
        ));

        return card;
    }
}