package com.wiseplanner.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wiseplanner.model.Course;

import java.lang.reflect.Type;
import java.util.List;

public class CourseParser {
    private List<Course> courses;

    public CourseParser(String jsonData) {
        Type listType = new TypeToken<List<Course>>() {
        }.getType();
        Gson gson = new Gson();
        courses = gson.fromJson(jsonData, listType);
    }

    public List<Course> getCourses() {
        return courses;
    }
}
