package org.example;

import java.io.IOException;
import java.nio.file.Path;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

public class Main extends Application {
    private static final Path STORAGE_PATH = Path.of(System.getProperty("user.home"), ".todo-desktop-java", "tasks.json");

    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();
        TaskRepository taskRepository = new TaskRepository(STORAGE_PATH);
        TaskService taskService = new TaskService();
        ObservableList<Task> tasks = loadTasksWithFallback(taskRepository);

        Label title = new Label("To-do Application");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: 700;");
        Button clearCompletedBtn = new Button("Clear Completed");
        clearCompletedBtn.setOnAction(event -> {
            if (!taskService.clearCompleted(tasks)) {
                return;
            }
            saveTasksWithAlert(taskRepository, tasks);
        });

        HBox header = new HBox(10, title, clearCompletedBtn);
        HBox.setHgrow(title, Priority.ALWAYS);
        header.setAlignment(Pos.CENTER_LEFT);

        ListView<Task> listView = new ListView<>(tasks);
        listView.setPlaceholder(new Label("No tasks yet. Add your first task."));
        listView.setCellFactory(lv -> new ListCell<>() {
            private final CheckBox completedCheckBox = new CheckBox();
            private final Label taskText = new Label();
            private final Button deleteButton = new Button("Delete");
            private final HBox row = new HBox(10, completedCheckBox, taskText, deleteButton);

            {
                HBox.setHgrow(taskText, Priority.ALWAYS);
                row.setAlignment(Pos.CENTER_LEFT);
            }

            @Override
            protected void updateItem(Task task, boolean empty) {
                super.updateItem(task, empty);
                if (empty || task == null) {
                    setGraphic(null);
                    return;
                }

                taskText.setText(task.getText());
                applyCompletedStyle(taskText, task.isCompleted());
                completedCheckBox.setSelected(task.isCompleted());

                completedCheckBox.setOnAction(event -> {
                    taskService.setCompleted(task, completedCheckBox.isSelected());
                    applyCompletedStyle(taskText, task.isCompleted());
                    saveTasksWithAlert(taskRepository, tasks);
                });

                deleteButton.setOnAction(event -> {
                    taskService.deleteTask(tasks, task);
                    saveTasksWithAlert(taskRepository, tasks);
                });
                setGraphic(row);
            }
        });

        TextField input = new TextField();
        Button addBtn = new Button("Add");

        HBox inputRow = new HBox(10, input, addBtn);
        BorderPane.setMargin(inputRow, new Insets(12, 0, 0, 0));
        inputRow.setAlignment(Pos.CENTER);

        HBox.setHgrow(input, Priority.ALWAYS);
        input.setPromptText("Add a task...");
        root.setPadding(new Insets(16));

        addBtn.setOnAction(event -> addTaskFromInput(input, tasks, taskService, taskRepository));
        input.setOnAction(event -> addTaskFromInput(input, tasks, taskService, taskRepository));

        root.setTop(header);
        root.setCenter(listView);
        root.setBottom(inputRow);

        Scene scene = new Scene(root, 600, 400);
        stage.setTitle("To-Do application");
        stage.setScene(scene);
        stage.show();
    }

    private void addTaskFromInput(
            TextField input,
            ObservableList<Task> tasks,
            TaskService taskService,
            TaskRepository taskRepository) {
        if (!taskService.addTask(tasks, input.getText())) {
            return;
        }

        saveTasksWithAlert(taskRepository, tasks);
        input.clear();
    }

    private ObservableList<Task> loadTasksWithFallback(TaskRepository taskRepository) {
        try {
            return FXCollections.observableArrayList(taskRepository.loadTasks());
        } catch (IOException e) {
            showErrorAlert("Could not load saved tasks. Starting with an empty list.");
            return FXCollections.observableArrayList();
        }
    }

    private void saveTasksWithAlert(TaskRepository taskRepository, ObservableList<Task> tasks) {
        try {
            taskRepository.saveTasks(tasks);
        } catch (IOException e) {
            showErrorAlert("Could not save tasks to disk.");
        }
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Persistence Error");
        alert.setHeaderText("Task storage failed");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void applyCompletedStyle(Label taskLabel, boolean completed) {
        if (completed) {
            taskLabel.setStyle("-fx-strikethrough: true; -fx-text-fill: #6b7280;");
            return;
        }
        taskLabel.setStyle("");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
