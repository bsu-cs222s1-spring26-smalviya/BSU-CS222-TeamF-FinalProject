package edu.bsu.cs;

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

    public String fetchCourses() throws IOException, URISyntaxException {
        String encodedUrlString = "https://canvas.instructure.com/api/v1/courses?access_token=" +
                URLEncoder.encode(user.getCanvasToken(), Charset.defaultCharset());
        URI uri = new URI(encodedUrlString);
        URLConnection connection = uri.toURL().openConnection();
        connection.setRequestProperty("User-Agent", "Final Project" + user.getName());
        connection.connect();
        return new String(connection.getInputStream().readAllBytes(), Charset.defaultCharset());
    }

    public String fetchAssignments(String courseId) throws IOException, URISyntaxException {
        String encodedUrlString = "https://canvas.instructure.com/api/v1/courses/" +
                courseId + "/assignments?access_token=" +
                URLEncoder.encode(user.getCanvasToken(), Charset.defaultCharset());
        URI uri = new URI(encodedUrlString);
        URLConnection connection = uri.toURL().openConnection();
        connection.setRequestProperty("User-Agent", "Final Project" + user.getName());
        connection.connect();
        return new String(connection.getInputStream().readAllBytes(), Charset.defaultCharset());
    }
}
