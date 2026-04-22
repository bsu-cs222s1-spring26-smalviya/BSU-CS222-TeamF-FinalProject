package com.wiseplanner.gui.controller;

import com.wiseplanner.core.WisePlannerKernel;
import com.wiseplanner.model.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class DashboardController extends BaseController {

    @FXML private Label greetingLabel;
    @FXML private Label gradeAnalysisLabel;
    @FXML private Label insightsLabel;
    @FXML private Label statusLabel;
    @FXML private VBox  assignmentsBox;
    @FXML private VBox  announcementsBox;
    @FXML private VBox  todoBox;
    @FXML private VBox  gradeChartBox;
    @FXML private VBox  scheduleBox;

    private MainWindowController mainWindowController;

    public void setMainWindowController(MainWindowController mwc) { this.mainWindowController = mwc; }

    @Override
    public void setKernel(WisePlannerKernel kernel) {
        super.setKernel(kernel);
        loadDashboard();
    }

    @FXML void onCoursesNavButtonClick(ActionEvent e) throws IOException {
        if (mainWindowController != null) mainWindowController.showCoursesPage();
    }
    @FXML void onTasksNavButtonClick(ActionEvent e) throws IOException {
        if (mainWindowController != null) mainWindowController.showTasksPage();
    }
    @FXML void onSchedulesNavButtonClick(ActionEvent e) throws IOException {
        if (mainWindowController != null) mainWindowController.showSchedulesPage();
    }
    @FXML void onRefreshButtonClick(ActionEvent e) { loadDashboard(); }

    @FXML
    void onAddTaskClick(ActionEvent e) throws IOException {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(
                getClass().getClassLoader().getResource("fxml/TaskDetail.fxml")));
        Parent root = loader.load();
        TaskDetailController ctrl = loader.getController();
        ctrl.setKernel(kernel);
        ctrl.setOnTaskCreated(task -> Platform.runLater(this::loadDashboard));
        ctrl.setupForAdd();
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Add Task");
        stage.setScene(new Scene(root));
        stage.showAndWait();
    }

    private void loadDashboard() {
        clearAll();
        statusLabel.setText("Loading dashboard… this may take a moment.");
        new Thread(() -> {
            try {
                Dashboard dashboard = kernel.dashboard().getDashboard();
                Platform.runLater(() -> { statusLabel.setText(""); populate(dashboard); });
            } catch (Exception ex) {
                Platform.runLater(() ->
                        statusLabel.setText("[Error] Failed to load dashboard: " + ex.getMessage()));
            }
        }).start();
    }

    private void populate(Dashboard dashboard) {
        String greeting = dashboard.getGreeting();
        if (greeting != null) {
            String[] parts = greeting.split("\n\n", 2);
            greetingLabel.setText(parts[0]);
            if (parts.length > 1) {
                Label quoteLabel = new Label(parts[1]);
                quoteLabel.setWrapText(true);
                quoteLabel.setStyle("-fx-text-fill: #E66000; -fx-font-size: 12px; -fx-font-style: italic;");
                ((VBox) greetingLabel.getParent()).getChildren().add(quoteLabel);
            }
        }

        setAiLabel(insightsLabel, dashboard.getGeminiInsights(),
                "Set a Gemini API key in Settings to enable AI insights.");
        setAiLabel(gradeAnalysisLabel, dashboard.getGeminiGradeAnalysis(), "");

        populateGradeChart(dashboard.getAllCourses());
        populateAssignments(dashboard.getTodaysAssignments());
        populateAnnouncements(dashboard.getTodaysAnnouncements());
        populateTodo(dashboard.getTodoList(), dashboard.getAllCourses());
        populateSchedule();
    }

    private void populateGradeChart(List<Course> courses) {
        gradeChartBox.getChildren().clear();
        if (courses == null || courses.isEmpty()) {
            gradeChartBox.getChildren().add(makeInfoLabel("No course data available.")); return;
        }
        for (Course course : courses) {
            List<Assignment> assignments = course.getAssignments();
            if (assignments == null || assignments.isEmpty()) continue;

            long submitted = assignments.stream().filter(a ->
                    a.getSubmission() != null
                            && a.getSubmission().getWorkflow_state() != null
                            && !"unsubmitted".equals(a.getSubmission().getWorkflow_state())).count();
            long late    = assignments.stream().filter(a ->
                    a.getSubmission() != null && Boolean.TRUE.equals(a.getSubmission().getLate())).count();
            long missing = assignments.stream().filter(a ->
                    a.getSubmission() != null && Boolean.TRUE.equals(a.getSubmission().getMissing())).count();
            double pct = (submitted * 100.0) / assignments.size();

            Label nameLabel = new Label(course.getCourse_code() != null
                    ? course.getCourse_code() : course.getName());
            nameLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #2D3B45; -fx-font-weight: bold;");
            nameLabel.setMinWidth(120);

            HBox bar = new HBox();
            bar.setMinHeight(14); bar.setPrefHeight(14);
            bar.setStyle("-fx-background-color: #E5E7EB; -fx-background-radius: 7;");
            bar.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(bar, Priority.ALWAYS);

            if (submitted > 0) {
                Region fill = new Region();
                fill.setPrefHeight(14); fill.setMinHeight(14);
                fill.setStyle("-fx-background-color: #27ae60; -fx-background-radius: 7;");
                fill.prefWidthProperty().bind(bar.widthProperty().multiply(pct / 100.0));
                bar.getChildren().add(fill);
            }

            Label pctLabel = new Label(String.format("%.0f%%  (%d/%d submitted%s%s)",
                    pct, submitted, assignments.size(),
                    late > 0 ? ", " + late + " late" : "",
                    missing > 0 ? ", " + missing + " missing" : ""));
            pctLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #6B7280;");

            HBox row = new HBox(8, nameLabel, bar);
            row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            HBox.setHgrow(bar, Priority.ALWAYS);
            gradeChartBox.getChildren().addAll(row, pctLabel);
        }
    }

    private void populateAssignments(List<Assignment> list) {
        assignmentsBox.getChildren().clear();
        if (list == null || list.isEmpty()) {
            assignmentsBox.getChildren().add(makeInfoLabel("No assignments due today. Enjoy your day!"));
            return;
        }
        for (Assignment a : list) assignmentsBox.getChildren().add(makeAssignmentCard(a));
    }

    private VBox makeAssignmentCard(Assignment a) {
        String state    = (a.getSubmission() != null && a.getSubmission().getWorkflow_state() != null)
                ? a.getSubmission().getWorkflow_state() : "unsubmitted";
        boolean late    = a.getSubmission() != null && Boolean.TRUE.equals(a.getSubmission().getLate());
        boolean missing = a.getSubmission() != null && Boolean.TRUE.equals(a.getSubmission().getMissing());

        String badgeColor = "unsubmitted".equals(state) ? "#e67e22" : "#27ae60";
        if (late)    badgeColor = "#c0392b";
        if (missing) badgeColor = "#8e44ad";

        Label title = new Label(a.getName());
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: #2D3B45;");
        title.setWrapText(true);

        String dueText = (a.getDue_at() == null || "null".equalsIgnoreCase(a.getDue_at()))
                ? "No due date" : "Due: " + formatDate(a.getDue_at());
        Label dueLabel = new Label(dueText);
        dueLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #777;");

        final String bc = badgeColor;
        Label stateBadge = new Label(state + (late ? " · LATE" : "") + (missing ? " · MISSING" : ""));
        stateBadge.setStyle("-fx-font-size: 10px; -fx-text-fill: white; -fx-background-color: "
                + bc + "; -fx-background-radius: 8; -fx-padding: 2 7 2 7;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        VBox card = new VBox(4, title, new HBox(6, dueLabel, spacer, stateBadge));
        card.setStyle("-fx-padding: 8 10 8 10; -fx-background-color: #fafafa;"
                + "-fx-border-color: " + bc + "; -fx-border-radius: 6;"
                + "-fx-background-radius: 6; -fx-border-width: 0 0 0 3; -fx-cursor: hand;");

        card.setOnMouseClicked(ev -> {
            try {
                FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(
                        getClass().getClassLoader().getResource("fxml/AssignmentDetail.fxml")));
                Parent root = loader.load();
                AssignmentDetailController ctrl = loader.getController();
                ctrl.setContent(a);
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setTitle(a.getName());
                stage.setScene(new Scene(root));
                stage.showAndWait();
            } catch (IOException ex) { throw new RuntimeException(ex); }
        });
        return card;
    }

    private void populateAnnouncements(List<Announcement> list) {
        announcementsBox.getChildren().clear();
        if (list == null || list.isEmpty()) {
            announcementsBox.getChildren().add(makeInfoLabel("No recent announcements.")); return;
        }
        for (Announcement a : list) announcementsBox.getChildren().add(makeAnnouncementCard(a));
    }

    private VBox makeAnnouncementCard(Announcement a) {
        Label title = new Label(a.getTitle());
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #2D3B45;");
        title.setWrapText(true);

        String msgRaw = a.getMessage() != null
                ? a.getMessage().replaceAll("<[^>]*>", "").replaceAll("&nbsp;", " ").trim() : "";
        if (msgRaw.length() > 90) msgRaw = msgRaw.substring(0, 87) + "…";
        Label msg = new Label(msgRaw);
        msg.setStyle("-fx-font-size: 11px; -fx-text-fill: #555;");
        msg.setWrapText(true);

        Label date = new Label(a.getPosted_at() != null ? formatDate(a.getPosted_at()) : "");
        date.setStyle("-fx-font-size: 10px; -fx-text-fill: #aaa;");

        VBox card = new VBox(3, title, msg, date);
        card.setStyle("-fx-padding: 8 10 8 10; -fx-background-color: #fafafa;"
                + "-fx-border-color: #0770A3; -fx-border-radius: 6;"
                + "-fx-background-radius: 6; -fx-border-width: 0 0 0 3; -fx-cursor: hand;");

        card.setOnMouseClicked(ev -> {
            try {
                FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(
                        getClass().getClassLoader().getResource("fxml/AnnouncementDetail.fxml")));
                Parent root = loader.load();
                AnnouncementDetailController ctrl = loader.getController();
                ctrl.setContent(a);
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setTitle(a.getTitle());
                stage.setScene(new Scene(root));
                stage.showAndWait();
            } catch (IOException ex) { throw new RuntimeException(ex); }
        });
        return card;
    }

    private void populateTodo(List<Task> tasks, List<Course> courses) {
        todoBox.getChildren().clear();
        if (tasks == null || tasks.isEmpty()) {
            todoBox.getChildren().add(makeInfoLabel("No tasks yet. Tap + Add to create one.")); return;
        }
        Map<String, String> courseNames = new LinkedHashMap<>();
        if (courses != null) {
            for (Course c : courses)
                courseNames.put(String.valueOf(c.getId()),
                        c.getCourse_code() != null ? c.getCourse_code() : c.getName());
        }
        Map<String, List<Task>> grouped = new LinkedHashMap<>();
        for (Task t : tasks) {
            String key = (t.getCourseId() == null || t.getCourseId().isBlank()) ? "UNASSIGNED" : t.getCourseId();
            grouped.computeIfAbsent(key, k -> new ArrayList<>()).add(t);
        }
        for (Map.Entry<String, List<Task>> entry : grouped.entrySet()) {
            String groupKey  = entry.getKey();
            String groupName = "UNASSIGNED".equals(groupKey) ? "📌 Unassigned"
                    : "📚 " + courseNames.getOrDefault(groupKey, groupKey);
            Label header = new Label(groupName);
            header.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #6B7280; -fx-padding: 4 0 2 0;");
            todoBox.getChildren().add(header);
            for (Task t : entry.getValue()) todoBox.getChildren().add(makeTaskRow(t));
        }
    }

    private HBox makeTaskRow(Task t) {
        CheckBox cb = new CheckBox();
        cb.setSelected(t.isDone());
        Label titleLabel = new Label(t.getTitle());
        titleLabel.setWrapText(true);
        titleLabel.setStyle(t.isDone()
                ? "-fx-font-size: 12px; -fx-text-fill: #aaa; -fx-strikethrough: true;"
                : "-fx-font-size: 12px; -fx-text-fill: #2D3B45;");
        Label deadline = new Label(t.getDeadline() != null ? "  " + t.getDeadline() : "");
        deadline.setStyle("-fx-font-size: 10px; -fx-text-fill: #aaa;");
        cb.setOnAction(ev -> {
            t.setDone(cb.isSelected());
            titleLabel.setStyle(cb.isSelected()
                    ? "-fx-font-size: 12px; -fx-text-fill: #aaa; -fx-strikethrough: true;"
                    : "-fx-font-size: 12px; -fx-text-fill: #2D3B45;");
            try { kernel.task().saveTask(); } catch (Exception ignored) {}
        });
        VBox textCol = new VBox(1, titleLabel, deadline);
        HBox.setHgrow(textCol, Priority.ALWAYS);
        HBox row = new HBox(8, cb, textCol);
        row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        return row;
    }

    private void populateSchedule() {
        scheduleBox.getChildren().clear();
        try {
            DayOfWeek today = LocalDate.now().getDayOfWeek();
            List<com.wiseplanner.model.Schedule> todayS = kernel.schedule().getScheduleList().stream()
                    .filter(s -> s.getDayOfWeeks() != null && s.getDayOfWeeks().contains(today))
                    .sorted(Comparator.comparing(com.wiseplanner.model.Schedule::getStartTime))
                    .collect(Collectors.toList());
            if (todayS.isEmpty()) {
                scheduleBox.getChildren().add(makeInfoLabel("No classes today.")); return;
            }
            for (com.wiseplanner.model.Schedule s : todayS) scheduleBox.getChildren().add(makeScheduleCard(s));
        } catch (Exception e) {
            scheduleBox.getChildren().add(makeInfoLabel("Could not load schedules."));
        }
    }

    private VBox makeScheduleCard(com.wiseplanner.model.Schedule s) {
        Label name = new Label(s.getName());
        name.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #2D3B45;");
        Label time = new Label("🕐 " + s.toStringStartTime() + " – " + s.toStringEndTime());
        time.setStyle("-fx-font-size: 11px; -fx-text-fill: #555;");
        VBox card = new VBox(2, name, time);
        if (s.getLocation() != null && !s.getLocation().isBlank()) {
            Label l = new Label("📍 " + s.getLocation());
            l.setStyle("-fx-font-size: 11px; -fx-text-fill: #555;"); card.getChildren().add(l);
        }
        if (s.getProfessor() != null && !s.getProfessor().isBlank()) {
            Label p = new Label("👤 " + s.getProfessor());
            p.setStyle("-fx-font-size: 11px; -fx-text-fill: #555;"); card.getChildren().add(p);
        }
        card.setStyle("-fx-padding: 8 10 8 10; -fx-background-color: #fafafa;"
                + "-fx-border-color: #E66000; -fx-border-radius: 6;"
                + "-fx-background-radius: 6; -fx-border-width: 0 0 0 3;");
        return card;
    }

    private void setAiLabel(Label label, String content, String fallback) {
        label.setWrapText(true);
        if (content == null || content.isBlank()) {
            label.setText(fallback); label.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 12px;");
        } else if (content.startsWith("[Error]")) {
            label.setText(content.substring(7).trim()); label.setStyle("-fx-text-fill: #c0392b; -fx-font-size: 12px;");
        } else if (content.startsWith("[Info]")) {
            label.setText(content.substring(6).trim()); label.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 12px;");
        } else {
            label.setText(content); label.setStyle("-fx-text-fill: #2D3B45; -fx-font-size: 13px;");
        }
    }

    private Label makeInfoLabel(String text) {
        Label l = new Label(text);
        l.setWrapText(true);
        l.setStyle("-fx-font-size: 12px; -fx-text-fill: #6B7280;");
        return l;
    }

    private String formatDate(String iso) {
        try {
            String[] months = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
            String[] p = iso.substring(0, 10).split("-");
            String time = iso.length() > 16 ? " " + iso.substring(11, 16) : "";
            return months[Integer.parseInt(p[1]) - 1] + " " + p[2] + ", " + p[0] + time;
        } catch (Exception e) { return iso; }
    }

    private void clearAll() {
        greetingLabel.setText("");
        gradeAnalysisLabel.setText("");
        insightsLabel.setText("");
        statusLabel.setText("");
        gradeChartBox.getChildren().clear();
        assignmentsBox.getChildren().clear();
        announcementsBox.getChildren().clear();
        todoBox.getChildren().clear();
        scheduleBox.getChildren().clear();
        if (greetingLabel.getParent() instanceof VBox vbox)
            vbox.getChildren().removeIf(n -> n != greetingLabel
                    && n instanceof Label l && l.getStyle().contains("italic"));
    }
}