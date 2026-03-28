package com.wiseplanner.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SubmissionTest {
    Submission submission = new Submission("submitted", true, true);

    @Test
    public void setWorkflow_state() {
        submission.setWorkflow_state("unsubmitted");
        Assertions.assertEquals("unsubmitted", submission.getWorkflow_state());
    }

    @Test
    public void getWorkflow_state() {
        Assertions.assertEquals("submitted", submission.getWorkflow_state());
    }

    @Test
    public void setLate() {
        submission.setLate(false);
        Assertions.assertFalse(submission.getLate());
    }

    @Test
    public void getLate() {
        Assertions.assertTrue(submission.getLate());
    }

    @Test
    public void setMissing() {
        submission.setMissing(false);
        Assertions.assertFalse(submission.getMissing());
    }

    @Test
    public void getMissing() {
        Assertions.assertTrue(submission.getMissing());
    }
}
