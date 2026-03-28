package com.wiseplanner.service;

import com.google.gson.Gson;
import com.wiseplanner.model.Submission;

public class SubmissionParser {
    private Submission submission;

    public SubmissionParser(String jsonData) {
        Gson gson = new Gson();
        submission = gson.fromJson(jsonData, Submission.class);
    }

    public Submission getSubmission() {
        return submission;
    }
}
