package com.wiseplanner.util;

import com.wiseplanner.exception.NetworkException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class GeminiConnectorTest {

    @Test
    public void isConfigured_whenNoEnvVarSet_returnsFalse() {
        String key = System.getenv("GEMINI_API_KEY");
        if (key != null && !key.isBlank()) return; // skip if key is present locally
        Assertions.assertFalse(new GeminiConnector().isConfigured());
    }

    @Test
    public void generate_whenNotConfigured_throwsNetworkException() {
        String key = System.getenv("GEMINI_API_KEY");
        if (key != null && !key.isBlank()) return;
        Assertions.assertThrows(NetworkException.class,
                () -> new GeminiConnector().generate("Hello"));
    }

    @Test
    public void generate_whenNotConfigured_exceptionMessageMentionsApiKey() {
        String key = System.getenv("GEMINI_API_KEY");
        if (key != null && !key.isBlank()) return;
        NetworkException ex = Assertions.assertThrows(NetworkException.class,
                () -> new GeminiConnector().generate("Hello"));
        Assertions.assertTrue(ex.getMessage().contains("GEMINI_API_KEY"));
    }

    private static class StubGeminiConnector extends GeminiConnector {
        private final String fakeResponse;
        private final boolean shouldThrow;

        StubGeminiConnector(String fakeResponse) { this.fakeResponse = fakeResponse; this.shouldThrow = false; }
        StubGeminiConnector(boolean shouldThrow)  { this.fakeResponse = null;         this.shouldThrow = shouldThrow; }

        @Override public boolean isConfigured() { return true; }

        @Override
        public String generate(String prompt) throws NetworkException {
            if (shouldThrow) throw new NetworkException("Simulated network failure");
            return fakeResponse;
        }
    }

    @Test
    public void stubConnector_isConfigured_returnsTrue() {
        Assertions.assertTrue(new StubGeminiConnector("response").isConfigured());
    }

    @Test
    public void stubConnector_generate_returnsConfiguredResponse() throws NetworkException {
        Assertions.assertEquals("Great work today!", new StubGeminiConnector("Great work today!").generate("prompt"));
    }

    @Test
    public void stubConnector_generate_throwsWhenConfigured() {
        Assertions.assertThrows(NetworkException.class,
                () -> new StubGeminiConnector(true).generate("prompt"));
    }
}