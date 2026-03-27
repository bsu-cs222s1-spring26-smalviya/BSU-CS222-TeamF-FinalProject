package com.wiseplanner.gui;

import com.google.gson.Gson;
import com.wiseplanner.model.User;
import com.wiseplanner.service.WisePlannerKernel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class App extends Application {
    WisePlannerKernel wisePlannerKernel;
    public static final String DATA_FOLDER = ".wiseplanner";
    public static final String USER_DATA_FILE = "user.json";
    User user = null;
    Gson gson = new Gson();

    public App() {
    }

    private void showLogin() throws IOException {
        Stage loginStage = new Stage();
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("fxml/Login.fxml")));
        loginStage.setTitle("Wise Planner");
        loginStage.setScene(new Scene(root));
        loginStage.show();
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("fxml/MainWindow.fxml")));
        primaryStage.setTitle("Wise Planner");
        primaryStage.setScene(new Scene(root));


        String userPath = System.getProperty("user.home");
        Path userDataPath = Paths.get(userPath, DATA_FOLDER, USER_DATA_FILE);
        File userDataFile = userDataPath.toFile();

        if (userDataFile.exists()) {
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(userDataFile))) {
                user = gson.fromJson(bufferedReader, User.class);
            } catch (IOException e) {
                System.err.println("[Error] File read failed, unable to read user data.");
            }
        } else {
            showLogin();
            //user = new User(name, canvasToken);
            userDataFile.getParentFile().mkdirs();
            String userJson = gson.toJson(user);
            try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(userDataFile))) {
                bufferedWriter.write(userJson);
            } catch (IOException e) {
                System.err.println("[Error] File write failed, unable to write user data.");
            }
        }
        wisePlannerKernel = new WisePlannerKernel(user);

        primaryStage.show();
    }
}
