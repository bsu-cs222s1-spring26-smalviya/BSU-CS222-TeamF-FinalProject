package com.wiseplanner.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Objects;

public class CourseParserTest {
    CourseParser courseParser;

    public CourseParserTest() throws IOException {
        try (InputStream jsonFile = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("course.json")) {
            if (jsonFile == null) {
                throw new IllegalStateException("[Error] course.json not find! ");
            }
            String jsonData = new String(Objects.requireNonNull(jsonFile).readAllBytes(), Charset.defaultCharset());
            courseParser = new CourseParser(jsonData);
        }
    }

    @Test
    public void parseId() {
        Assertions.assertEquals("370663", courseParser.getCourses().get(0).getId());
        Assertions.assertEquals("370664", courseParser.getCourses().get(1).getId());
    }

    @Test
    public void parseName() {
        Assertions.assertEquals("InstructureCon 2012", courseParser.getCourses().get(0).getName());
        Assertions.assertEquals("InstructureCon 2013", courseParser.getCourses().get(1).getName());
    }
}
