package persistence;


import model.Task;
import model.TaskList;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

// Represents a reader that reads task list from JSON data stored in file
public class JsonReader {
    private String source;

    // EFFECTS: constructs reader to read from source file
    public JsonReader(String source) {
        this.source = source;
    }

    // EFFECTS: reads task list from file and returns it;
    // throws IOException if an error occurs reading data from file
    public TaskList read() throws IOException {
        String jsonData = readFile(source);
        JSONObject jsonObject = new JSONObject(jsonData);
        return parseTaskList(jsonObject);
    }

    // EFFECTS: reads source file as string and returns it
    private String readFile(String source) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();

        try (Stream<String> stream = Files.lines(Paths.get(source), StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s));
        }

        return contentBuilder.toString();
    }

    // EFFECTS: parses task list from JSON object and returns it
    private TaskList parseTaskList(JSONObject jsonObject) {
        String name = jsonObject.getString("name");
        TaskList tl = new TaskList(name);
        addTasks(tl, jsonObject);
        return tl;
    }

    // MODIFIES: tl
    // EFFECTS: parses tasks from JSON object and adds them to task list
    private void addTasks(TaskList tl, JSONObject jsonObject) {
        JSONArray jsonArray = jsonObject.getJSONArray("Tasks");
        for (Object json : jsonArray) {
            JSONObject nextTask = (JSONObject) json;
            addTask(tl, nextTask);
        }
    }

    // MODIFIES: tl
    // EFFECTS: parses task from JSON object and adds it to task list
    private void addTask(TaskList tl, JSONObject jsonObject) {
        int    year = jsonObject.getInt("year");
        int   month = jsonObject.getInt("month");
        int     day = jsonObject.getInt("day");
        String name = jsonObject.getString("name");
        Boolean status = jsonObject.getBoolean("status");
        String description = jsonObject.getString("description");
        Task task = new Task(year, month, day, name, description);
        if (status) {
            task.markCompleted();
        }
        tl.insertTask(task);
    }
}
