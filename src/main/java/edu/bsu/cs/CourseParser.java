package edu.bsu.cs;

import com.jayway.jsonpath.JsonPath;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CourseParser {
    private List<Course> courses = new ArrayList<>();

    public CourseParser(String jsonData) {
        List<Map<String, Object>> rawCourses = JsonPath.read(jsonData, "$");
        for (Map<String, Object> i : rawCourses) {
            String id = String.valueOf(i.get("id"));
            String name = String.valueOf(i.get("name"));
            courses.add(new Course(id, name));
        }
    }

    public List<Course> getCourses() {
        return courses;
    }
}
