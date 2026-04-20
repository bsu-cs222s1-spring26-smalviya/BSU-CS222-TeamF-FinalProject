package com.wiseplanner.service;

import com.google.gson.Gson;
import com.wiseplanner.exception.FileCorruptionException;
import com.wiseplanner.exception.FileReadException;
import com.wiseplanner.exception.FileWriteException;
import com.wiseplanner.model.User;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;

public class UserManager {
    private static final String DATA_FOLDER = ".wiseplanner";
    private static final String USER_DATA_FILE = "user.json";
    Gson gson = new Gson();
    private User user;
    private Path userDataPath;
    private String userPath;

    public UserManager() {
        userPath = System.getProperty("user.home");
        userDataPath = Paths.get(userPath, DATA_FOLDER, USER_DATA_FILE);
    }

    public boolean isLogin() throws FileReadException, FileCorruptionException {
        File userDataFile = userDataPath.toFile();
        if (userDataFile.exists()) {
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(userDataFile))) {
                user = gson.fromJson(bufferedReader, User.class);
                if (user == null || user.getName().isEmpty() || user.getCanvasToken().isEmpty()) {
                    throw (new FileCorruptionException("File Corrupted, unable to initialize user data."));
                }
                return true;
            } catch (IOException e) {
                throw (new FileReadException("File read failed, unable to read user data."));
            }
        } else {
            return false;
        }
    }

    public void setUser(String name, String canvasToken) throws FileWriteException {
        String existingGeminiKey = (user != null) ? user.getGeminiApiKey() : null;
        user = new User(name, canvasToken);
        user.setGeminiApiKey(existingGeminiKey);
        saveUser();
    }

    public void setGeminiApiKey(String geminiApiKey) throws FileWriteException {
        user.setGeminiApiKey(geminiApiKey);
        saveUser();
    }

    public String getGeminiApiKey() {
        return user != null ? user.getGeminiApiKey() : null;
    }

    private void saveUser() throws FileWriteException {
        File userDataFile = userDataPath.toFile();
        userDataFile.getParentFile().mkdirs();
        String userJson = gson.toJson(user);
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(userDataFile))) {
            bufferedWriter.write(userJson);
        } catch (IOException e) {
            throw (new FileWriteException("File write failed, unable to write user data."));
        }
    }

    public User getUser() { return user; }

    public void logout() throws FileWriteException {
        File userDataFile = userDataPath.toFile();
        try {
            Files.deleteIfExists(userDataFile.toPath());
            user = null;
        } catch (IOException e) {
            throw new FileWriteException("File delete failed, unable to remove user data.");
        }
    }
}