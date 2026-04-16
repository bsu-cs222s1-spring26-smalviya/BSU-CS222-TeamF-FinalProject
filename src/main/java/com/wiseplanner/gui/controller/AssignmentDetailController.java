package com.wiseplanner.gui.controller;

import com.wiseplanner.model.Assignment;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class AssignmentDetailController {

    @FXML
    private WebView descriptionWebView;

    @FXML
    private Label dueAtLabel;

    @FXML
    private Label nameLabel;

    @FXML
    private Label workflowLabel;

    public void setContent(Assignment assignment) {
        WebEngine webEngine = descriptionWebView.getEngine();
        webEngine.loadContent(assignment.getDescription());
        nameLabel.setText(assignment.getName());
        dueAtLabel.setText(assignment.getDue_at());
        workflowLabel.setText(assignment.getSubmission().getWorkflow_state());
    }
}
