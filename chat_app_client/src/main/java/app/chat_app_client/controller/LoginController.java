package app.chat_app_client.controller;

import app.chat_app_client.LocalData;
import app.chat_app_client.MainStage;
import app.chat_app_client.ServerConnection;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import static app.chat_app_client.ServerConnection.MESSAGE_DELIMITER;

public class LoginController {

    @FXML
    private TextField tfName;

    @FXML
    private PasswordField tfPassword;

    @FXML
    private Label labelInvalid;

    @FXML
    void onClickLogin(MouseEvent event){
        String name = tfName.getText();
        String password = tfPassword.getText();
        if (name.isEmpty() || password.isEmpty() || name.contains(MESSAGE_DELIMITER) || password.contains(MESSAGE_DELIMITER)) {
            labelInvalid.setVisible(true);
            return;
        } else {
            labelInvalid.setVisible(false);
        }
        if (ServerConnection.getInstance().login(name, password)) {
            MainStage.getInstance().changeScene(MainStage.ClientScene.CHAT);
        } else {
            labelInvalid.setVisible(true);
        }
    }

    @FXML
    void onClickRegister(MouseEvent event) {
        MainStage.getInstance().changeScene(MainStage.ClientScene.REGISTER);
    }

}
