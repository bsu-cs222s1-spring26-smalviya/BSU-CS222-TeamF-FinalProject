package com.wiseplanner.gui.controller;

import com.wiseplanner.core.WisePlannerKernel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class SettingsController extends BaseController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField tokenField;

    @FXML
    private Label statusLabel;

    @Override
    public void setKernel(WisePlannerKernel kernel) {
        super.setKernel(kernel);
        nameField.setText(kernel.user().getUser().getName());
        tokenField.setText(kernel.user().getUser().getCanvasToken());
    }

    @FXML
    void onSaveButtonClick(ActionEvent event) {
        String newName = nameField.getText().trim();
        String newToken = tokenField.getText().trim();

        if (newName.isEmpty() || newToken.isEmpty()) {
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText("Name and token cannot be empty.");
            return;
        }

        kernel.user().setUser(newName, newToken);
        kernel.initialize();

        statusLabel.setStyle("-fx-text-fill: green;");
        statusLabel.setText("Saved successfully!");
    }
}