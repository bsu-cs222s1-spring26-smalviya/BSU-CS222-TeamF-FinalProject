package edu.bsu.cs;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Objects;

public class AssignmentParserTest {
    AssignmentParser assignmentParser;

    public AssignmentParserTest() throws IOException {
        try (InputStream jsonFile = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("assignment.json")) {
            if (jsonFile == null) {
                throw new IllegalStateException("[Error] assignment.json not found!");
            }
            String jsonData = new String(Objects.requireNonNull(jsonFile).readAllBytes(), Charset.defaultCharset());
            assignmentParser = new AssignmentParser(jsonData);
        }
    }

    @Test
    public void parseId() {
        Assertions.assertEquals("1", assignmentParser.getAssignments().get(0).getId());
        Assertions.assertEquals("2", assignmentParser.getAssignments().get(1).getId());
    }

    @Test
    public void parseName() {
        Assertions.assertEquals("Read Chapter 5", assignmentParser.getAssignments().get(0).getName());
        Assertions.assertEquals("Lab Report", assignmentParser.getAssignments().get(1).getName());
    }

    @Test
    public void parseDueAt() {
        Assertions.assertEquals("2026-04-01T23:59:00Z", assignmentParser.getAssignments().get(0).getDueAt());
        Assertions.assertEquals("2026-04-05T23:59:00Z", assignmentParser.getAssignments().get(1).getDueAt());
    }
}