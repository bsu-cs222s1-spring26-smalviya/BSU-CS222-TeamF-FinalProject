package com.wiseplanner.gui;

import com.wiseplanner.core.WisePlannerKernel;
import com.wiseplanner.gui.controller.LoginController;
import com.wiseplanner.gui.controller.MainWindowController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.*;
import java.util.Objects;

public class App extends Application {
    WisePlannerKernel wisePlannerKernel = new WisePlannerKernel();

    private void showLogin() throws IOException {
        Stage loginStage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/Login.fxml"));
        Parent root = loader.load();
        LoginController controller = loader.getController();
        loginStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/launcher.png")));
        loginStage.setTitle("Login");
        loginStage.setScene(new Scene(root));
        loginStage.setMinWidth(300);
        loginStage.setMinHeight(220);
        loginStage.showAndWait();
        if (controller.isLoginSuccessful()) {
            String name = controller.getName();
            String canvasToken = controller.getCanvasToken();
            wisePlannerKernel.user().setUser(name, canvasToken);
        }
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getClassLoader().getResource("fxml/MainWindow.fxml")));
        Parent root = loader.load();
        MainWindowController controller = loader.getController();
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/launcher.png")));
        primaryStage.setTitle("Wise Planner");
        primaryStage.setScene(new Scene(root, 1366, 768));
        primaryStage.setMinWidth(960);
        primaryStage.setMinHeight(600);
        // Check if login was successful
        if (!wisePlannerKernel.user().isLogin()) {
            showLogin();
        }
        controller.setKernel(wisePlannerKernel);
        primaryStage.show();
    }
}
