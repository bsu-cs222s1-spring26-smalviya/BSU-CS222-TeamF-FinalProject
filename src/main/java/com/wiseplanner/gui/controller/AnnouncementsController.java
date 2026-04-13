package com.wiseplanner.gui.controller;

import com.wiseplanner.model.Announcement;
import com.wiseplanner.model.Course;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

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

    public void setCourse(Course course) {
        this.course = course;
    }

    public void loadAnnouncements() {
        try{
            kernel.canvas().updateAnnouncements(course);
            announcements.setAll(course.getAnnouncements());
            announcementsTable.setItems(announcements);
        } catch (Exception e) {
            System.err.println("[Error] " + e.getMessage());
        }

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
