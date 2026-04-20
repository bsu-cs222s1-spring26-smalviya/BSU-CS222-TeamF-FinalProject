package com.wiseplanner.gui.controller;

import com.wiseplanner.model.Announcement;
import com.wiseplanner.model.Course;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class AnnouncementsController extends BaseController {

    @FXML private AnchorPane anchorPane;
    @FXML private TableView<Announcement> announcementsTable;
    @FXML private TableColumn<Announcement, String> messageColumn;
    @FXML private TableColumn<Announcement, String> posted_atColumn;
    @FXML private TableColumn<Announcement, String> titleColumn;

    private final ObservableList<Announcement> announcements = FXCollections.observableArrayList();
    private Course course;
    private boolean tableConfigured = false;

    @FXML
    private void initialize() { configureTable(); }

    private enum PlaceholderMode { LOADING, EMPTY, FAILED }

    public void setCourse(Course course) { this.course = course; }

    public void loadAnnouncements() {
        setPlaceholder(PlaceholderMode.LOADING);
        runAsync(
                () -> {
                    kernel.canvas().updateAnnouncements(course);
                    return sortedAnnouncements(course.getAnnouncements());
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

    private List<Announcement> sortedAnnouncements(List<Announcement> list) {
        if (list == null) return List.of();
        return list.stream()
                .sorted(Comparator.comparing(
                        a -> (a.getPosted_at() == null ? "" : a.getPosted_at()),
                        Comparator.reverseOrder()))
                .toList();
    }

    public void configureTable() {
        if (tableConfigured) return;
        announcementsTable.setItems(announcements);
        announcementsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));

        messageColumn.setCellValueFactory(new PropertyValueFactory<>("message"));
        messageColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : stripHtml(item));
            }
        });

        posted_atColumn.setCellValueFactory(cellData -> {
            String p = cellData.getValue().getPosted_at();
            return new SimpleStringProperty(p != null ? formatDate(p) : "");
        });

        announcementsTable.getColumns().setAll(titleColumn, messageColumn, posted_atColumn);

        announcementsTable.setRowFactory(table -> {
            TableRow<Announcement> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    try {
                        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(
                                getClass().getClassLoader().getResource("fxml/AnnouncementDetail.fxml")));
                        Parent root = loader.load();
                        AnnouncementDetailController ctrl = loader.getController();
                        ctrl.setContent(row.getItem());
                        Stage stage = new Stage();
                        stage.initModality(Modality.APPLICATION_MODAL);
                        stage.setTitle("Announcement Detail");
                        stage.setScene(new Scene(root));
                        stage.showAndWait();
                    } catch (IOException e) { throw new RuntimeException(e); }
                }
            });
            return row;
        });
        tableConfigured = true;
    }

    private void setPlaceholder(PlaceholderMode mode) {
        String imgPath   = switch (mode) { case LOADING -> "/images/loading.png"; case EMPTY -> "/images/empty.png"; case FAILED -> "/images/failed.png"; };
        String labelText = switch (mode) { case LOADING -> "Loading..."; case EMPTY -> "Nothing here..."; case FAILED -> "Failed to load"; };
        ImageView iv = new ImageView(new Image(getClass().getResourceAsStream(imgPath)));
        iv.setFitHeight(48); iv.setFitWidth(48); iv.setSmooth(true);
        if (mode == PlaceholderMode.LOADING) {
            RotateTransition rt = new RotateTransition(Duration.seconds(1), iv);
            rt.setByAngle(360); rt.setCycleCount(Animation.INDEFINITE);
            rt.setInterpolator(Interpolator.LINEAR); rt.play();
        }
        VBox vBox = new VBox(15, iv, new Label(labelText));
        vBox.setAlignment(Pos.CENTER);
        announcementsTable.setPlaceholder(vBox);
    }

    private String stripHtml(String html) {
        if (html == null || html.equals("null")) return "No Description";
        String c = html.replaceAll("<[^>]*>", "").replaceAll("\\s+", " ").trim();
        return c.length() > 80 ? c.substring(0, 77) + "..." : c;
    }

    private String formatDate(String iso) {
        try {
            String[] months = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
            String[] p = iso.substring(0, 10).split("-");
            String time = iso.length() > 16 ? " " + iso.substring(11, 16) : "";
            return months[Integer.parseInt(p[1]) - 1] + " " + p[2] + ", " + p[0] + time;
        } catch (Exception e) { return iso; }
    }
}