package app.chat_app_client;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SecondaryStage extends Stage {
    private static SecondaryStage stage;
    private SecondaryStage() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(ClientApp.class.getResource("groupCreation-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            this.setTitle("New Room");
            this.setScene(scene);
            this.setResizable(false);
            this.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static SecondaryStage getInstance() {
        return stage == null ? stage = new SecondaryStage() : stage;
    }
}
