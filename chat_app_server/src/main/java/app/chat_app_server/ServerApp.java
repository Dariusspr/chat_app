package app.chat_app_server;

import app.chat_app_server.gui.LogStage;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class ServerApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        stage = LogStage.getInstance();
        stage.show();
    }

    public static void main() {
        launch();
    }
}