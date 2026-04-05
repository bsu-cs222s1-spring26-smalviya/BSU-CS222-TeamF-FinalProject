package com.wiseplanner.gui.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class AddTaskController {

    @FXML
    private Button cancelButton;

    @FXML
    private Label contentLabel;

    @FXML
    private TextArea contentTextArea;

    @FXML
    private DatePicker deadlineDate;

    @FXML
    private Spinner<?> deadlineHour;

    @FXML
    private Label deadlineLabel;

    @FXML
    private Spinner<?> deadlineMinute;

    @FXML
    private Button okButton;

    @FXML
    private Label titleLabel;

    @FXML
    private TextField titleTextField;

    @FXML
    void onCancelButtonClick(ActionEvent event) {

    }

    @FXML
    void onOKButtonClick(ActionEvent event) {

    }

}
