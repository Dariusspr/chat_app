package app.chat_app_server.gui;

import app.chat_app_server.ServerApp;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.io.IOException;

public class LogStage extends Stage {
    private static LogStage stage;
    private LogController serverController;
    private LogStage() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(ServerApp.class.getResource("log-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            serverController = fxmlLoader.getController();
            stage.setTitle("Server log");
            stage.setScene(scene);
            stage.setResizable(false);

            stage.setOnCloseRequest(event -> {
                event.consume();
                closeStage();
            });
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static LogStage getInstance() {
        return stage == null ? stage = new LogStage() : stage;
    }

    private void closeStage() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit");
        alert.setHeaderText("You're about to turn off the server");
        alert.setContentText("Are you sure?");

        var result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            this.close();
        }
    }
}


