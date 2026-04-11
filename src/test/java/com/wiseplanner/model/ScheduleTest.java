package com.wiseplanner.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

public class ScheduleTest {
    Schedule schedule;

    public ScheduleTest() {
        Set<DayOfWeek> dayOfWeeks = new HashSet<>();
        dayOfWeeks.add(DayOfWeek.MONDAY);
        dayOfWeeks.add(DayOfWeek.WEDNESDAY);
        dayOfWeeks.add(DayOfWeek.FRIDAY);
        LocalTime startTime = LocalTime.of(10, 00);
        LocalTime endTime = LocalTime.of(10, 50);
        schedule = new Schedule("CS230", dayOfWeeks, startTime, endTime);
        schedule.setProfessor("No Name");
        schedule.setLocation("RB120");
        schedule.setCanvasCourseId("370663");
    }

    @Test
    public void setName() {
        schedule.setName("CS222");
        Assertions.assertEquals("CS222", schedule.getName());
    }

    @Test
    public void getName() {
        Assertions.assertEquals("CS230", schedule.getName());
    }

    @Test
    public void setDayOfWeeks() {
        Set<DayOfWeek> dayOfWeeks = new HashSet<>();
        dayOfWeeks.add(DayOfWeek.TUESDAY);
        dayOfWeeks.add(DayOfWeek.THURSDAY);
        schedule.setDayOfWeeks(dayOfWeeks);
        Assertions.assertEquals(dayOfWeeks, schedule.getDayOfWeeks());
    }

    @Test
    public void getDayOfWeeks() {
        Set<DayOfWeek> dayOfWeeks = new HashSet<>();
        dayOfWeeks.add(DayOfWeek.MONDAY);
        dayOfWeeks.add(DayOfWeek.WEDNESDAY);
        dayOfWeeks.add(DayOfWeek.FRIDAY);
        Assertions.assertEquals(dayOfWeeks, schedule.getDayOfWeeks());
    }

    @Test
    public void setStartTime() {
        LocalTime startTime = LocalTime.of(10, 50);
        schedule.setStartTime(startTime);
        Assertions.assertEquals(startTime, schedule.getStartTime());
    }

    @Test
    public void toStringStartTime() {
        Assertions.assertEquals("10:00", schedule.toStringStartTime());
    }

    @Test
    public void getStartTime() {
        LocalTime startTime = LocalTime.of(10, 00);
        Assertions.assertEquals(startTime, schedule.getStartTime());
    }

    @Test
    public void setEndTime() {
        LocalTime endTime = LocalTime.of(10, 00);
        schedule.setEndTime(endTime);
        Assertions.assertEquals(endTime, schedule.getEndTime());
    }

    @Test
    public void toStringEndTime() {
        Assertions.assertEquals("10:50", schedule.toStringEndTime());
    }

    @Test
    public void getEndTime() {
        LocalTime endTime = LocalTime.of(10, 50);
        Assertions.assertEquals(endTime, schedule.getEndTime());
    }

    @Test
    public void setProfessor() {
        schedule.setProfessor("Test Name");
        Assertions.assertEquals("Test Name", schedule.getProfessor());
    }

    @Test
    public void getProfessor() {
        Assertions.assertEquals("No Name", schedule.getProfessor());
    }

    @Test
    public void setLocation() {
        schedule.setLocation("RB130");
        Assertions.assertEquals("RB130", schedule.getLocation());
    }

    @Test
    public void getLocation() {
        Assertions.assertEquals("RB120", schedule.getLocation());
    }

    @Test
    public void setCanvasCourseId() {
        schedule.setCanvasCourseId("370664");
        Assertions.assertEquals("370664", schedule.getCanvasCourseId());
    }

    @Test
    public void getCanvasCourseId() {
        Assertions.assertEquals("370663", schedule.getCanvasCourseId());
    }
}
