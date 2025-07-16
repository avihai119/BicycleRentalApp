package com.client.bicyclerentalclient;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class HelloApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
        Parent root = loader.load();

        double width = 500;
        double height = 700;

        try {
            Screen screen = Screen.getPrimary();
            Rectangle2D bounds = screen.getVisualBounds();

            if (bounds.getWidth() > 800 && bounds.getHeight() > 600) {
                width = Math.max(500, bounds.getWidth() * 0.35);
                height = Math.max(700, bounds.getHeight() * 0.8);

                width = Math.min(width, 800);
                height = Math.min(height, 1000);
            }
        }

        catch (Exception e) {
            System.err.println("Could not get screen dimensions, using default sizes");
            e.printStackTrace();
        }

        Scene scene = new Scene(root, width, height);
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());

        primaryStage.setTitle("Bicycle Rental System");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.setMinWidth(450);
        primaryStage.setMinHeight(650);

        try {
            Screen screen = Screen.getPrimary();
            Rectangle2D bounds = screen.getVisualBounds();
            primaryStage.setX((bounds.getWidth() - width) / 2);
            primaryStage.setY((bounds.getHeight() - height) / 2);
        }

        catch (Exception e) {
            System.err.println("Could not center window, using default position");
        }

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}