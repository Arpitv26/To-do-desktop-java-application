package org.example;

import javafx.application.Application;
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

        ListView<String> listView = new ListView<>();
        TextField input = new TextField();
        Button addBtn = new Button("Add");

        //bottom row:
        HBox inputRow = new HBox(10, input, addBtn); // 10 = spacing
        BorderPane.setMargin(inputRow, new Insets(12, 0, 0, 0));
        inputRow.setAlignment(javafx.geometry.Pos.CENTER);


        //make text grow when window grows + padding:
        HBox.setHgrow(input, Priority.ALWAYS);
        input.setPromptText("Add a task...");
        root.setPadding(new Insets(16));
        inputRow.setAlignment(Pos.CENTER);


        //set everything inito the BorderPane
        root.setTop(title);
        root.setCenter(listView);
        root.setBottom(inputRow);


        Scene scene = new Scene(root, 600, 400);
        stage.setTitle("To-Do application");
        stage.setScene(scene);
        stage.show();


    }

    public static void main(String[] args) {
        launch(args);
    }
}
