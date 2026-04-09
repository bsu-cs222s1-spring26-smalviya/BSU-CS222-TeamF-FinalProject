package com.wiseplanner.util;

import com.wiseplanner.util.parser.SubmissionParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Objects;

public class SubmissionParserTest {
    SubmissionParser submissionParser;
    public SubmissionParserTest() throws IOException {
        try (InputStream jsonFile = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("submission.json")) {
            if (jsonFile == null) {
                throw new IllegalStateException("[Error] course.json not find! ");
            }
            String jsonData = new String(Objects.requireNonNull(jsonFile).readAllBytes(), Charset.defaultCharset());
            submissionParser = new SubmissionParser(jsonData);
        }
    }

    @Test
    public void parseWorkflow_state(){
        Assertions.assertEquals("submitted",submissionParser.getSubmission().getWorkflow_state());
    }

    @Test
    public void parseLate(){
        Assertions.assertFalse(submissionParser.getSubmission().getLate());
    }

    @Test
    public void parseMissing(){
        Assertions.assertTrue(submissionParser.getSubmission().getMissing());
    }
}
