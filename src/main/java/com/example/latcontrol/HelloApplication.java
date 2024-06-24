package com.example.latcontrol;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        LatitudeControl latitudeField = new LatitudeControl();
        LongitudeControl longitudeField = new LongitudeControl();
        Button addButton = new Button("Добавить");
        Label latitudeLabel = new Label("Широта");
        Label longitudeLabel = new Label("Долгота");
        Label currentLatitudeValues = new Label();
        Label currentLongitudeValues = new Label();
        VBox pane = new VBox();

        currentLatitudeValues.textProperty().bind(latitudeField.valueForUserProperty().asString());
        currentLongitudeValues.textProperty().bind(longitudeField.valueForUserProperty().asString());

        //TODO: Переделать в цикл for по массиву данных
        pane.getChildren().add(0, latitudeLabel);
        pane.getChildren().add(1, latitudeField);
        pane.getChildren().add(2, longitudeLabel);
        pane.getChildren().add(3, longitudeField);
        pane.getChildren().add(4, addButton);
        pane.getChildren().add(5, currentLatitudeValues);
        pane.getChildren().add(6, currentLongitudeValues);

        Scene scene = new Scene(pane, 500, 300);
        stage.setScene(scene);
        pane.requestFocus();
        stage.show();
    }

}