package edu.bsu.cs;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TaskTest {
    private Task task = new Task("Finish Project", "Complete User Story 4", "Software Engineering");

    @Test
    public void testGetNote() {
        Assertions.assertEquals("Complete User Story 4", task.getNote());
    }

    @Test
    public void testGetCourseName() {
        Assertions.assertEquals("Software Engineering", task.getCourseName());
    }

    @Test
    public void testGetTimestampIsGenerated() {
        Assertions.assertNotNull(task.getTimestamp());
        Assertions.assertFalse(task.getTimestamp().isEmpty());
    }
}