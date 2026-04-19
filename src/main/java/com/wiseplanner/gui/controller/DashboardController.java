package com.wiseplanner.gui.controller;

import com.wiseplanner.core.WisePlannerKernel;
import com.wiseplanner.model.Announcement;
import com.wiseplanner.model.Assignment;
import com.wiseplanner.model.Dashboard;
import com.wiseplanner.model.Task;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class DashboardController extends BaseController {

    @FXML private Label greetingLabel;
    @FXML private Label gradeAnalysisLabel;
    @FXML private Label insightsLabel;
    @FXML private Label statusLabel;
    @FXML private VBox  assignmentsBox;
    @FXML private VBox  announcementsBox;
    @FXML private VBox  todoBox;

    @Override
    public void setKernel(WisePlannerKernel kernel) {
        super.setKernel(kernel);
        loadDashboard();
    }

    @FXML
    void onRefreshButtonClick(ActionEvent event) {
        loadDashboard();
    }

    private void loadDashboard() {
        clearAll();
        statusLabel.setText("Loading dashboard… this may take a moment.");

        new Thread(() -> {
            try {
                Dashboard dashboard = kernel.dashboard().getDashboard();
                Platform.runLater(() -> {
                    statusLabel.setText("");
                    populate(dashboard);
                });
            } catch (Exception e) {
                Platform.runLater(() ->
                        statusLabel.setText("[Error] Failed to load dashboard: " + e.getMessage())
                );
            }
        }).start();
    }

    private void populate(Dashboard dashboard) {
        greetingLabel.setText(dashboard.getGreeting() != null ? dashboard.getGreeting() : "");

        setAiLabel(gradeAnalysisLabel, dashboard.getGeminiGradeAnalysis(),
                "[Info] Set a Gemini API key in Settings → User Settings to enable AI features.");
        setAiLabel(insightsLabel, dashboard.getGeminiInsights(),
                "[Info] Set a Gemini API key in Settings → User Settings to enable AI features.");

        if (dashboard.getTodaysAssignments() == null || dashboard.getTodaysAssignments().isEmpty()) {
            assignmentsBox.getChildren().add(makeLabel("No assignments due today. Enjoy your day!"));
        } else {
            for (Assignment a : dashboard.getTodaysAssignments()) {
                String dueAt = a.getDue_at();
                String dueTime = (dueAt != null && dueAt.length() > 10)
                        ? " — due " + dueAt.substring(11, 16) : "";
                String status   = a.getSubmission() != null
                        ? a.getSubmission().getWorkflow_state() : "unsubmitted";
                boolean late    = a.getSubmission() != null && a.getSubmission().getLate();
                boolean missing = a.getSubmission() != null && a.getSubmission().getMissing();
                String text = "• " + a.getName() + dueTime + "  [" + status + "]"
                        + (late    ? "  ⚠ LATE"    : "")
                        + (missing ? "  ✗ MISSING" : "");
                assignmentsBox.getChildren().add(makeLabel(text));
            }
        }

        if (dashboard.getTodaysAnnouncements() == null || dashboard.getTodaysAnnouncements().isEmpty()) {
            announcementsBox.getChildren().add(makeLabel("No new announcements today."));
        } else {
            for (Announcement a : dashboard.getTodaysAnnouncements()) {
                if ("ERR".equals(a.getId())) {
                    announcementsBox.getChildren().add(makeErrorLabel(a.getMessage()));
                    continue;
                }
                Label title = makeLabel("• " + a.getTitle());
                title.setStyle("-fx-font-weight: bold; -fx-font-size: 13;");
                announcementsBox.getChildren().add(title);
                if (a.getMessage() != null && !a.getMessage().isBlank()) {
                    String msg = a.getMessage().replaceAll("<[^>]*>", "").trim();
                    if (msg.length() > 120) msg = msg.substring(0, 117) + "…";
                    announcementsBox.getChildren().add(makeLabel("  " + msg));
                }
            }
        }

        if (dashboard.getTodoList() == null || dashboard.getTodoList().isEmpty()) {
            todoBox.getChildren().add(makeLabel("Your to-do list is empty. Add tasks from the Tasks menu."));
        } else {
            int i = 1;
            for (Task t : dashboard.getTodoList()) {
                String text = i++ + ".  " + t.getTitle()
                        + (t.getDeadline() != null ? "  (deadline: " + t.getDeadline() + ")" : "");
                todoBox.getChildren().add(makeLabel(text));
            }
        }
    }

    private void setAiLabel(Label label, String content, String fallback) {
        label.setWrapText(true);
        if (content == null || content.isBlank()) {
            label.setText(fallback);
            label.setStyle("-fx-text-fill: grey; -fx-font-size: 12;");
        } else if (content.startsWith("[Error]")) {
            label.setText(content.substring(7).trim());
            label.setStyle("-fx-text-fill: #c0392b; -fx-font-size: 12;");
        } else if (content.startsWith("[Info]")) {
            label.setText(content.substring(6).trim());
            label.setStyle("-fx-text-fill: grey; -fx-font-size: 12;");
        } else {
            label.setText(content);
            label.setStyle("-fx-text-fill: black; -fx-font-size: 13;");
        }
    }

    private Label makeLabel(String text) {
        Label l = new Label(text);
        l.setWrapText(true);
        l.setStyle("-fx-font-size: 13;");
        return l;
    }

    private Label makeErrorLabel(String text) {
        Label l = new Label(text);
        l.setWrapText(true);
        l.setStyle("-fx-font-size: 12; -fx-text-fill: #c0392b;");
        return l;
    }

    private void clearAll() {
        greetingLabel.setText("");
        gradeAnalysisLabel.setText("");
        insightsLabel.setText("");
        statusLabel.setText("");
        assignmentsBox.getChildren().clear();
        announcementsBox.getChildren().clear();
        todoBox.getChildren().clear();
    }
}