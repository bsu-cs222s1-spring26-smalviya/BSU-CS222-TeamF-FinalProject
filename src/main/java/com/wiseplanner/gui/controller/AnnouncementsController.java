package com.wiseplanner.gui.controller;

import com.wiseplanner.model.Announcement;
import com.wiseplanner.model.Course;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class AnnouncementsController extends BaseController {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private TableView<Announcement> announcementsTable;

    @FXML
    private TableColumn<Announcement, String> idColumn;

    @FXML
    private TableColumn<Announcement, String> messageColumn;

    @FXML
    private TableColumn<Announcement, String> posted_atColumn;

    @FXML
    private TableColumn<Announcement, String> titleColumn;

    private final ObservableList<Announcement> announcements = FXCollections.observableArrayList();
    private Course course;
    private boolean tableConfigured = false;

    @FXML
    private void initialize() {
        configureTable();
    }

    private enum PlaceholderMode {
        LOADING, EMPTY, FAILED
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public void loadAnnouncements() {
        setPlaceholder(PlaceholderMode.LOADING);
        runAsync(
                () -> {
                    kernel.canvas().updateAnnouncements(course);
                    return course.getAnnouncements();
                },
                result -> {
                    announcements.setAll(result);
                    announcementsTable.setItems(announcements);
                    setPlaceholder(PlaceholderMode.EMPTY);
                },
                error -> {
                    setPlaceholder(PlaceholderMode.FAILED);
                    Alert alert = new Alert(Alert.AlertType.ERROR, error.getMessage(), ButtonType.OK);
                    alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                    alert.showAndWait();
                }
        );

    }

    public void configureTable() {
        if (tableConfigured) {
            return;
        }
        announcementsTable.setItems(announcements);
        announcementsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        messageColumn.setCellValueFactory(new PropertyValueFactory<>("message"));
        posted_atColumn.setCellValueFactory(new PropertyValueFactory<>("posted_at"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        messageColumn.setCellFactory(column -> new TableCell<Announcement, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(stripHtml(item));
                }
            }

        });
        announcementsTable.getColumns().setAll(idColumn, titleColumn, messageColumn, posted_atColumn);
        tableConfigured = true;
    }

    private void setPlaceholder(PlaceholderMode mode) {
        if (mode.equals(PlaceholderMode.LOADING)) {
            Image image = new Image(getClass().getResourceAsStream("/images/loading.png"));
            ImageView imageView = new ImageView(image);
            imageView.setFitHeight(48);
            imageView.setFitWidth(48);
            imageView.setSmooth(true);
            RotateTransition rotateTransition = new RotateTransition(Duration.seconds(1), imageView);
            rotateTransition.setByAngle(360);
            rotateTransition.setCycleCount(Animation.INDEFINITE);
            rotateTransition.setInterpolator(Interpolator.LINEAR);
            rotateTransition.play();
            Label statusLabel = new Label("Loading...");
            VBox vBox = new VBox(15);
            vBox.setAlignment(Pos.CENTER);
            vBox.getChildren().addAll(imageView, statusLabel);
            announcementsTable.setPlaceholder(vBox);
        }
        if (mode.equals(PlaceholderMode.EMPTY)) {
            Image image = new Image(getClass().getResourceAsStream("/images/empty.png"));
            ImageView imageView = new ImageView(image);
            imageView.setFitHeight(48);
            imageView.setFitWidth(48);
            imageView.setSmooth(true);
            Label statusLabel = new Label("Nothing here...");
            VBox vBox = new VBox(15);
            vBox.setAlignment(Pos.CENTER);
            vBox.getChildren().addAll(imageView, statusLabel);
            announcementsTable.setPlaceholder(vBox);
        }
        if (mode.equals(PlaceholderMode.FAILED)) {
            Image image = new Image(getClass().getResourceAsStream("/images/failed.png"));
            ImageView imageView = new ImageView(image);
            imageView.setFitHeight(48);
            imageView.setFitWidth(48);
            imageView.setSmooth(true);
            Label statusLabel = new Label("Failed to load");
            VBox vBox = new VBox(15);
            vBox.setAlignment(Pos.CENTER);
            vBox.getChildren().addAll(imageView, statusLabel);
            announcementsTable.setPlaceholder(vBox);
        }
    }

    private String stripHtml(String html) {
        if (html == null || html.equals("null")) {
            return "No Description";
        }

        String noTags = html.replaceAll("<[^>]*>", "");
        String cleaned = noTags.replaceAll("\\s+", " ").trim();
        if (cleaned.length() > 80) {
            return cleaned.substring(0, 77) + "...";
        }

        return cleaned;
    }
}
