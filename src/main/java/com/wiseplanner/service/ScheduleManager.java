package com.wiseplanner.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wiseplanner.exception.DeleteException;
import com.wiseplanner.exception.FileReadException;
import com.wiseplanner.exception.FileWriteException;
import com.wiseplanner.model.Schedule;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ScheduleManager {
    private static final String DATA_FOLDER = ".wiseplanner";
    private static final String SCHEDULE_DATA_FILE = "schedule.json";
    private Path scheduleDataPath;
    private String userPath;
    private List<Schedule> scheduleList;
    Gson gson = new Gson();

    public ScheduleManager() {
        this.userPath = System.getProperty("user.home");
        this.scheduleDataPath = Paths.get(userPath, DATA_FOLDER, SCHEDULE_DATA_FILE);
    }

    public void addSchedule(Schedule schedule) {
        scheduleList.add(schedule);
        saveSchedule();
    }

    public void deleteSchedule(int index) {
        if (index < 0 || index >= scheduleList.size()) {
            throw new IndexOutOfBoundsException("Schedule deletion failed, invalid schedule index.");
        }
        scheduleList.remove(index);
        saveSchedule();
    }

    public void deleteSchedule(Schedule schedule) {
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
