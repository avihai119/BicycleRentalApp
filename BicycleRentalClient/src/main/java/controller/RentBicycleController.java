package controller;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import model.Bicycle;
import model.Response;
import model.Rental;
import service.ServerCommunicationService;
import util.AlertUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RentBicycleController {

    @FXML
    private TextField userIdField;

    @FXML
    private ComboBox<Bicycle> bicycleComboBox;

    @FXML
    private Button rentBtn;

    @FXML
    private Button cancelBtn;

    @FXML
    private Button refreshBtn;

    @FXML
    private Label statusLabel;

    private ServerCommunicationService serverService;
    private ObservableList<Bicycle> availableBicycles;

    @FXML
    private void initialize() {
        serverService = new ServerCommunicationService();
        availableBicycles = FXCollections.observableArrayList();
        bicycleComboBox.setItems(availableBicycles);

        loadAvailableBicycles();

        bicycleComboBox.setCellFactory(param -> new ListCell<Bicycle>() {
            @Override
            protected void updateItem(Bicycle bicycle, boolean empty) {
                super.updateItem(bicycle, empty);
                if (empty || bicycle == null) {
                    setText(null);
                }

                else {
                    setText(bicycle.toString());
                }
            }
        });

        bicycleComboBox.setButtonCell(new ListCell<Bicycle>() {
            @Override
            protected void updateItem(Bicycle bicycle, boolean empty) {
                super.updateItem(bicycle, empty);
                if (empty || bicycle == null) {
                    setText(null);
                }

                else {
                    setText(bicycle.toString());
                }
            }
        });
    }

    @FXML
    private void handleRent() {
        String userIdText = userIdField.getText().trim();
        Bicycle selectedBicycle = bicycleComboBox.getSelectionModel().getSelectedItem();

        if (userIdText.isEmpty()) {
            AlertUtil.showError("Input Error", "Please enter a user ID");
            return;
        }

        if (selectedBicycle == null) {
            AlertUtil.showError("Selection Error", "Please select a bicycle");
            return;
        }

        try {
            long userId = Long.parseLong(userIdText);

            if (userHasActiveRental(userId)) {
                AlertUtil.showWarning("Rental Restriction",
                        "You already have an active rental. Please return your current bicycle before renting another one.");
                return;
            }

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("bicycleId", selectedBicycle.getId());
            requestBody.put("userId", userId);

            Response<Bicycle> response = serverService.sendObjectRequest("rental/rent", requestBody, Bicycle.class);

            if (response.isSuccess()) {
                AlertUtil.showSuccess("Success", "Bicycle rented successfully!");
                statusLabel.setText("Bicycle rented successfully!");

                loadAvailableBicycles();

                userIdField.clear();
                bicycleComboBox.getSelectionModel().clearSelection();

            }

            else {
                AlertUtil.showError("Rental Failed", response.getMessage());
                statusLabel.setText("Rental failed: " + response.getMessage());
            }

        }

        catch (NumberFormatException e) {
            AlertUtil.showError("Input Error", "User ID must be a valid number");
        }
    }

    private boolean userHasActiveRental(long userId) {
        try {
            Response<List<Rental>> response = serverService.sendListRequest("rental/active", null, Rental.class);

            if (response.isSuccess()) {
                List<Rental> activeRentals = response.getBody();
                return activeRentals.stream()
                        .anyMatch(rental -> rental.getUserId() == userId);
            }

        }

        catch (Exception e) {
            System.err.println("Error checking active rentals: " + e.getMessage());
        }

        return false;
    }

    private void openWindow(String fxmlPath, String title, double widthPercent, double heightPercent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Screen screen = Screen.getPrimary();
            Rectangle2D bounds = screen.getVisualBounds();

            double width = bounds.getWidth() * widthPercent;
            double height = bounds.getHeight() * heightPercent;

            Scene scene = new Scene(root, width, height);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());

            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(scene);
            stage.setResizable(true);

            stage.setMinWidth(bounds.getWidth() * 0.3); // 30% of screen width
            stage.setMinHeight(bounds.getHeight() * 0.4); // 40% of screen height

            stage.setX((bounds.getWidth() - width) / 2);
            stage.setY((bounds.getHeight() - height) / 2);

            stage.show();

        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRefresh() {
        loadAvailableBicycles();
        statusLabel.setText("Bicycle list refreshed");
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) cancelBtn.getScene().getWindow();
        stage.close();
    }

    private void loadAvailableBicycles() {
        try {
            Response<List<Bicycle>> response = serverService.sendListRequest("bicycle/available", null, Bicycle.class);

            if (response.isSuccess()) {
                availableBicycles.clear();
                availableBicycles.addAll(response.getBody());
                statusLabel.setText("Available bicycles: " + availableBicycles.size());
            }

            else {
                AlertUtil.showError("Load Error", "Failed to load available bicycles: " + response.getMessage());
                statusLabel.setText("Failed to load bicycles");
            }

        }

        catch (Exception e) {
            AlertUtil.showError("Connection Error", "Failed to connect to server: " + e.getMessage());
            statusLabel.setText("Connection failed");
        }
    }
}