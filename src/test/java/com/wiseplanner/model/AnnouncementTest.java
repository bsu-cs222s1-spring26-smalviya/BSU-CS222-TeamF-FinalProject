package com.wiseplanner.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AnnouncementTest {
    Announcement announcement = new Announcement("1", "Hear ye", "Henceforth, all assignments must be...", "2017-01-31T22:00:00Z");

    @Test
    public void setId() {
        announcement.setId("2");
        Assertions.assertEquals("2", announcement.getId());
    }

    @Test
    public void getId() {
        Assertions.assertEquals("1", announcement.getId());
    }

    @Test
    public void setTitle() {
        announcement.setTitle("Hear");
        Assertions.assertEquals("Hear", announcement.getTitle());
    }

    @Test
    public void getTitle() {
        Assertions.assertEquals("Hear ye", announcement.getTitle());
    }

    @Test
    public void setMessage() {
        announcement.setMessage("Henceforth,");
        Assertions.assertEquals("Henceforth,", announcement.getMessage());
    }

    @Test
    public void getMessage() {
        Assertions.assertEquals("Henceforth, all assignments must be...", announcement.getMessage());
    }

    @Test
    public void setPosted_at() {
        announcement.setPosted_at("2017-01-30T22:00:00Z");
        Assertions.assertEquals("2017-01-30T22:00:00Z", announcement.getPosted_at());
    }

    @Test
    public void getPosted_at() {
        Assertions.assertEquals("2017-01-31T22:00:00Z", announcement.getPosted_at());
    }
}
