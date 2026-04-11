package com.wiseplanner.util.parser;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AssignmentParserTest {
    AssignmentParser assignmentParser;

    public AssignmentParserTest() throws IOException {
        try (InputStream jsonFile = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("assignment.json")) {
            if (jsonFile == null) {
                throw new IllegalStateException("[Error] assignment.json not found!");
            }
            List<String> jsonData = new ArrayList<>();
            jsonData.add(new String(Objects.requireNonNull(jsonFile).readAllBytes(), Charset.defaultCharset()));
            assignmentParser = new AssignmentParser(jsonData);
        }
    }

    @Test
    public void parseId() {
        Assertions.assertEquals("4", assignmentParser.getAssignments().get(0).getId());
    }

    @Test
    public void parseName() {
        Assertions.assertEquals("some assignment", assignmentParser.getAssignments().get(0).getName());
    }

    @Test
    public void parseDueAt() {
        Assertions.assertEquals("2012-07-01T23:59:00-06:00", assignmentParser.getAssignments().get(0).getDue_at());
    }
}