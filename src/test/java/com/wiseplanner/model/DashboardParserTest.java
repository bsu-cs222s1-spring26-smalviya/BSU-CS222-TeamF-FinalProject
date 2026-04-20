package com.wiseplanner.util.parser;

import com.wiseplanner.model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DashboardParserTest {

    private DashboardParser parser;
    private final String TODAY     = LocalDate.now().toString();
    private final String YESTERDAY = LocalDate.now().minusDays(1).toString();
    private final String TOMORROW  = LocalDate.now().plusDays(1).toString();

    @BeforeEach
    public void setUp() {
        parser = new DashboardParser();
    }

    @Test
    public void buildDashboard_returnsNonNullDashboard() {
        Assertions.assertNotNull(parser.buildDashboard("Alice",
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), "i", "a"));
    }

    @Test
    public void buildDashboard_greetingContainsUserName() {
        Dashboard d = parser.buildDashboard("Alice",
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), "", "");
        Assertions.assertTrue(d.getGreeting().contains("Alice"));
    }

    @Test
    public void buildDashboard_greetingContainsTodaysYear() {
        Dashboard d = parser.buildDashboard("Alice",
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), "", "");
        Assertions.assertTrue(d.getGreeting().contains(String.valueOf(LocalDate.now().getYear())));
    }

    @Test
    public void buildDashboard_geminiInsightsStoredAsIs() {
        Dashboard d = parser.buildDashboard("Alice",
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), "Great day!", "grades");
        Assertions.assertEquals("Great day!", d.getGeminiInsights());
    }

    @Test
    public void buildDashboard_geminiGradeAnalysisStoredAsIs() {
        Dashboard d = parser.buildDashboard("Alice",
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), "insights", "2 missing!");
        Assertions.assertEquals("2 missing!", d.getGeminiGradeAnalysis());
    }

    @Test
    public void buildDashboard_todoListStoredAsIs() {
        List<Task> tasks = new ArrayList<>();
        tasks.add(new Task("2026-04-20", "Study", "Chapter 5"));
        tasks.add(new Task("2026-04-21", "Exercise", "30 mins"));
        Dashboard d = parser.buildDashboard("Alice",
                new ArrayList<>(), new ArrayList<>(), tasks, "", "");
        Assertions.assertEquals(2, d.getTodoList().size());
    }


    @Test
    public void buildDashboard_assignmentDueToday_isIncluded() {
        List<Assignment> a = new ArrayList<>();
        a.add(new Assignment("1", "Today's Lab", "desc", TODAY + "T23:59:00Z"));
        Dashboard d = parser.buildDashboard("Alice", a, new ArrayList<>(), new ArrayList<>(), "", "");
        Assertions.assertEquals(1, d.getTodaysAssignments().size());
        Assertions.assertEquals("Today's Lab", d.getTodaysAssignments().get(0).getName());
    }

    @Test
    public void buildDashboard_assignmentDueYesterday_isExcluded() {
        List<Assignment> a = new ArrayList<>();
        a.add(new Assignment("2", "Old Lab", "desc", YESTERDAY + "T23:59:00Z"));
        Dashboard d = parser.buildDashboard("Alice", a, new ArrayList<>(), new ArrayList<>(), "", "");
        Assertions.assertEquals(0, d.getTodaysAssignments().size());
    }

    @Test
    public void buildDashboard_assignmentDueTomorrow_isExcluded() {
        List<Assignment> a = new ArrayList<>();
        a.add(new Assignment("3", "Future Lab", "desc", TOMORROW + "T23:59:00Z"));
        Dashboard d = parser.buildDashboard("Alice", a, new ArrayList<>(), new ArrayList<>(), "", "");
        Assertions.assertEquals(0, d.getTodaysAssignments().size());
    }

    @Test
    public void buildDashboard_assignmentNullDueDate_isExcluded() {
        List<Assignment> a = new ArrayList<>();
        a.add(new Assignment("4", "No Deadline", "desc", null));
        Dashboard d = parser.buildDashboard("Alice", a, new ArrayList<>(), new ArrayList<>(), "", "");
        Assertions.assertEquals(0, d.getTodaysAssignments().size());
    }

    @Test
    public void buildDashboard_assignmentStringNullDueDate_isExcluded() {
        List<Assignment> a = new ArrayList<>();
        a.add(new Assignment("5", "Null String Due", "desc", "null"));
        Dashboard d = parser.buildDashboard("Alice", a, new ArrayList<>(), new ArrayList<>(), "", "");
        Assertions.assertEquals(0, d.getTodaysAssignments().size());
    }

    @Test
    public void buildDashboard_mixedDueDates_onlyTodayReturned() {
        List<Assignment> a = new ArrayList<>();
        a.add(new Assignment("1", "Today A",    "d", TODAY     + "T10:00:00Z"));
        a.add(new Assignment("2", "Yesterday",  "d", YESTERDAY + "T10:00:00Z"));
        a.add(new Assignment("3", "Today B",    "d", TODAY     + "T23:59:00Z"));
        a.add(new Assignment("4", "Tomorrow",   "d", TOMORROW  + "T10:00:00Z"));
        Dashboard d = parser.buildDashboard("Alice", a, new ArrayList<>(), new ArrayList<>(), "", "");
        Assertions.assertEquals(2, d.getTodaysAssignments().size());
    }

    @Test
    public void buildDashboard_nullAssignmentsList_returnsEmptyList() {
        Dashboard d = parser.buildDashboard("Alice", null, new ArrayList<>(), new ArrayList<>(), "", "");
        Assertions.assertNotNull(d.getTodaysAssignments());
        Assertions.assertEquals(0, d.getTodaysAssignments().size());
    }


    @Test
    public void buildDashboard_announcementPostedToday_isIncluded() {
        List<Announcement> ann = new ArrayList<>();
        ann.add(new Announcement("10", "Today's Notice", "msg", TODAY + "T09:00:00Z"));
        Dashboard d = parser.buildDashboard("Alice", new ArrayList<>(), ann, new ArrayList<>(), "", "");
        Assertions.assertEquals(1, d.getTodaysAnnouncements().size());
        Assertions.assertEquals("Today's Notice", d.getTodaysAnnouncements().get(0).getTitle());
    }

    @Test
    public void buildDashboard_announcementPostedYesterday_isExcluded() {
        List<Announcement> ann = new ArrayList<>();
        ann.add(new Announcement("11", "Old Notice", "msg", YESTERDAY + "T09:00:00Z"));
        Dashboard d = parser.buildDashboard("Alice", new ArrayList<>(), ann, new ArrayList<>(), "", "");
        Assertions.assertEquals(0, d.getTodaysAnnouncements().size());
    }

    @Test
    public void buildDashboard_announcementNullPostedAt_isExcluded() {
        List<Announcement> ann = new ArrayList<>();
        ann.add(new Announcement("12", "No Date", "msg", null));
        Dashboard d = parser.buildDashboard("Alice", new ArrayList<>(), ann, new ArrayList<>(), "", "");
        Assertions.assertEquals(0, d.getTodaysAnnouncements().size());
    }

    @Test
    public void buildDashboard_nullAnnouncementsList_returnsEmptyList() {
        Dashboard d = parser.buildDashboard("Alice", new ArrayList<>(), null, new ArrayList<>(), "", "");
        Assertions.assertNotNull(d.getTodaysAnnouncements());
        Assertions.assertEquals(0, d.getTodaysAnnouncements().size());
    }

    @Test
    public void buildDashboard_mixedAnnouncementDates_onlyTodayReturned() {
        List<Announcement> ann = new ArrayList<>();
        ann.add(new Announcement("1", "Today A",   "m", TODAY     + "T08:00:00Z"));
        ann.add(new Announcement("2", "Yesterday", "m", YESTERDAY + "T08:00:00Z"));
        ann.add(new Announcement("3", "Today B",   "m", TODAY     + "T12:00:00Z"));
        Dashboard d = parser.buildDashboard("Alice", new ArrayList<>(), ann, new ArrayList<>(), "", "");
        Assertions.assertEquals(2, d.getTodaysAnnouncements().size());
    }

    @Test
    public void buildDashboard_nullTodoList_returnsEmptyList() {
        Dashboard d = parser.buildDashboard("Alice", new ArrayList<>(), new ArrayList<>(), null, "", "");
        Assertions.assertNotNull(d.getTodoList());
        Assertions.assertEquals(0, d.getTodoList().size());
    }

    @Test
    public void buildDashboard_emptyTodoList_returnsEmptyList() {
        Dashboard d = parser.buildDashboard("Alice",
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), "", "");
        Assertions.assertEquals(0, d.getTodoList().size());
    }
}