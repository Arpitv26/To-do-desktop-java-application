package org.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class TaskServiceTest {
    private final TaskService taskService = new TaskService();

    @Test
    void addTaskRejectsBlankInput() {
        List<Task> tasks = new ArrayList<>();

        boolean added = taskService.addTask(tasks, "   ");

        assertFalse(added);
        assertTrue(tasks.isEmpty());
    }

    @Test
    void addTaskStoresTrimmedText() {
        List<Task> tasks = new ArrayList<>();

        boolean added = taskService.addTask(tasks, "  Buy milk  ");

        assertTrue(added);
        assertEquals(1, tasks.size());
        assertEquals("Buy milk", tasks.get(0).getText());
        assertFalse(tasks.get(0).isCompleted());
    }

    @Test
    void setCompletedUpdatesState() {
        Task task = new Task("Read chapter");

        taskService.setCompleted(task, true);
        assertTrue(task.isCompleted());

        taskService.setCompleted(task, false);
        assertFalse(task.isCompleted());
    }

    @Test
    void deleteTaskRemovesOnlyTargetTask() {
        List<Task> tasks = new ArrayList<>();
        Task first = new Task("First");
        Task second = new Task("Second");
        tasks.add(first);
        tasks.add(second);

        taskService.deleteTask(tasks, first);

        assertEquals(1, tasks.size());
        assertEquals("Second", tasks.get(0).getText());
    }
}
