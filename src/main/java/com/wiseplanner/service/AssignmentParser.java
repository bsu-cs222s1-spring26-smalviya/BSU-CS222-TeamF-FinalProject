package com.wiseplanner.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wiseplanner.model.Assignment;

import java.lang.reflect.Type;
import java.util.List;

public class AssignmentParser {
    private List<Assignment> assignments;

    public AssignmentParser(String jsonData) {
        Type listType = new TypeToken<List<Assignment>>() {
        }.getType();
        Gson gson = new Gson();
        assignments = gson.fromJson(jsonData, listType);
    }

    public List<Assignment> getAssignments() {
        return assignments;
    }
}