package controller;

import model.Rental;
import model.Response;
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

public class ReturnBicycleController {

    @FXML
    private ComboBox<Rental> activeRentalsComboBox;

    @FXML
    private Button returnBtn;

    @FXML
    private Button cancelBtn;

    @FXML
    private Button refreshBtn;

    @FXML
    private Label statusLabel;

    @FXML
    private TextField userIdField;

    @FXML
    private TextField bicycleIdField;

    @FXML
    private RadioButton selectFromListRadio;

    @FXML
    private RadioButton enterManuallyRadio;

    @FXML
    private ToggleGroup inputMethodGroup;

    private ServerCommunicationService serverService;
    private ObservableList<Rental> activeRentals;

    @FXML
    private void initialize() {
        serverService = new ServerCommunicationService();
        activeRentals = FXCollections.observableArrayList();

        inputMethodGroup = new ToggleGroup();
        selectFromListRadio.setToggleGroup(inputMethodGroup);
        enterManuallyRadio.setToggleGroup(inputMethodGroup);
        selectFromListRadio.setSelected(true);

        activeRentalsComboBox.setItems(activeRentals);

        activeRentalsComboBox.setCellFactory(param -> new ListCell<Rental>() {
            @Override
            protected void updateItem(Rental rental, boolean empty) {
                super.updateItem(rental, empty);
                if (empty || rental == null) {
                    setText(null);
                } else {
                    setText("User: " + rental.getUserId() + " - Bicycle: " + rental.getBicycleId() +
                            " - Rental ID: " + rental.getRentalId());
                }
            }
        });

        activeRentalsComboBox.setButtonCell(new ListCell<Rental>() {
            @Override
            protected void updateItem(Rental rental, boolean empty) {
                super.updateItem(rental, empty);
                if (empty || rental == null) {
                    setText(null);
                }

                else {
                    setText("User: " + rental.getUserId() + " - Bicycle: " + rental.getBicycleId() +
                            " - Rental ID: " + rental.getRentalId());
                }
            }
        });

        inputMethodGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            updateInputMethodUI();
        });

        loadActiveRentals();
        updateInputMethodUI();
    }

    private void updateInputMethodUI() {
        boolean useList = selectFromListRadio.isSelected();
        activeRentalsComboBox.setDisable(!useList);
        userIdField.setDisable(useList);
        bicycleIdField.setDisable(useList);

        if (!useList) {
            activeRentalsComboBox.getSelectionModel().clearSelection();
        }

        else {
            userIdField.clear();
            bicycleIdField.clear();
        }
    }

    @FXML
    private void handleReturn() {
        try {
            long bicycleId;

            if (selectFromListRadio.isSelected()) {
                Rental selectedRental = activeRentalsComboBox.getSelectionModel().getSelectedItem();
                if (selectedRental == null) {
                    AlertUtil.showError("Selection Error", "Please select a rental to return");
                    return;
                }
                bicycleId = selectedRental.getBicycleId();

            }

            else {
                String bicycleIdText = bicycleIdField.getText().trim();
                if (bicycleIdText.isEmpty()) {
                    AlertUtil.showError("Input Error", "Please enter a bicycle ID");
                    return;
                }
                bicycleId = Long.parseLong(bicycleIdText);
            }

            if (!AlertUtil.showConfirmation("Confirm Return",
                    "Are you sure you want to return bicycle ID: " + bicycleId + "?")) {
                return;
            }

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("bicycleId", bicycleId);

            Response<String> response = serverService.sendStringRequest("rental/return", requestBody);

            if (response.isSuccess()) {
                AlertUtil.showSuccess("Success", "Bicycle returned successfully!");
                statusLabel.setText("Bicycle returned successfully!");

                loadActiveRentals();

                activeRentalsComboBox.getSelectionModel().clearSelection();
                userIdField.clear();
                bicycleIdField.clear();

            }

            else {
                AlertUtil.showError("Return Failed", response.getMessage());
                statusLabel.setText("Return failed: " + response.getMessage());
            }

        }

        catch (NumberFormatException e) {
            AlertUtil.showError("Input Error", "Bicycle ID must be a valid number");
        }
    }

    @FXML
    private void handleRefresh() {
        loadActiveRentals();
        statusLabel.setText("Active rentals refreshed");
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) cancelBtn.getScene().getWindow();
        stage.close();
    }

    private void loadActiveRentals() {
        try {
            Response<List<Rental>> response = serverService.sendListRequest("rental/active", null, Rental.class);

            if (response.isSuccess()) {
                activeRentals.clear();
                activeRentals.addAll(response.getBody());
                statusLabel.setText("Active rentals loaded: " + activeRentals.size());
            }

            else {
                AlertUtil.showError("Load Error", "Failed to load active rentals: " + response.getMessage());
                statusLabel.setText("Failed to load rentals");
            }

        }

        catch (Exception e) {
            AlertUtil.showError("Connection Error", "Failed to connect to server: " + e.getMessage());
            statusLabel.setText("Connection failed");
        }
    }
}