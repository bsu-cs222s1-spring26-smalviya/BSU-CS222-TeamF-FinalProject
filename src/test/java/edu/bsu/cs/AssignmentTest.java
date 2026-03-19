package edu.bsu.cs;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AssignmentTest {
    Assignment assignment = new Assignment(
            "1",
            "Read Chapter 5",
            "Read and summarize chapter 5",
            "2026-04-01T23:59:00Z"
    );

    @Test
    public void getId() {
        Assertions.assertEquals("1", assignment.getId());
    }

    @Test
    public void getName() {
        Assertions.assertEquals("Read Chapter 5", assignment.getName());
    }

    @Test
    public void getDescription() {
        Assertions.assertEquals("Read and summarize chapter 5", assignment.getDescription());
    }

    @Test
    public void getDueAt() {
        Assertions.assertEquals("2026-04-01T23:59:00Z", assignment.getDueAt());
    }
}