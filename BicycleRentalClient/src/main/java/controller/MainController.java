package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class MainController {

    @FXML
    private Button rentBicycleBtn;

    @FXML
    private Button viewRentalsBtn;

    @FXML
    private Button returnBicycleBtn;

    @FXML
    private Button exitBtn;

    @FXML
    private void initialize() {
        System.out.println("Main controller initialized");
    }

    @FXML
    private void handleRentBicycle() {
        openWindow("/fxml/rent-bicycle.fxml", "Rent Bicycle", 0.5, 0.7);
    }

    @FXML
    private void handleViewRentals() {
        openWindow("/fxml/view-rentals.fxml", "View Rentals", 0.5, 0.7);
    }

    @FXML
    private void handleReturnBicycle() {
        openWindow("/fxml/return-bicycle.fxml", "Return Bicycle", 0.5, 0.7);
    }

    @FXML
    private void handleExit() {
        System.exit(0);
    }

    private void openWindow(String fxmlPath, String title, double widthPercent, double heightPercent) {
        try {
            System.out.println("Opening window: " + title);

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            double width = 700;
            double height = 700;

            try {
                Screen screen = Screen.getPrimary();
                Rectangle2D bounds = screen.getVisualBounds();

                if (bounds.getWidth() > 800 && bounds.getHeight() > 600) {
                    width = Math.max(400, bounds.getWidth() * widthPercent);
                    height = Math.max(500, bounds.getHeight() * heightPercent);

                    width = Math.min(width, 1200);
                    height = Math.min(height, 1000);
                }
            }

            catch (Exception screenException) {
                System.err.println("Could not get screen dimensions for " + title + ", using default sizes");
                screenException.printStackTrace();
            }

            Scene scene = new Scene(root, width, height);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());

            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(scene);
            stage.setResizable(true);
            stage.setMinWidth(400);
            stage.setMinHeight(300);

            try {
                Screen screen = Screen.getPrimary();
                Rectangle2D bounds = screen.getVisualBounds();
                stage.setX((bounds.getWidth() - width) / 2);
                stage.setY((bounds.getHeight() - height) / 2);
            }

            catch (Exception centerException) {
                System.err.println("Could not center window " + title);
            }

            stage.show();
            System.out.println("Window opened successfully: " + title); // Debug output

        }

        catch (Exception e) {
            System.err.println("Error opening window: " + title);
            e.printStackTrace();
        }
    }
}