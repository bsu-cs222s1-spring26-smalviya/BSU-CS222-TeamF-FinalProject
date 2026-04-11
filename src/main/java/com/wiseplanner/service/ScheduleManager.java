package com.wiseplanner.service;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.wiseplanner.exception.DeleteException;
import com.wiseplanner.exception.FileReadException;
import com.wiseplanner.exception.FileWriteException;
import com.wiseplanner.model.Schedule;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ScheduleManager {
    private static final String DATA_FOLDER = ".wiseplanner";
    private static final String SCHEDULE_DATA_FILE = "schedule.json";
    private Path scheduleDataPath;
    private String userPath;
    private List<Schedule> scheduleList;
    Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalTime.class, (JsonSerializer<LocalTime>) (src, typeOfSrc, context) ->
                    new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_TIME)))
            .registerTypeAdapter(LocalTime.class, (JsonDeserializer<LocalTime>) (json, typeOfT, context) ->
                    LocalTime.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_TIME))
            .create();

    public ScheduleManager() {
        this.userPath = System.getProperty("user.home");
        this.scheduleDataPath = Paths.get(userPath, DATA_FOLDER, SCHEDULE_DATA_FILE);
    }

    public void addSchedule(String name, Set<DayOfWeek> dayOfWeeks, LocalTime startTime, LocalTime endTime) throws FileWriteException {
        Schedule schedule = new Schedule(name, dayOfWeeks, startTime, endTime);
        scheduleList.add(schedule);
        saveSchedule();
    }

    public void addSchedule(String name, Set<DayOfWeek> dayOfWeeks, LocalTime startTime, LocalTime endTime, String professor, String location) throws FileWriteException {
        Schedule schedule = new Schedule(name, dayOfWeeks, startTime, endTime);
        schedule.setProfessor(professor);
        schedule.setLocation(location);
        scheduleList.add(schedule);
        saveSchedule();
    }

    public void deleteSchedule(int index) throws IndexOutOfBoundsException, FileWriteException {
        if (index < 0 || index >= scheduleList.size()) {
            throw new IndexOutOfBoundsException("Schedule deletion failed, invalid schedule index.");
        }
        scheduleList.remove(index);
        saveSchedule();
    }

    public void deleteSchedule(Schedule schedule) throws DeleteException {
        if (scheduleList.remove(schedule)) {
            saveSchedule();
        } else {
            throw new DeleteException("Schedule deletion failed, schedule not found.");
        }
    }

    public List<Schedule> getScheduleList() {
        return scheduleList;
    }

    public void loadSchedule() throws FileReadException {
        File scheduleDataFile = scheduleDataPath.toFile();
        if (scheduleDataFile.exists()) {
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(scheduleDataFile))) {
                Type listType = new TypeToken<List<Schedule>>() {
                }.getType();
                scheduleList = gson.fromJson(bufferedReader, listType);
                if (scheduleList == null) {
                    scheduleList = new ArrayList<>();
                }
            } catch (IOException e) {
                scheduleList = new ArrayList<>();
                throw (new FileReadException("File read failed, unable to read schedule data."));
            }
        } else {
            scheduleList = new ArrayList<>();
        }
    }

    public void saveSchedule() throws FileWriteException {
        File scheduleDataFile = scheduleDataPath.toFile();
        scheduleDataFile.getParentFile().mkdirs();
        Type listType = new TypeToken<List<Schedule>>() {
        }.getType();
        String scheduleJson = gson.toJson(scheduleList, listType);
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(scheduleDataFile))) {
            bufferedWriter.write(scheduleJson);
        } catch (IOException e) {
            throw (new FileWriteException("File write failed, unable to write schedule data."));
        }
    }
}
