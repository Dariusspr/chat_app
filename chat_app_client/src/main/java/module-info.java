module app.chat_app_client {
    requires javafx.controls;
    requires javafx.fxml;


    opens app.chat_app_client to javafx.fxml;
    exports app.chat_app_client;
}