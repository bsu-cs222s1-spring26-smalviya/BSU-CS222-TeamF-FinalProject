package com.wiseplanner.gui.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private TextField canvasTokenField;

    @FXML
    private Label canvasTokenLabel;

    @FXML
    private Button loginButton;

    @FXML
    private TextField nameField;

    @FXML
    private Label nameLabel;

    @FXML
    private VBox vBox;

    @FXML
    void onLoginButtonClick(ActionEvent event) {
        name = nameField.getText();
        canvasToken = canvasTokenField.getText();
        if (name.isBlank() || canvasToken.isBlank()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Invalid input. Please check your input.", ButtonType.OK);
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.showAndWait();
        } else {
            isLoginSuccessful = true;
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.close();
        }
    }

    private boolean isLoginSuccessful = false;
    private String name;
    private String canvasToken;

    public boolean isLoginSuccessful() {
        return isLoginSuccessful;
    }

    public String getName() {
        return name;
    }

    public String getCanvasToken() {
        return canvasToken;
    }
}
