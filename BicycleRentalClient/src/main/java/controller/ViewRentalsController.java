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

import java.time.format.DateTimeFormatter;
import java.util.List;

public class ViewRentalsController {

    @FXML
    private TableView<Rental> rentalsTable;

    @FXML
    private TableColumn<Rental, Long> rentalIdColumn;

    @FXML
    private TableColumn<Rental, Long> userIdColumn;

    @FXML
    private TableColumn<Rental, Long> bicycleIdColumn;

    @FXML
    private TableColumn<Rental, String> startTimeColumn;

    @FXML
    private TableColumn<Rental, String> statusColumn;

    @FXML
    private Button refreshBtn;

    @FXML
    private Button closeBtn;

    @FXML
    private Label statusLabel;

    private ServerCommunicationService serverService;
    private ObservableList<Rental> rentals;

    @FXML
    private void initialize() {
        serverService = new ServerCommunicationService();
        rentals = FXCollections.observableArrayList();

        rentalIdColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleLongProperty(cellData.getValue().getRentalId()).asObject());

        userIdColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleLongProperty(cellData.getValue().getUserId()).asObject());

        bicycleIdColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleLongProperty(cellData.getValue().getBicycleId()).asObject());

        startTimeColumn.setCellValueFactory(cellData -> {
            String formattedTime = cellData.getValue().getStartTime()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            return new javafx.beans.property.SimpleStringProperty(formattedTime);
        });

        statusColumn.setCellValueFactory(cellData -> {
            String status = cellData.getValue().isActive() ? "ACTIVE" : "COMPLETED";
            return new javafx.beans.property.SimpleStringProperty(status);
        });

        rentalsTable.setRowFactory(tv -> {
            TableRow<Rental> row = new TableRow<>();
            row.itemProperty().addListener((obs, oldRental, newRental) -> {
                if (newRental == null) {
                    row.setStyle("");
                }

                else if (newRental.isActive()) {
                    row.setStyle("-fx-background-color: #e8f5e8;");
                }

                else {
                    row.setStyle("-fx-background-color: #f5f5f5;");
                }
            });

            return row;
        });

        rentalsTable.setItems(rentals);

        loadActiveRentals();
    }

    @FXML
    private void handleRefresh() {
        loadActiveRentals();
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) closeBtn.getScene().getWindow();
        stage.close();
    }

    private void loadActiveRentals() {
        try {
            Response<List<Rental>> response = serverService.sendListRequest("rental/active", null, Rental.class);

            if (response.isSuccess()) {
                rentals.clear();
                rentals.addAll(response.getBody());
                statusLabel.setText("Active rentals loaded: " + rentals.size());
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