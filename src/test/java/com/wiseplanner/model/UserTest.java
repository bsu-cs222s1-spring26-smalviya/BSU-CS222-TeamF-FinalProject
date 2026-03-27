package com.wiseplanner.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UserTest {
    User user = new User("Canvas Account", "nZA7QWcIwj");

    @Test
    public void setName() {
        user.setName("Canvas Account1");
        Assertions.assertEquals("Canvas Account1", user.getName());
    }

    @Test
    public void getName() {
        Assertions.assertEquals("Canvas Account", user.getName());
    }

    @Test
    public void setCanvasToken() {
        user.setCanvasToken("nZA7QWcIwk");
        Assertions.assertEquals("nZA7QWcIwk", user.getCanvasToken());
    }

    @Test
    public void getCanvasToken() {
        Assertions.assertEquals("nZA7QWcIwj", user.getCanvasToken());
    }
}
