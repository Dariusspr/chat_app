package app.chat_app_server.gui;

import app.chat_app_server.ServerApp;
import app.chat_app_server.Service;
import app.chat_app_server.fileIO.DataSaver;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.io.IOException;

public class LogStage extends Stage {
    private static LogStage stage;
    private LogController controller;
    private LogStage() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(ServerApp.class.getResource("log-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            this.setTitle("Server log");
            this.setScene(scene);
            this.setResizable(false);
            controller = fxmlLoader.getController();
            this.setOnCloseRequest(event -> {
                event.consume();
                quit();
            });
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static LogStage getInstance() {
        return stage == null ? stage = new LogStage() : stage;
    }

    private void quit() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit");
        alert.setHeaderText("You're about to turn off the server");
        alert.setContentText("Are you sure?");

        var result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Service.getInstance().stop();
            DataSaver dataSaver = new DataSaver();
            dataSaver.saveUsers();
            dataSaver.saveGroups();

            this.close();
        }
    }

    public LogController getController() {
        return controller;
    }
}


