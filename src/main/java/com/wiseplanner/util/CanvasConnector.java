package com.wiseplanner.util;

import com.wiseplanner.exception.NetworkException;
import com.wiseplanner.model.Course;
import com.wiseplanner.model.User;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class CanvasConnector {
    private User user;

    public CanvasConnector(User user) {
        this.user = user;
    }

    public String fetchCourses() throws NetworkException {
        try {
            String encodedUrlString = "https://canvas.instructure.com/api/v1/courses?access_token=" +
                    URLEncoder.encode(user.getCanvasToken(), Charset.defaultCharset());
            URI uri = new URI(encodedUrlString);
            URLConnection connection = uri.toURL().openConnection();
            connection.setRequestProperty("User-Agent", "Final Project " + user.getName());
            connection.connect();
            return new String(connection.getInputStream().readAllBytes(), Charset.defaultCharset());
        } catch (URISyntaxException | IOException e) {
            throw new NetworkException("Network connection failed, unable to retrieve course information.");
        }

    }

    public List<String> fetchAssignments(Course course) throws NetworkException {
        try {
            List<String> pages = new ArrayList<>();
            String encodedUrlString = "https://canvas.instructure.com/api/v1/courses/" +
                    course.getId() + "/assignments?include[]=submission";
            while (encodedUrlString != null) {
                URI uri = new URI(encodedUrlString);
                URLConnection connection = uri.toURL().openConnection();
                connection.setRequestProperty("Authorization", "Bearer " + user.getCanvasToken());
                connection.setRequestProperty("User-Agent", "Final Project " + user.getName());
                connection.connect();
                String body = new String(connection.getInputStream().readAllBytes(), Charset.defaultCharset());
                pages.add(body);
                String link = connection.getHeaderField("Link");
                encodedUrlString = parseNextUrl(link);
            }
            return pages;
        } catch (URISyntaxException | IOException e) {
            throw new NetworkException("Network connection failed, unable to retrieve assignments information.");
        }
    }

    public String fetchAnnouncements(Course course) throws NetworkException {
        try {
            String encodedUrlString = "https://canvas.instructure.com/api/v1/announcements?context_codes%5B%5D=course_" +
                    course.getId() + "&access_token=" + URLEncoder.encode(user.getCanvasToken(), Charset.defaultCharset());
            System.out.println("Fetching URL: " + encodedUrlString);
            URI uri = new URI(encodedUrlString);
            URLConnection connection = uri.toURL().openConnection();
            connection.setRequestProperty("Authorization", "Bearer " + user.getCanvasToken());
            connection.setRequestProperty("User-Agent", "Final Project " + user.getName());
            connection.connect();
            String response = new String(connection.getInputStream().readAllBytes(), Charset.defaultCharset());
            System.out.println("RAW JSON: " + response);
            return response;
        } catch (URISyntaxException | IOException e) {
            throw new NetworkException("Network connection failed, unable to retrieve announcements information.");
        }
    }

    private String parseNextUrl(String linkHeader) {
        if (linkHeader == null) {
            return null;
        }
        for (String part : linkHeader.split(",")) {
            if (part.contains("rel=\"next\"")) {
                int start = part.indexOf("<") + 1;
                int end = part.indexOf(">");
                return part.substring(start, end);
            }
        }
        return null;
    }
}
