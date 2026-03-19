package edu.bsu.cs;

import com.jayway.jsonpath.JsonPath;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AssignmentParser {
    private List<Assignment> assignments = new ArrayList<>();

    public AssignmentParser(String jsonData) {
        List<Map<String, Object>> rawAssignments = JsonPath.read(jsonData, "$");
        for (Map<String, Object> item : rawAssignments) {
            String id = String.valueOf(item.get("id"));
            String name = String.valueOf(item.get("name"));
            String description = String.valueOf(item.get("description"));
            String dueAt = String.valueOf(item.get("due_at"));
            assignments.add(new Assignment(id, name, description, dueAt));
        }
    }

    public List<Assignment> getAssignments() {
        return assignments;
    }
}