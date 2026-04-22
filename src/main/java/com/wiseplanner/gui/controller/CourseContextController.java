package com.wiseplanner.gui.controller;

import com.wiseplanner.model.Announcement;
import com.wiseplanner.model.Assignment;
import com.wiseplanner.model.Course;
import com.wiseplanner.model.Submission;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class CourseContextController extends BaseController {

    @FXML private Label courseNameLabel;
    @FXML private Label assignmentsStatusLabel;
    @FXML private Label announcementsStatusLabel;
    @FXML private VBox  assignmentsListBox;
    @FXML private VBox  announcementsListBox;

    private MainWindowController mainWindowController;
    private Course course;

    public void setMainWindowController(MainWindowController mainWindowController) {
        this.mainWindowController = mainWindowController;
    }

    public void setCourse(Course course) {
        this.course = course;
        if (courseNameLabel != null) courseNameLabel.setText(course.getName());
        loadBoth();
    }



    private void loadBoth() {
        assignmentsStatusLabel.setText("Loading…");
        announcementsStatusLabel.setText("Loading…");

        new Thread(() -> {
            try {
                kernel.canvas().updateAssignments(course);
                List<Assignment> sorted = sortedAssignments(course.getAssignments());
                Platform.runLater(() -> {
                    assignmentsStatusLabel.setText("");
                    populateAssignments(sorted);
                });
            } catch (Exception e) {
                Platform.runLater(() ->
                        assignmentsStatusLabel.setText("[Error] " + e.getMessage()));
            }
        }).start();

        new Thread(() -> {
            try {
                kernel.canvas().updateAnnouncements(course);
                List<Announcement> sorted = sortedAnnouncements(course.getAnnouncements());
                Platform.runLater(() -> {
                    announcementsStatusLabel.setText("");
                    populateAnnouncements(sorted);
                });
            } catch (Exception e) {
                Platform.runLater(() ->
                        announcementsStatusLabel.setText("[Error] " + e.getMessage()));
            }
        }).start();
    }


    private List<Assignment> sortedAssignments(List<Assignment> list) {
        if (list == null) return List.of();
        return list.stream()
                .sorted(Comparator
                        .<Assignment, Integer>comparing(a -> isUnsubmitted(a) ? 0 : 1)
                        .thenComparing(a -> {
                            String due = a.getDue_at();
                            return (due == null || due.equalsIgnoreCase("null")) ? "" : due;
                        }, Comparator.reverseOrder()))
                .toList();
    }

    private boolean isUnsubmitted(Assignment a) {
        Submission s = a.getSubmission();
        return s == null || s.getWorkflow_state() == null
                || s.getWorkflow_state().equalsIgnoreCase("unsubmitted");
    }


    private List<Announcement> sortedAnnouncements(List<Announcement> list) {
        if (list == null) return List.of();
        return list.stream()
                .sorted(Comparator.comparing(
                        a -> (a.getPosted_at() == null ? "" : a.getPosted_at()),
                        Comparator.reverseOrder()))
                .toList();
    }



    private void populateAssignments(List<Assignment> list) {
        assignmentsListBox.getChildren().clear();
        if (list.isEmpty()) {
            assignmentsListBox.getChildren().add(makeInfoLabel("No assignments found."));
            return;
        }
        for (Assignment a : list) {
            assignmentsListBox.getChildren().add(makeAssignmentCard(a));
        }
    }

    private void populateAnnouncements(List<Announcement> list) {
        announcementsListBox.getChildren().clear();
        if (list.isEmpty()) {
            announcementsListBox.getChildren().add(makeInfoLabel("No announcements found."));
            return;
        }
        for (Announcement a : list) {
            announcementsListBox.getChildren().add(makeAnnouncementCard(a));
        }
    }



    private VBox makeAssignmentCard(Assignment a) {
        boolean unsubmitted = isUnsubmitted(a);
        String state = a.getSubmission() != null && a.getSubmission().getWorkflow_state() != null
                ? a.getSubmission().getWorkflow_state() : "unsubmitted";
        boolean late    = a.getSubmission() != null && a.getSubmission().getLate();
        boolean missing = a.getSubmission() != null && a.getSubmission().getMissing();

        String badgeColor  = unsubmitted ? "#e67e22" : "#27ae60";
        if (late)    badgeColor = "#c0392b";
        if (missing) badgeColor = "#8e44ad";
        String borderColor = unsubmitted ? "#e67e22" : "#dddddd";

        Label titleLabel = new Label(a.getName());
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13; -fx-text-fill: #2D3B45;");
        titleLabel.setWrapText(true);

        Label descLabel = new Label(stripHtml(a.getDescription()));
        descLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #555555;");
        descLabel.setWrapText(true);

        String dueText = (a.getDue_at() == null || a.getDue_at().equalsIgnoreCase("null"))
                ? "No due date" : "Due: " + formatDate(a.getDue_at());
        String stateText = state + (late ? " · LATE" : "") + (missing ? " · MISSING" : "");

        Label dueLabel = new Label(dueText);
        dueLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #777;");

        final String finalBadge = badgeColor;
        Label stateLabel = new Label(stateText);
        stateLabel.setStyle("-fx-font-size: 11; -fx-text-fill: white; -fx-background-color: "
                + finalBadge + "; -fx-background-radius: 8; -fx-padding: 2 7 2 7;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        HBox metaRow = new HBox(8, dueLabel, spacer, stateLabel);

        VBox card = new VBox(4, titleLabel, descLabel, metaRow);
        card.setStyle("-fx-padding: 8 10 8 10; -fx-background-color: #fafafa;"
                + "-fx-border-color: " + borderColor + "; -fx-border-radius: 6;"
                + "-fx-background-radius: 6; -fx-border-width: 0 0 0 3;");

        card.setOnMouseClicked(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(
                        getClass().getClassLoader().getResource("fxml/AssignmentDetail.fxml")));
                javafx.scene.Parent root = loader.load();
                AssignmentDetailController ctrl = loader.getController();
                ctrl.setContent(a);
                javafx.stage.Stage stage = new javafx.stage.Stage();
                stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
                stage.setTitle(a.getName());
                stage.setScene(new javafx.scene.Scene(root));
                stage.showAndWait();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        card.setStyle(card.getStyle() + "-fx-cursor: hand;");
        return card;
    }

    private VBox makeAnnouncementCard(Announcement a) {
        Label titleLabel = new Label(a.getTitle());
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13; -fx-text-fill: #2D3B45;");
        titleLabel.setWrapText(true);

        Label msgLabel = new Label(stripHtml(a.getMessage()));
        msgLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #555;");
        msgLabel.setWrapText(true);

        Label dateLabel = new Label(a.getPosted_at() != null ? formatDate(a.getPosted_at()) : "");
        dateLabel.setStyle("-fx-font-size: 10; -fx-text-fill: #aaa;");

        VBox card = new VBox(3, titleLabel, msgLabel, dateLabel);
        card.setStyle("-fx-padding: 8 10 8 10; -fx-background-color: #fafafa;"
                + "-fx-border-color: #dddddd; -fx-border-radius: 6;"
                + "-fx-background-radius: 6; -fx-border-width: 0 0 0 3;");

        card.setOnMouseClicked(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(
                        getClass().getClassLoader().getResource("fxml/AnnouncementDetail.fxml")));
                javafx.scene.Parent root = loader.load();
                AnnouncementDetailController ctrl = loader.getController();
                ctrl.setContent(a);
                javafx.stage.Stage stage = new javafx.stage.Stage();
                stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
                stage.setTitle(a.getTitle());
                stage.setScene(new javafx.scene.Scene(root));
                stage.showAndWait();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        card.setStyle(card.getStyle() + "-fx-cursor: hand;");
        return card;
    }



    @FXML
    void onShowAllAssignmentsClick(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(
                getClass().getClassLoader().getResource("fxml/Assignments.fxml")));
        Parent node = loader.load();
        AssignmentsController controller = loader.getController();
        controller.setKernel(kernel);
        controller.setCourse(course);
        controller.loadAssignments();
        mainWindowController.changePage(node);
    }

    @FXML
    void onShowAllAnnouncementsClick(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(
                getClass().getClassLoader().getResource("fxml/Announcements.fxml")));
        Parent node = loader.load();
        AnnouncementsController controller = loader.getController();
        controller.setKernel(kernel);
        controller.setCourse(course);
        controller.loadAnnouncements();
        mainWindowController.changePage(node);
    }



    private Label makeInfoLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill: grey; -fx-font-size: 12;");
        return l;
    }

    private String stripHtml(String html) {
        if (html == null || html.equalsIgnoreCase("null") || html.isBlank()) return "No description.";
        String cleaned = html.replaceAll("<[^>]*>", "").replaceAll("&nbsp;", " ")
                .replaceAll("\\s+", " ").trim();
        return cleaned.length() > 100 ? cleaned.substring(0, 97) + "…" : cleaned;
    }

    private String formatDate(String iso) {
        try {
            String[] months = {"Jan","Feb","Mar","Apr","May","Jun",
                    "Jul","Aug","Sep","Oct","Nov","Dec"};
            String[] p = iso.substring(0, 10).split("-");
            String time = iso.length() > 16 ? " " + iso.substring(11, 16) : "";
            return months[Integer.parseInt(p[1]) - 1] + " " + p[2] + ", " + p[0] + time;
        } catch (Exception e) { return iso; }
    }
}