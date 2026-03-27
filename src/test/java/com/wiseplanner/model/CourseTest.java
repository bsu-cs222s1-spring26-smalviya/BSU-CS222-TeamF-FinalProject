package com.wiseplanner.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CourseTest {
    Course course = new Course("370663", "InstructureCon 2012");

    @Test
    public void setId() {
        course.setId("370664");
        Assertions.assertEquals("370664", course.getId());
    }

    @Test
    public void getId() {
        Assertions.assertEquals("370663", course.getId());
    }

    @Test
    public void setName() {
        course.setName("InstructureCon 2013");
        Assertions.assertEquals("InstructureCon 2013", course.getName());
    }

    @Test
    public void getName() {
        Assertions.assertEquals("InstructureCon 2012", course.getName());
    }
}
