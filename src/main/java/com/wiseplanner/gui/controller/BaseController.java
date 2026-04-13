package com.wiseplanner.gui.controller;

import com.wiseplanner.core.WisePlannerKernel;
import javafx.scene.Parent;
import javafx.scene.layout.Region;

public abstract class BaseController {
    protected WisePlannerKernel kernel;

    public void setKernel(WisePlannerKernel kernel) {
        this.kernel = kernel;
    }

    public WisePlannerKernel getKernel() {
        return kernel;
    }

    // Allow Component Resizing to Maximum Dimensions
    protected Parent stretchToFill(Parent node) {
        if (node instanceof Region region) {
            region.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        }
        return node;
    }
}
