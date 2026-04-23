package com.wiseplanner.gui.controller;

import com.wiseplanner.core.WisePlannerKernel;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public abstract class BaseController {
    protected WisePlannerKernel kernel;
    private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool(runnable -> {
        Thread thread = new Thread(runnable);
        thread.setDaemon(true);
        thread.setName("fetch");
        return thread;
    });

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

    protected Label createPlaceholderLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-text-fill: #2D3B45; -fx-font-size: 13px;");
        return label;
    }

    // Fetch data in background
    public static <T> void runAsync(Callable<T> action, Consumer<T> onSuccess, Consumer<Throwable> onError) {
        EXECUTOR.submit(() -> {
            try {
                T result = action.call();
                Platform.runLater(() -> onSuccess.accept(result));
            } catch (Throwable throwable) {
                Platform.runLater(() -> onError.accept(throwable));
            }
        });
    }
}
