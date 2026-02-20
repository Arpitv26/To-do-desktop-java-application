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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.animation.ScaleTransition;
import javafx.scene.Node;

public class Main extends Application {
    private static final Path STORAGE_PATH = Path.of(System.getProperty("user.home"), ".todo-desktop-java", "tasks.json");

    @Override
    public void start(Stage stage) {
        StackPane root = new StackPane();
        TaskRepository taskRepository = new TaskRepository(STORAGE_PATH);
        TaskService taskService = new TaskService();
        ObservableList<Task> tasks = loadTasksWithFallback(taskRepository);

        Label title = new Label("Tasks");
        title.getStyleClass().add("app-title");
        StackPane titleIcon = buildTitleIcon();
        HBox titleBox = new HBox(12, titleIcon, title);
        titleBox.getStyleClass().add("title-box");

        Button clearCompletedBtn = new Button("Clear Completed");
        clearCompletedBtn.getStyleClass().add("secondary-button");
        installHoverScale(clearCompletedBtn, 1.03, Duration.millis(140));
        clearCompletedBtn.setOnAction(event -> {
            if (!taskService.clearCompleted(tasks)) {
                return;
            }
            saveTasksWithAlert(taskRepository, tasks);
        });

        HBox headerTop = new HBox(12, titleBox, clearCompletedBtn);
        HBox.setHgrow(titleBox, Priority.ALWAYS);
        headerTop.setAlignment(Pos.CENTER_LEFT);
        headerTop.getStyleClass().add("header-top");

        Label progressLabel = new Label();
        progressLabel.getStyleClass().add("progress-label");

        Region activeDot = new Region();
        activeDot.getStyleClass().addAll("status-dot", "active-dot");
        Label activeLabel = new Label();
        activeLabel.getStyleClass().add("footer-label");
        HBox activeGroup = new HBox(8, activeDot, activeLabel);
        activeGroup.setAlignment(Pos.CENTER);

        Region completedDot = new Region();
        completedDot.getStyleClass().addAll("status-dot", "completed-dot");
        Label completedLabel = new Label();
        completedLabel.getStyleClass().add("footer-label");
        HBox completedGroup = new HBox(8, completedDot, completedLabel);
        completedGroup.setAlignment(Pos.CENTER);

        HBox footer = new HBox(24, activeGroup, completedGroup);
        footer.setAlignment(Pos.CENTER);
        footer.getStyleClass().add("footer");

        VBox header = new VBox(8, headerTop, progressLabel);
        header.getStyleClass().add("header");

        ListView<Task> listView = new ListView<>(tasks);
        Label placeholder = new Label("No tasks yet. Add one to get started!");
        placeholder.getStyleClass().add("empty-placeholder");
        listView.setPlaceholder(placeholder);
        listView.getStyleClass().add("todo-list");
        listView.setCellFactory(lv -> new ListCell<>() {
            private final CheckBox completedCheckBox = new CheckBox();
            private final Label taskText = new Label();
            private final Button deleteButton = new Button("Delete");
            private final HBox row = new HBox(10, completedCheckBox, taskText, deleteButton);
            private final ScaleTransition rowHoverIn = new ScaleTransition(Duration.millis(140), row);
            private final ScaleTransition rowHoverOut = new ScaleTransition(Duration.millis(160), row);

            {
                HBox.setHgrow(taskText, Priority.ALWAYS);
                row.setAlignment(Pos.CENTER_LEFT);
                row.getStyleClass().add("todo-row");
                rowHoverIn.setToX(1.015);
                rowHoverIn.setToY(1.015);
                rowHoverOut.setToX(1.0);
                rowHoverOut.setToY(1.0);
                row.setOnMouseEntered(event -> {
                    rowHoverOut.stop();
                    rowHoverIn.playFromStart();
                    deleteButton.setOpacity(1);
                });
                row.setOnMouseExited(event -> {
                    rowHoverIn.stop();
                    rowHoverOut.playFromStart();
                    deleteButton.setOpacity(0);
                });
                completedCheckBox.getStyleClass().add("todo-check");
                taskText.getStyleClass().add("task-label");
                deleteButton.getStyleClass().add("delete-button");
                installHoverScale(deleteButton, 1.08, Duration.millis(120));
                deleteButton.setOpacity(0);
                deleteButton.setFocusTraversable(false);
            }

            @Override
            protected void updateItem(Task task, boolean empty) {
                super.updateItem(task, empty);
                if (empty || task == null) {
                    setGraphic(null);
                    return;
                }

                taskText.setText(task.getText());
                applyCompletedStyle(row, taskText, task.isCompleted());
                completedCheckBox.setSelected(task.isCompleted());

                completedCheckBox.setOnAction(event -> {
                    taskService.setCompleted(task, completedCheckBox.isSelected());
                    applyCompletedStyle(row, taskText, task.isCompleted());
                    saveTasksWithAlert(taskRepository, tasks);
                    updateStats(tasks, progressLabel, activeLabel, completedLabel, footer, clearCompletedBtn);
                });

                deleteButton.setOnAction(event -> {
                    taskService.deleteTask(tasks, task);
                    saveTasksWithAlert(taskRepository, tasks);
                    updateStats(tasks, progressLabel, activeLabel, completedLabel, footer, clearCompletedBtn);
                });
                setGraphic(row);
            }
        });

        TextField input = new TextField();
        Button addBtn = new Button("Add");

        HBox inputRow = new HBox(10, input, addBtn);
        inputRow.setAlignment(Pos.CENTER);
        inputRow.getStyleClass().add("input-row");

        HBox.setHgrow(input, Priority.ALWAYS);
        input.setPromptText("Add a task...");
        input.getStyleClass().add("task-input");
        addBtn.getStyleClass().add("primary-button");
        installHoverScale(addBtn, 1.04, Duration.millis(140));

        addBtn.setOnAction(event -> {
            addTaskFromInput(input, tasks, taskService, taskRepository);
            updateStats(tasks, progressLabel, activeLabel, completedLabel, footer, clearCompletedBtn);
        });
        input.setOnAction(event -> {
            addTaskFromInput(input, tasks, taskService, taskRepository);
            updateStats(tasks, progressLabel, activeLabel, completedLabel, footer, clearCompletedBtn);
        });

        VBox container = new VBox(18, header, listView, inputRow, footer);
        container.setMaxWidth(640);
        container.getStyleClass().add("app-container");
        VBox.setVgrow(listView, Priority.ALWAYS);
        HBox.setHgrow(input, Priority.ALWAYS);
        inputRow.setFillHeight(true);
        inputRow.setMaxWidth(Double.MAX_VALUE);
        input.setMaxWidth(Double.MAX_VALUE);
        root.getChildren().add(container);
        root.setPadding(new Insets(24));

        updateStats(tasks, progressLabel, activeLabel, completedLabel, footer, clearCompletedBtn);

        Scene scene = new Scene(root, 720, 520);
        scene.getStylesheets().add(Main.class.getResource("/styles/app.css").toExternalForm());
        stage.setTitle("Tasks");
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

    private void applyCompletedStyle(HBox row, Label taskLabel, boolean completed) {
        taskLabel.getStyleClass().remove("completed");
        if (completed) {
            taskLabel.getStyleClass().add("completed");
        }
    }

    private void updateStats(
            ObservableList<Task> tasks,
            Label progressLabel,
            Label activeLabel,
            Label completedLabel,
            HBox footer,
            Button clearCompletedBtn) {
        int completedCount = (int) tasks.stream().filter(Task::isCompleted).count();
        int totalCount = tasks.size();
        int activeCount = totalCount - completedCount;

        progressLabel.setText(completedCount + " of " + totalCount + " completed");
        activeLabel.setText(activeCount + " Active");
        completedLabel.setText(completedCount + " Completed");

        clearCompletedBtn.setDisable(completedCount == 0);
        footer.setVisible(totalCount > 0);
        footer.setManaged(totalCount > 0);
    }

    private StackPane buildTitleIcon() {
        Circle ring = new Circle(18);
        ring.getStyleClass().add("app-icon-circle");

        Label check = new Label("✓");
        check.getStyleClass().add("app-icon-check");

        StackPane icon = new StackPane(ring, check);
        icon.getStyleClass().add("app-icon");
        return icon;
    }

    private void installHoverScale(Node node, double scale, Duration duration) {
        ScaleTransition hoverIn = new ScaleTransition(duration, node);
        hoverIn.setToX(scale);
        hoverIn.setToY(scale);
        ScaleTransition hoverOut = new ScaleTransition(duration, node);
        hoverOut.setToX(1.0);
        hoverOut.setToY(1.0);

        node.setOnMouseEntered(event -> {
            hoverOut.stop();
            hoverIn.playFromStart();
        });
        node.setOnMouseExited(event -> {
            hoverIn.stop();
            hoverOut.playFromStart();
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
