package com.wiseplanner.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wiseplanner.model.Announcement;

import java.lang.reflect.Type;
import java.util.List;

public class AnnouncementParser {
    private List<Announcement> announcements;

    public AnnouncementParser(String jsonData) {
        Type listType = new TypeToken<List<Announcement>>() {
        }.getType();
        Gson gson = new Gson();
        announcements = gson.fromJson(jsonData, listType);
    }

    public List<Announcement> getAnnouncements() {
        return announcements;
    }
}
