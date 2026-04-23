package com.wiseplanner.gui.controller;

import com.wiseplanner.model.Course;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.io.IOException;

public class CoursesController extends BaseController {

    @FXML private FlowPane cardsPane;
    @FXML private ScrollPane coursesScrollPane;
    @FXML private StackPane contentPane;

    private StackPane placeholderOverlay;
    private MainWindowController mainWindowController;

    private static final String[] CARD_COLORS = {
            "#4A90D9", "#E67E22", "#2ECC71", "#9B59B6",
            "#E74C3C", "#1ABC9C", "#F39C12", "#3498DB"
    };

    private enum PlaceholderMode { LOADING, EMPTY, FAILED }

    public void setMainWindowController(MainWindowController mainWindowController) {
        this.mainWindowController = mainWindowController;
    }

    @FXML
    private void initialize() {
        coursesScrollPane.viewportBoundsProperty().addListener((obs, oldBounds, newBounds) -> {
            contentPane.setMinWidth(newBounds.getWidth());
            contentPane.setMinHeight(newBounds.getHeight());
        });
    }

    private void setPlaceholder(PlaceholderMode mode) {
        cardsPane.getChildren().clear();
        contentPane.getChildren().remove(placeholderOverlay);
        VBox vBox = null;
        if (mode.equals(PlaceholderMode.LOADING)) {
            ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream("/images/loading.png")));
            imageView.setFitHeight(48);
            imageView.setFitWidth(48);
            imageView.setSmooth(true);
            RotateTransition rotateTransition = new RotateTransition(Duration.seconds(1), imageView);
            rotateTransition.setByAngle(360);
            rotateTransition.setCycleCount(Animation.INDEFINITE);
            rotateTransition.setInterpolator(Interpolator.LINEAR);
            rotateTransition.play();
            Label statusLabel = createPlaceholderLabel("Loading...");
            vBox = new VBox(15, imageView, statusLabel);
        }
        if (mode.equals(PlaceholderMode.EMPTY)) {
            ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream("/images/empty.png")));
            imageView.setFitHeight(48);
            imageView.setFitWidth(48);
            imageView.setSmooth(true);
            Label statusLabel = createPlaceholderLabel("Nothing here...");
            vBox = new VBox(15, imageView, statusLabel);
        }
        if (mode.equals(PlaceholderMode.FAILED)) {
            ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream("/images/failed.png")));
            imageView.setFitHeight(48);
            imageView.setFitWidth(48);
            imageView.setSmooth(true);
            Label statusLabel = createPlaceholderLabel("Fail to load");
            vBox = new VBox(15, imageView, statusLabel);
        }
        vBox.setAlignment(Pos.CENTER);
        placeholderOverlay = new StackPane(vBox);
        placeholderOverlay.setAlignment(Pos.CENTER);
        placeholderOverlay.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        contentPane.getChildren().add(placeholderOverlay);
    }

    public void loadCourses() {
        setPlaceholder(PlaceholderMode.LOADING);
        runAsync(
                () -> {
                    kernel.canvas().updateCourses();
                    return kernel.canvas().getCourses();
                },
                result -> {
                    cardsPane.getChildren().clear();
                    if (result == null || result.isEmpty()) {
                        setPlaceholder(PlaceholderMode.EMPTY);
                        return;
                    }
                    contentPane.getChildren().remove(placeholderOverlay);
                    for (int i = 0; i < result.size(); i++) {
                        Course course = result.get(i);
                        String color = CARD_COLORS[i % CARD_COLORS.length];
                        VBox card = createCourseCard(course, color);
                        cardsPane.getChildren().add(card);
                    }
                },
                error -> {
                    setPlaceholder(PlaceholderMode.FAILED);
                    Alert alert = new Alert(Alert.AlertType.ERROR, error.getMessage(), ButtonType.OK);
                    alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                    alert.showAndWait();
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

        Image iconImage = new Image(getClass().getResourceAsStream("/images/course_white.png"));
        ImageView assignmentIcon = new ImageView(iconImage);
        assignmentIcon.setFitWidth(100);
        assignmentIcon.setFitHeight(100);
        assignmentIcon.setPreserveRatio(true);
        assignmentIcon.setSmooth(true);
        assignmentIcon.setOpacity(0.3);
        StackPane banner = new StackPane(colorBanner,assignmentIcon, codeLabel);
        banner.setMargin(codeLabel, new javafx.geometry.Insets(0, 90, 0, 10));
        banner.setAlignment(assignmentIcon, Pos.BOTTOM_RIGHT);

        Label nameLabel = new Label(course.getName());
        nameLabel.setWrapText(true);
        nameLabel.setMaxWidth(200);

        nameLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #2D3B45;");

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
                        "-fx-padding: 10 10 10 10;" +
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
                        "-fx-padding: 10 10 10 10;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 8, 0, 0, 3);" +
                        "-fx-cursor: hand;"
        ));
        card.setOnMouseExited(e -> card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #dddddd;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 10 10 10 10;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 6, 0, 0, 2);"
        ));

        return card;
    }
}
