package app.chat_app_client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ClientApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        stage = MainStage.getInstance();
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}