module app.chat_app_client {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.datatransfer;
    requires java.desktop;


    opens app.chat_app_client to javafx.fxml;
    exports app.chat_app_client;
    exports app.chat_app_client.controller;
    opens app.chat_app_client.controller to javafx.fxml;
}