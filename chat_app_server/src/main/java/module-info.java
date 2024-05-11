module app.chat_app_server {
    requires javafx.controls;
    requires javafx.fxml;

    opens app.chat_app_server to javafx.fxml;
    opens app.chat_app_server.gui to javafx.fxml;
    exports app.chat_app_server.gui;
    exports app.chat_app_server;
}