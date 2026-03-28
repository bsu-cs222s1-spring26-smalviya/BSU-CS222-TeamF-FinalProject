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

    public String fetchAssignments(Course course) throws NetworkException {
        try {
            String encodedUrlString = "https://canvas.instructure.com/api/v1/courses/" +
                    course.getId() + "/assignments?include[]=submission&access_token=" +
                    URLEncoder.encode(user.getCanvasToken(), Charset.defaultCharset());
            URI uri = new URI(encodedUrlString);
            URLConnection connection = uri.toURL().openConnection();
            connection.setRequestProperty("User-Agent", "Final Project " + user.getName());
            connection.connect();
            return new String(connection.getInputStream().readAllBytes(), Charset.defaultCharset());
        } catch (URISyntaxException | IOException e) {
            throw new NetworkException("Network connection failed, unable to retrieve assignments information.");
        }
    }
}
