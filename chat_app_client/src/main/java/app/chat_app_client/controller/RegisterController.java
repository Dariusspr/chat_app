package app.chat_app_client.controller;

import app.chat_app_client.ClientLauncher;
import app.chat_app_client.MainStage;
import app.chat_app_client.ServerConnection;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import static app.chat_app_client.ServerConnection.MESSAGE_DELIMITER;

public class RegisterController {
    @FXML
    private TextField tfName;

    @FXML
    private PasswordField tfPassword;

    @FXML
    private Label labelInvalid;

    @FXML
    void onClickBack() {
        MainStage.getInstance().changeScene(MainStage.ClientScene.LOGIN);
    }

    @FXML
    void onClickRegister() {
        String name = tfName.getText();
        String password = tfPassword.getText();
        if (name.isEmpty() || password.isEmpty() || name.contains(MESSAGE_DELIMITER) || password.contains(MESSAGE_DELIMITER)) {
            labelInvalid.setVisible(true);
            return;
        } else {
            labelInvalid.setVisible(false);
        }

        if (ServerConnection.getInstance().sendRegister(name, password)) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("User successfully registered");
            alert.setHeaderText(null);
            alert.setContentText("Account has been created");
            alert.showAndWait();
            onClickBack();
        } else {
            labelInvalid.setVisible(true);
        }
    }
}
