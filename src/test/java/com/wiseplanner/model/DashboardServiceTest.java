package com.wiseplanner.service;

import com.wiseplanner.exception.NetworkException;
import com.wiseplanner.model.*;
import com.wiseplanner.util.GeminiConnector;
import com.wiseplanner.util.parser.DashboardParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

public class DashboardServiceTest {

    private static class StubCanvasService extends CanvasService {
        private final boolean shouldThrow;
        private final List<Course> courses;

        StubCanvasService(boolean shouldThrow) {
            super(new User("test", "token"));
            this.shouldThrow = shouldThrow;
            this.courses = new ArrayList<>();
        }
        StubCanvasService(List<Course> courses) {
            super(new User("test", "token"));
            this.shouldThrow = false;
            this.courses = courses;
        }

        @Override public void updateCourses() throws NetworkException {
            if (shouldThrow) throw new NetworkException("Canvas is down");
        }
        @Override public List<Course> getCourses() { return courses; }
        @Override public void updateAssignments(Course c) throws NetworkException {
            if (shouldThrow) throw new NetworkException("Canvas is down");
        }
        @Override public void updateAnnouncements(Course c) throws NetworkException {
            if (shouldThrow) throw new NetworkException("Canvas is down");
        }
    }

    private static class StubTaskManager extends TaskManager {
        private final List<Task> tasks;
        StubTaskManager(List<Task> tasks) { this.tasks = tasks; }
        @Override public List<Task> getTaskList() { return tasks; }
    }

    private static class StubUserManager extends UserManager {
        private final User user;
        StubUserManager(String name) { this.user = new User(name, "fake-token"); }
        @Override public User getUser() { return user; }
    }

    private static class StubGeminiConnector extends GeminiConnector {
        private final String response;
        private final boolean shouldThrow;
        StubGeminiConnector(String response)    { this.response = response; this.shouldThrow = false; }
        StubGeminiConnector(boolean shouldThrow) { this.response = null;    this.shouldThrow = shouldThrow; }
        @Override public boolean isConfigured() { return true; }
        @Override public String generate(String prompt) throws NetworkException {
            if (shouldThrow) throw new NetworkException("Gemini is unavailable");
            return response;
        }
    }

    private StubUserManager stubUser;
    private StubTaskManager stubTaskManager;

    @BeforeEach
    public void setUp() {
        stubUser = new StubUserManager("Alice");
        List<Task> tasks = new ArrayList<>();
        tasks.add(new Task("2026-04-20", "Read Chapter 5", "Textbook"));
        stubTaskManager = new StubTaskManager(tasks);
    }


    @Test
    public void getDashboard_returnsNonNullDashboard() {
        DashboardService service = new DashboardService(
                new StubCanvasService(new ArrayList<>()), stubTaskManager, stubUser,
                new StubGeminiConnector("insights"), new DashboardParser());
        Assertions.assertNotNull(service.getDashboard());
    }

    @Test
    public void getDashboard_greetingContainsUserName() {
        DashboardService service = new DashboardService(
                new StubCanvasService(new ArrayList<>()), stubTaskManager, stubUser,
                new StubGeminiConnector("insights"), new DashboardParser());
        Assertions.assertTrue(service.getDashboard().getGreeting().contains("Alice"));
    }

    @Test
    public void getDashboard_todoListComesFromTaskManager() {
        DashboardService service = new DashboardService(
                new StubCanvasService(new ArrayList<>()), stubTaskManager, stubUser,
                new StubGeminiConnector("insights"), new DashboardParser());
        Dashboard d = service.getDashboard();
        Assertions.assertEquals(1, d.getTodoList().size());
        Assertions.assertEquals("Read Chapter 5", d.getTodoList().get(0).getTitle());
    }

    @Test
    public void getDashboard_geminiInsightsAndAnalysisNotNull() {
        DashboardService service = new DashboardService(
                new StubCanvasService(new ArrayList<>()), stubTaskManager, stubUser,
                new StubGeminiConnector("Great day!"), new DashboardParser());
        Dashboard d = service.getDashboard();
        Assertions.assertNotNull(d.getGeminiInsights());
        Assertions.assertNotNull(d.getGeminiGradeAnalysis());
    }


    @Test
    public void getDashboard_canvasThrows_dashboardStillReturns() {
        DashboardService service = new DashboardService(
                new StubCanvasService(true), stubTaskManager, stubUser,
                new StubGeminiConnector("insights"), new DashboardParser());
        Assertions.assertDoesNotThrow(() -> service.getDashboard());
    }

    @Test
    public void getDashboard_canvasThrows_todoListStillLoaded() {
        DashboardService service = new DashboardService(
                new StubCanvasService(true), stubTaskManager, stubUser,
                new StubGeminiConnector("insights"), new DashboardParser());
        Assertions.assertEquals(1, service.getDashboard().getTodoList().size());
    }

    @Test
    public void getDashboard_canvasThrows_errorSurfacedInAnnouncements() {
        DashboardService service = new DashboardService(
                new StubCanvasService(true), stubTaskManager, stubUser,
                new StubGeminiConnector("insights"), new DashboardParser());
        boolean hasErr = service.getDashboard().getTodaysAnnouncements()
                .stream().anyMatch(a -> "ERR".equals(a.getId()));
        Assertions.assertTrue(hasErr);
    }

    @Test
    public void getDashboard_geminiThrows_dashboardStillReturns() {
        DashboardService service = new DashboardService(
                new StubCanvasService(new ArrayList<>()), stubTaskManager, stubUser,
                new StubGeminiConnector(true), new DashboardParser());
        Assertions.assertDoesNotThrow(() -> service.getDashboard());
    }

    @Test
    public void getDashboard_geminiThrows_errorMessageInInsights() {
        DashboardService service = new DashboardService(
                new StubCanvasService(new ArrayList<>()), stubTaskManager, stubUser,
                new StubGeminiConnector(true), new DashboardParser());
        Assertions.assertTrue(service.getDashboard().getGeminiInsights().startsWith("[Error]"));
    }

    @Test
    public void getDashboard_geminiThrows_errorMessageInGradeAnalysis() {
        DashboardService service = new DashboardService(
                new StubCanvasService(new ArrayList<>()), stubTaskManager, stubUser,
                new StubGeminiConnector(true), new DashboardParser());
        Assertions.assertTrue(service.getDashboard().getGeminiGradeAnalysis().startsWith("[Error]"));
    }

    @Test
    public void getDashboard_bothCanvasAndGeminiThrow_dashboardStillReturns() {
        DashboardService service = new DashboardService(
                new StubCanvasService(true), stubTaskManager, stubUser,
                new StubGeminiConnector(true), new DashboardParser());
        Dashboard d = Assertions.assertDoesNotThrow(() -> service.getDashboard());
        Assertions.assertEquals(1, d.getTodoList().size()); // local tasks survive
    }
}