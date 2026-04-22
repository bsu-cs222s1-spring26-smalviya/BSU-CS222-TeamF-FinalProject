package com.wiseplanner.gui.controller;

import com.wiseplanner.exception.DeleteException;
import com.wiseplanner.model.Schedule;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class SchedulesController extends BaseController {

    @FXML private Button addButton;
    @FXML private VBox   scheduleListBox;

    private static final String[] DAY_COLORS = {
            "#4A90D9", "#E66000", "#27ae60", "#9B59B6", "#E74C3C", "#1ABC9C", "#F39C12"
    };

    @FXML
    void onAddButtonClick(ActionEvent e) throws IOException {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(
                getClass().getClassLoader().getResource("fxml/ScheduleDetail.fxml")));
        Parent root = loader.load();
        ScheduleDetailController ctrl = loader.getController();
        ctrl.setKernel(kernel);
        ctrl.setMode(ScheduleDetailController.ViewMode.ADD);
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(root));
        stage.showAndWait();
        loadSchedules();
    }

    public void loadSchedules() {
        try { kernel.schedule().loadSchedule(); } catch (Exception ignored) {}
        renderList();
    }

    private void renderList() {
        scheduleListBox.getChildren().clear();
        List<Schedule> all = kernel.schedule().getScheduleList();
        if (all == null || all.isEmpty()) {
            Label l = new Label("No schedules yet. Click + Add Schedule to create one.");
            l.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 13px;");
            scheduleListBox.getChildren().add(l); return;
        }

        Map<DayOfWeek, List<Schedule>> byDay = new LinkedHashMap<>();
        for (DayOfWeek d : DayOfWeek.values()) byDay.put(d, new ArrayList<>());
        for (Schedule s : all) {
            if (s.getDayOfWeeks() == null) continue;
            for (DayOfWeek d : s.getDayOfWeeks()) byDay.get(d).add(s);
        }

        DayOfWeek today = LocalDate.now().getDayOfWeek();
        for (Map.Entry<DayOfWeek, List<Schedule>> entry : byDay.entrySet()) {
            if (entry.getValue().isEmpty()) continue;
            DayOfWeek day = entry.getKey();
            boolean isToday = day == today;
            Label dayHeader = new Label(dayName(day) + (isToday ? "  ← Today" : ""));
            dayHeader.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; "
                    + "-fx-text-fill: " + (isToday ? "#E66000" : "#2D3B45") + "; -fx-padding: 10 0 4 0;");
            scheduleListBox.getChildren().add(dayHeader);
            entry.getValue().stream()
                    .sorted(Comparator.comparing(Schedule::getStartTime))
                    .forEach(s -> scheduleListBox.getChildren().add(makeScheduleCard(s, day)));
        }
    }

    private HBox makeScheduleCard(Schedule s, DayOfWeek day) {
        String accent = DAY_COLORS[day.getValue() - 1];
        Label name = new Label(s.getName());
        name.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #2D3B45;");
        Label time = new Label("🕐 " + s.toStringStartTime() + " – " + s.toStringEndTime());
        time.setStyle("-fx-font-size: 12px; -fx-text-fill: #555;");
        VBox details = new VBox(2, name, time);
        if (s.getLocation() != null && !s.getLocation().isBlank()) {
            Label l = new Label("📍 " + s.getLocation());
            l.setStyle("-fx-font-size: 12px; -fx-text-fill: #555;"); details.getChildren().add(l);
        }
        if (s.getProfessor() != null && !s.getProfessor().isBlank()) {
            Label p = new Label("👤 " + s.getProfessor());
            p.setStyle("-fx-font-size: 12px; -fx-text-fill: #555;"); details.getChildren().add(p);
        }
        HBox.setHgrow(details, Priority.ALWAYS);
        Button editBtn = new Button("✏");
        editBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #0770A3; -fx-cursor: hand; -fx-font-size: 14px;");
        editBtn.setOnAction(e -> openScheduleDetail(s, ScheduleDetailController.ViewMode.MODIFY));
        Button delBtn = new Button("🗑");
        delBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #c0392b; -fx-cursor: hand; -fx-font-size: 14px;");
        delBtn.setOnAction(e -> {
            Alert c = new Alert(Alert.AlertType.CONFIRMATION, "Delete \"" + s.getName() + "\"?", ButtonType.OK, ButtonType.CANCEL);
            c.showAndWait().ifPresent(r -> {
                if (r == ButtonType.OK) {
                    try { kernel.schedule().deleteSchedule(s); }
                    catch (DeleteException ex) { new Alert(Alert.AlertType.ERROR, ex.getMessage(), ButtonType.OK).showAndWait(); }
                    renderList();
                }
            });
        });
        HBox card = new HBox(12, details, editBtn, delBtn);
        card.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        card.setStyle("-fx-background-color: white; -fx-border-color: " + accent + ";"
                + "-fx-border-radius: 8; -fx-background-radius: 8;"
                + "-fx-padding: 12 14 12 14; -fx-border-width: 0 0 0 4;"
                + "-fx-effect: dropshadow(gaussian,rgba(0,0,0,0.05),4,0,0,1);");
        card.setOnMouseClicked(ev -> {
            if (ev.getTarget() != editBtn && ev.getTarget() != delBtn)
                openScheduleDetail(s, ScheduleDetailController.ViewMode.VIEW);
        });
        return card;
    }

    private void openScheduleDetail(Schedule s, ScheduleDetailController.ViewMode viewMode) {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(
                    getClass().getClassLoader().getResource("fxml/ScheduleDetail.fxml")));
            Parent root = loader.load();
            ScheduleDetailController ctrl = loader.getController();
            ctrl.setKernel(kernel); ctrl.setMode(viewMode); ctrl.setSchedule(s);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();
            renderList();
        } catch (IOException e) { throw new RuntimeException(e); }
    }

    private String dayName(DayOfWeek d) {
        return switch (d) {
            case MONDAY -> "Monday"; case TUESDAY -> "Tuesday"; case WEDNESDAY -> "Wednesday";
            case THURSDAY -> "Thursday"; case FRIDAY -> "Friday";
            case SATURDAY -> "Saturday"; case SUNDAY -> "Sunday";
        };
    }
}