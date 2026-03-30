package com.wiseplanner.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wiseplanner.model.Assignment;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class AssignmentParser {
    private List<Assignment> assignments = new ArrayList<>();

    public AssignmentParser(List<String> jsonData) {
        Type listType = new TypeToken<List<Assignment>>() {
        }.getType();
        Gson gson = new Gson();
        for (String i : jsonData) {
            List<Assignment> pageContent = gson.fromJson(i, listType);
            assignments.addAll(pageContent);
        }
    }

    public List<Assignment> getAssignments() {
        return assignments;
    }
}