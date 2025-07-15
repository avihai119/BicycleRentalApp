package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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
        openWindow("/fxml/rent-bicycle.fxml", "Rent Bicycle", 500, 400);
    }

    @FXML
    private void handleViewRentals() {
        openWindow("/fxml/view-rentals.fxml", "View Rentals", 700, 500);
    }

    @FXML
    private void handleReturnBicycle() {
        openWindow("/fxml/return-bicycle.fxml", "Return Bicycle", 400, 300);
    }

    @FXML
    private void handleExit() {
        System.exit(0);
    }

    private void openWindow(String fxmlPath, String title, int width, int height) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Scene scene = new Scene(loader.load(), width, height);

            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}