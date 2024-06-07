package com.example.latcontrol;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        LatControl latitudeField = new LatControl();
        latitudeField.setLayoutX(10.0);
        latitudeField.setLayoutY(10.0);

        Pane pane = new Pane();

        pane.getChildren().add(latitudeField);

        Scene scene = new Scene(pane, 500, 300);
        stage.setScene(scene);
        stage.show();
    }
}