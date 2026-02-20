package org.example;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

public class TaskRepository {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final TypeReference<List<Task>> TASK_LIST_TYPE = new TypeReference<>() { };

    private final Path storagePath;

    public TaskRepository(Path storagePath) {
        this.storagePath = storagePath;
    }

    public List<Task> loadTasks() throws IOException {
        if (Files.notExists(storagePath)) {
            return Collections.emptyList();
        }
        return OBJECT_MAPPER.readValue(storagePath.toFile(), TASK_LIST_TYPE);
    }

    public void saveTasks(List<Task> tasks) throws IOException {
        Path parent = storagePath.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValue(storagePath.toFile(), tasks);
    }
}
