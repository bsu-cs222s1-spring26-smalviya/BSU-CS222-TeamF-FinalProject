package edu.bsu.cs;

public class User {
    private String name;
    private String canvasToken;

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
}
