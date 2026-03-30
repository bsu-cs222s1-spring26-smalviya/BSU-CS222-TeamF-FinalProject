package com.wiseplanner.gui.controller;

import com.wiseplanner.core.WisePlannerKernel;

public abstract class BaseController {
    protected WisePlannerKernel kernel;

    public void setKernel(WisePlannerKernel kernel){
        this.kernel = kernel;
    }

    public WisePlannerKernel getKernel() {
        return kernel;
    }
}
