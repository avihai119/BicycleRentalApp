module com.client.bicyclerentalclient {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;


    opens com.client.bicyclerentalclient to javafx.fxml;
    exports com.client.bicyclerentalclient;
}