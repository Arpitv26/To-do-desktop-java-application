package org.example;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();

        Label title = new Label("To-do Application");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: 700;");

        ObservableList<Task> tasks = FXCollections.observableArrayList();
        ListView<Task> listView = new ListView<>(tasks);
        listView.setPlaceholder(new Label("No tasks yet. Add your first task."));

        TextField input = new TextField();
        Button addBtn = new Button("Add");

        HBox inputRow = new HBox(10, input, addBtn);
        BorderPane.setMargin(inputRow, new Insets(12, 0, 0, 0));
        inputRow.setAlignment(Pos.CENTER);

        HBox.setHgrow(input, Priority.ALWAYS);
        input.setPromptText("Add a task...");
        root.setPadding(new Insets(16));

        addBtn.setOnAction(event -> addTaskFromInput(input, tasks));
        input.setOnAction(event -> addTaskFromInput(input, tasks));

        root.setTop(title);
        root.setCenter(listView);
        root.setBottom(inputRow);

        Scene scene = new Scene(root, 600, 400);
        stage.setTitle("To-Do application");
        stage.setScene(scene);
        stage.show();
    }

    private void addTaskFromInput(TextField input, ObservableList<Task> tasks) {
        String text = input.getText() == null ? "" : input.getText().trim();
        if (text.isEmpty()) {
            return;
        }

        tasks.add(new Task(text));
        input.clear();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
