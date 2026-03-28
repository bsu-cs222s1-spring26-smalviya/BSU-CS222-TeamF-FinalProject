package com.wiseplanner.model;

public class Announcement {
    private String id;
    private String title;
    private String message;
    private String posted_at;

    public Announcement(String id, String title, String message, String posted_at) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.posted_at = posted_at;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setPosted_at(String posted_at) {
        this.posted_at = posted_at;
    }

    public String getPosted_at() {
        return posted_at;
    }
}
