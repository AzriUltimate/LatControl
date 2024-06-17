package com.example.latcontrol;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        LongitudeControl longitudeField = new LongitudeControl();
        LatitudeControl latitudeField = new LatitudeControl();
        Button addButton = new Button("Добавить");
        Label latitudeLabel = new Label("Широта");
        Label longitudeLabel = new Label("Долгота");
        VBox pane = new VBox();
        addButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println(latitudeField.getOutput());
                System.out.println(longitudeField.getOutput());
            }
        });

        pane.getChildren().add(0, latitudeLabel);
        pane.getChildren().add(1, latitudeField);
        pane.getChildren().add(2, longitudeLabel);
        pane.getChildren().add(3, longitudeField);
        pane.getChildren().add(4, addButton);

        Scene scene = new Scene(pane, 500, 300);
        stage.setScene(scene);
        stage.show();
    }
}