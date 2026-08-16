package com.hastane.htds;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                Objects.requireNonNull(getClass().getResource("/com/hastane/htds/view/login.fxml")),
                com.hastane.htds.util.LanguageManager.getBundle()
        );

        Parent root = loader.load();

        javafx.geometry.Rectangle2D bounds = javafx.stage.Screen.getPrimary().getVisualBounds();
        primaryStage.setX(bounds.getMinX());
        primaryStage.setY(bounds.getMinY());
        primaryStage.setWidth(bounds.getWidth());
        primaryStage.setHeight(bounds.getHeight());
        
        Scene scene = new Scene(root, bounds.getWidth(), bounds.getHeight());

        scene.getStylesheets().add(
                Objects.requireNonNull(getClass().getResource("/com/hastane/htds/view/login.css")).toExternalForm()
        );
        scene.getStylesheets().add(
                Objects.requireNonNull(getClass().getResource("/com/hastane/htds/view/touch-friendly.css")).toExternalForm()
        );

        primaryStage.setTitle("Hastane Arıza Takip Sistemi");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}