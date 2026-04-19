package com.wiseplanner.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.wiseplanner.exception.NetworkException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;

public class GeminiConnector {

    private static final String GEMINI_MODEL = "gemini-2.0-flash";
    private static final String GEMINI_API_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/"
                    + GEMINI_MODEL + ":generateContent?key=";

    private final String apiKey;
    private final Gson gson = new Gson();

    public GeminiConnector() {
        // Read from environment variable — safer than hardcoding in source code
        this.apiKey = System.getenv("GEMINI_API_KEY");
    }

    public boolean isConfigured() {
        return apiKey != null && !apiKey.isBlank();
    }

    public String generate(String prompt) throws NetworkException {
        if (!isConfigured()) {
            throw new NetworkException("Gemini API key not configured. Set the GEMINI_API_KEY environment variable.");
        }

        try {
            // Build the JSON body Gemini expects:
            // { "contents": [ { "parts": [ { "text": "..." } ] } ] }
            JsonObject textPart = new JsonObject();
            textPart.addProperty("text", prompt);

            JsonArray partsArray = new JsonArray();
            partsArray.add(textPart);

            JsonObject content = new JsonObject();
            content.add("parts", partsArray);

            JsonArray contentsArray = new JsonArray();
            contentsArray.add(content);

            JsonObject requestBody = new JsonObject();
            requestBody.add("contents", contentsArray);

            String requestJson = gson.toJson(requestBody);

            // Open connection and POST the JSON
            URI uri = new URI(GEMINI_API_URL + apiKey);
            HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true); // tells Java we want to SEND data
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(15000);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = requestJson.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                String errorBody = readStream(connection.getErrorStream());
                throw new NetworkException("Gemini API returned HTTP " + responseCode + ": " + errorBody);
            }

            String responseJson = readStream(connection.getInputStream());
            return parseGeminiResponse(responseJson);

        } catch (NetworkException e) {
            throw e;
        } catch (Exception e) {
            throw new NetworkException("Failed to connect to Gemini API: " + e.getMessage());
        }
    }

    private String parseGeminiResponse(String responseJson) throws NetworkException {
        try {
            // Response structure: { "candidates": [{ "content": { "parts": [{ "text": "..." }] } }] }
            JsonObject response = gson.fromJson(responseJson, JsonObject.class);
            JsonArray candidates = response.getAsJsonArray("candidates");
            if (candidates == null || candidates.isEmpty()) {
                throw new NetworkException("Gemini returned no candidates in response.");
            }
            JsonObject firstCandidate = candidates.get(0).getAsJsonObject();
            JsonObject content = firstCandidate.getAsJsonObject("content");
            JsonArray parts = content.getAsJsonArray("parts");
            return parts.get(0).getAsJsonObject().get("text").getAsString().trim();
        } catch (NetworkException e) {
            throw e;
        } catch (Exception e) {
            throw new NetworkException("Failed to parse Gemini response: " + e.getMessage());
        }
    }

    private String readStream(InputStream stream) throws IOException {
        if (stream == null) return "";
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString().trim();
        }
    }
}