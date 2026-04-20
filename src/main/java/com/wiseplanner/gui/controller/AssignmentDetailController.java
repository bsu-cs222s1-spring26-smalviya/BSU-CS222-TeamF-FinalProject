package com.wiseplanner.gui.controller;

import com.wiseplanner.model.Assignment;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class AssignmentDetailController {

    @FXML private WebView descriptionWebView;
    @FXML private Label   dueAtLabel;
    @FXML private Label   nameLabel;
    @FXML private Label   workflowLabel;

    public void setContent(Assignment assignment) {
        nameLabel.setText(assignment.getName());


        String due = assignment.getDue_at();
        dueAtLabel.setText((due == null || due.equalsIgnoreCase("null")) ? "No due date" : due);


        String state = (assignment.getSubmission() != null
                && assignment.getSubmission().getWorkflow_state() != null)
                ? assignment.getSubmission().getWorkflow_state()
                : "unsubmitted";
        workflowLabel.setText(state);


        String desc = assignment.getDescription();
        WebEngine webEngine = descriptionWebView.getEngine();
        webEngine.loadContent(desc != null && !desc.equalsIgnoreCase("null")
                ? desc : "<p>No description available.</p>");
    }
}