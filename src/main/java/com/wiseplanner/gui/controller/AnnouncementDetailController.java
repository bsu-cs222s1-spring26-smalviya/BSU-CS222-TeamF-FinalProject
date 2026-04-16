package com.wiseplanner.gui.controller;

import com.wiseplanner.model.Announcement;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class AnnouncementDetailController {

    @FXML
    private WebView messageWebView;

    @FXML
    private Label postedAtLabel;

    @FXML
    private Label titleLabel;

    public void setContent(Announcement announcement) {
        WebEngine webEngine = messageWebView.getEngine();
        webEngine.loadContent(announcement.getMessage());
        titleLabel.setText(announcement.getTitle());
        postedAtLabel.setText(announcement.getPosted_at());
    }
}
