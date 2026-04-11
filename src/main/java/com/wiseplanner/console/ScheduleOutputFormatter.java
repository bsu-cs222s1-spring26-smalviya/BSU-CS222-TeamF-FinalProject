package com.wiseplanner.console;

import com.wiseplanner.model.Schedule;

import java.util.List;

public class ScheduleOutputFormatter {
    public String getScheduleOutput(List<Schedule> scheduleList) {
        StringBuilder output = new StringBuilder();
        output.append("Schedule\n" +
                "Name\tTime\tProfessor\tLocation\tClass on\n");
        for (Schedule i : scheduleList) {
            output.append(
                    i.getName() + "\t" +
                            i.toStringStartTime() + "-" + i.toStringEndTime() + "\t" +
                            i.getProfessor() + "\t" +
                            i.getLocation() + "\t" +
                            i.getDayOfWeeks().toString()+
                            "\n"
            );
        }
        return output.toString();
    }
}
