package com.wiseplanner.model;

public class User {
    private String name;
    private String canvasToken;
    private String geminiApiKey;

    public User(String name, String canvasToken) {
        this.name = name;
        this.canvasToken = canvasToken;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setCanvasToken(String canvasToken) {
        this.canvasToken = canvasToken;
    }

    public String getCanvasToken() {
        return this.canvasToken;
    }

    public String getGeminiApiKey() {return geminiApiKey; }

    public void setGeminiApiKey(String geminiApiKey) {this.geminiApiKey = geminiApiKey; }
}
