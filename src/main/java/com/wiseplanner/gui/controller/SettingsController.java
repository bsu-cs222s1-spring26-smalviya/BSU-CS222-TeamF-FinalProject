package com.wiseplanner.gui.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;
import java.util.Objects;

public class SettingsController extends BaseController {

    private MainWindowController mainWindowController;

    public void setMainWindowController(MainWindowController mainWindowController) {
        this.mainWindowController = mainWindowController;
    }

    @FXML
    void onUserSettingsButtonClick(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                Objects.requireNonNull(getClass().getClassLoader().getResource("fxml/UserSettings.fxml"))
        );
        Parent node = loader.load();
        UserSettingsController controller = loader.getController();
        controller.setKernel(kernel);
        mainWindowController.changePage(node);
    }

    @FXML
    void onAboutButtonClick(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                Objects.requireNonNull(getClass().getClassLoader().getResource("fxml/About.fxml"))
        );
        Parent node = loader.load();
        mainWindowController.changePage(node);
    }
}
