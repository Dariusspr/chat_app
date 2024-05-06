package app.chat_app_client;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class LoginController {

    @FXML
    private TextField tfName;

    @FXML
    private PasswordField tfPassword;

    @FXML
    void onClickLogin(MouseEvent event) {
        String username = tfName.getText();
        String password = tfPassword.getText();

    }

    @FXML
    void onClickRegister(MouseEvent event) {
        MainStage.getInstance().changeScene(MainStage.ClientScene.REGISTER);
    }

}
