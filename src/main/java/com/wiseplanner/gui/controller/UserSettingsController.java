package com.wiseplanner.gui.controller;

import com.wiseplanner.core.WisePlannerKernel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;

public class UserSettingsController extends BaseController {

    @FXML private TextField nameField;
    @FXML private TextField tokenField;
    @FXML private TextField geminiKeyField;
    @FXML private Label statusLabel;

    @Override
    public void setKernel(WisePlannerKernel kernel) {
        super.setKernel(kernel);
        nameField.setText(kernel.user().getUser().getName());
        tokenField.setText(kernel.user().getUser().getCanvasToken());

        String savedKey = kernel.user().getGeminiApiKey();
        if (savedKey != null && !savedKey.isBlank()) {
            geminiKeyField.setText(savedKey);
        }
    }

    @FXML
    void onSaveButtonClick(ActionEvent event) {
        String newName      = nameField.getText().trim();
        String newToken     = tokenField.getText().trim().replaceAll("\\s+", "");
        String newGeminiKey = geminiKeyField.getText().trim().replaceAll("\\s+", "");

        if (newName.isEmpty() || newToken.isEmpty()) {
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText("Name and token cannot be empty.");
            return;
        }
        try {
            kernel.user().setUser(newName, newToken);

            if (!newGeminiKey.isEmpty()) {
                kernel.user().setGeminiApiKey(newGeminiKey);
            }

            kernel.initialize();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.showAndWait();
        }
        statusLabel.setStyle("-fx-text-fill: green;");
        statusLabel.setText("Saved successfully!");
    }
}