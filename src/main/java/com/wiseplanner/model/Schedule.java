package com.wiseplanner.model;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

public class Schedule {
    private String name;
    private Set<DayOfWeek> dayOfWeeks;
    private LocalTime startTime;
    private LocalTime endTime;
    private String professor;
    private String location;
    private String canvasCourseId;

    public Schedule(
            String name,
            Set<DayOfWeek> dayOfWeeks,
            LocalTime startTime,
            LocalTime endTime
    ) {
        this.name = name;
        this.dayOfWeeks = dayOfWeeks;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setDayOfWeeks(Set<DayOfWeek> dayOfWeeks) {
        this.dayOfWeeks = dayOfWeeks;
    }

    public Set<DayOfWeek> getDayOfWeeks() {
        return dayOfWeeks;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public String toStringStartTime() {
        return startTime.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public String toStringEndTime() {
        return endTime.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setProfessor(String professor) {
        this.professor = professor;
    }

    public String getProfessor() {
        return professor;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    public void setCanvasCourseId(String canvasCourseId) {
        this.canvasCourseId = canvasCourseId;
    }

    public String getCanvasCourseId() {
        return canvasCourseId;
    }
}
