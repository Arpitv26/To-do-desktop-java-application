package org.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class TaskRepositoryTest {
    @TempDir
    Path tempDir;

    @Test
    void loadTasksReturnsEmptyWhenFileDoesNotExist() throws IOException {
        TaskRepository repository = new TaskRepository(tempDir.resolve("tasks.json"));

        List<Task> tasks = repository.loadTasks();

        assertTrue(tasks.isEmpty());
    }

    @Test
    void saveAndLoadTasksRoundTrip() throws IOException {
        Path storage = tempDir.resolve("tasks.json");
        TaskRepository repository = new TaskRepository(storage);
        Task completedTask = new Task("Pay bills");
        completedTask.setCompleted(true);

        repository.saveTasks(List.of(new Task("Buy milk"), completedTask));
        List<Task> loadedTasks = repository.loadTasks();

        assertEquals(2, loadedTasks.size());
        assertEquals("Buy milk", loadedTasks.get(0).getText());
        assertTrue(loadedTasks.get(1).isCompleted());
    }

    @Test
    void loadTasksThrowsOnInvalidJson() throws IOException {
        Path storage = tempDir.resolve("tasks.json");
        Files.writeString(storage, "{not-valid-json");
        TaskRepository repository = new TaskRepository(storage);
        assertThrows(IOException.class, repository::loadTasks);
    }
}
