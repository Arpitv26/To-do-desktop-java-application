package org.example;

import java.util.List;

public class TaskService {
    public boolean addTask(List<Task> tasks, String rawText) {
        if (rawText == null) {
            return false;
        }

        String text = rawText.trim();
        if (text.isEmpty()) {
            return false;
        }

        tasks.add(new Task(text));
        return true;
    }

    public void setCompleted(Task task, boolean completed) {
        if (task == null) {
            return;
        }
        task.setCompleted(completed);
    }

    public void deleteTask(List<Task> tasks, Task task) {
        if (tasks == null || task == null) {
            return;
        }
        tasks.remove(task);
    }

    public boolean clearCompleted(List<Task> tasks) {
        if (tasks == null) {
            return false;
        }
        return tasks.removeIf(Task::isCompleted);
    }
}
