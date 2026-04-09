package com.wiseplanner.util;

import com.wiseplanner.util.parser.AnnouncementParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Objects;

public class AnnouncementParserTest {
    AnnouncementParser announcementParser;

    public AnnouncementParserTest() throws IOException {
        try (InputStream jsonFile = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("announcement.json")) {
            if (jsonFile == null) {
                throw new IllegalStateException("[Error] announcement.json not found!");
            }
            String jsonData = new String(Objects.requireNonNull(jsonFile).readAllBytes(), Charset.defaultCharset());
            announcementParser = new AnnouncementParser(jsonData);
        }
    }

    @Test
    public void parseId(){
        Assertions.assertEquals("1",announcementParser.getAnnouncements().get(0).getId());
    }
    @Test
    public void parseTitle(){
        Assertions.assertEquals("Hear ye",announcementParser.getAnnouncements().get(0).getTitle());
    }
    @Test
    public void parseMessage(){
        Assertions.assertEquals("Henceforth, all assignments must be...",announcementParser.getAnnouncements().get(0).getMessage());
    }
    @Test
    public void parsePosted_at(){
        Assertions.assertEquals("2017-01-31T22:00:00Z",announcementParser.getAnnouncements().get(0).getPosted_at());
    }
}
