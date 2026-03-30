package com.wiseplanner.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TaskTest {
    private Task task = new Task("2026-01-25T23:51:24Z", "Complete User Story 4", "Software Engineering");

    @Test
    public void testSetTimestamp() {
        task.setDeadline("2026-01-26T23:51:24Z");
        Assertions.assertEquals("2026-01-26T23:51:24Z", task.getDeadline());
    }

    @Test
    public void testGetTimestamp() {
        Assertions.assertEquals("2026-01-25T23:51:24Z", "2026-01-25T23:51:24Z");
    }

    @Test
    public void testSetTitle() {
        task.setTitle("Complete User Story 5");
        Assertions.assertEquals("Complete User Story 5", task.getTitle());
    }

    @Test
    public void testGetTitle() {
        Assertions.assertEquals("Complete User Story 4", task.getTitle());
    }

    @Test
    public void testSetContent() {
        task.setContent("Software Developing");
        Assertions.assertEquals("Software Developing", task.getContent());
    }

    @Test
    public void testGetContent() {
        Assertions.assertEquals("Software Engineering", task.getContent());
    }

}