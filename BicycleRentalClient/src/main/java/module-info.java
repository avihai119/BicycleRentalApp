module com.client.bicyclerentalclient {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;

    opens com.client.bicyclerentalclient to javafx.fxml;
    opens controller to javafx.fxml;
    opens model to com.google.gson;
    opens service to com.google.gson;

    exports com.client.bicyclerentalclient;
    exports controller;
    exports model;
    exports service;
    exports util;
}