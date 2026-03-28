package com.wiseplanner.model;

public class Submission {
    private String workflow_state;
    private boolean late;
    private boolean missing;

    public Submission(String workflow_state, boolean late, boolean missing) {
        this.workflow_state = workflow_state;
        this.late = late;
        this.missing = missing;
    }

    public void setWorkflow_state(String workflow_state) {
        this.workflow_state = workflow_state;
    }

    public String getWorkflow_state() {
        return workflow_state;
    }

    public void setLate(boolean late) {
        this.late = late;
    }

    public boolean getLate() {
        return late;
    }

    public void setMissing(boolean missing) {
        this.missing = missing;

    }

    public boolean getMissing() {
        return missing;
    }
}
