package app.chat_app_client;

import app.chat_app_client.controller.ChatController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.io.IOException;

public class MainStage extends Stage {
    private static MainStage mainStage;

    public void changeScene(ClientScene sceneType) {
        String filePath = null;
        switch (sceneType) {
            case LOGIN -> {
                filePath = "login-view.fxml";
            }
            case REGISTER -> {
                filePath = "register-view.fxml";
            }
            case CHAT -> {
                filePath = "chat-view.fxml";
            }
        }

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(ClientApp.class.getResource(filePath));
            Scene scene = new Scene(fxmlLoader.load());
            this.setTitle("Chat app");
            this.setScene(scene);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private MainStage() {
        changeScene(ClientScene.LOGIN);
        this.setResizable(false);

        this.setOnCloseRequest(event -> {
            event.consume();
            quit();
        });
    }

    public static MainStage getInstance() {
        return mainStage == null ? mainStage = new MainStage() : mainStage;
    }

    private void quit() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit");
        alert.setHeaderText("You're about to exit");
        alert.setContentText("Are you sure?");

        var result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            this.close();
            ServerConnection.getInstance().logout();
            ServerConnection.getInstance().stop();
            System.exit(0);
        }
    }

    public enum ClientScene {
        LOGIN, REGISTER, CHAT
    }
}

