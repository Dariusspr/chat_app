package app.chat_app_client.controller;

import app.chat_app_client.LocalData;
import app.chat_app_client.SecondaryStage;
import app.chat_app_client.ServerConnection;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class NewGroupController {


    @FXML
    private Button btnCreate;

    @FXML
    private Button btnLarge;

    @FXML
    private Button btnSmall;

    @FXML
    private Button btnBack;

    @FXML
    private Label labelInfo;

    @FXML
    private Button btnExisting;

    @FXML
    private TextField tfInput;

    private boolean typeLarge = true;
    private boolean joinGroup = false;

    @FXML
    void onClickCreate(MouseEvent event) {
        String name = tfInput.getText();
        if (name.isEmpty() || name.contains("@@") || name.contains(",") || name.contains("#!")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Invalid data");
            alert.showAndWait();
            return;
        }

        if (joinGroup) {
            if (ServerConnection.getInstance().joinGroup(name)) {
                flipVisibility();
                tfInput.clear();
                joinGroup = false;
                SecondaryStage.getInstance().close();
            }


            return;
        }

        if (ServerConnection.getInstance().createGroup(name, typeLarge ? "large" : "small")) {
            flipVisibility();
            tfInput.clear();
            SecondaryStage.getInstance().close();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Invalid data");
            alert.showAndWait();
        }
    }

    @FXML
    void onClickLarge(MouseEvent event) {
        typeLarge = true;
        joinGroup = false;
        tfInput.setPromptText("name");
        btnCreate.setText("create");
        flipVisibility();
    }

    private void flipVisibility() {
        btnCreate.setVisible(!btnCreate.isVisible());
        btnLarge.setVisible(!btnLarge.isVisible());
        btnSmall.setVisible(!btnSmall.isVisible());
        btnExisting.setVisible(!btnExisting.isVisible());
        labelInfo.setVisible(!labelInfo.isVisible());
        tfInput.setVisible(!tfInput.isVisible());
        btnBack.setVisible(!btnBack.isVisible());
    }

    @FXML
    void onClickSmall(MouseEvent event) {
        typeLarge = false;
        joinGroup = false;
        btnCreate.setText("create");
        tfInput.setPromptText("user name");
        flipVisibility();

    }

    @FXML
    void onClickExisting(MouseEvent event) {
        joinGroup = true;
        tfInput.setPromptText("id");
        btnCreate.setText("Join");
        flipVisibility();

    }

    @FXML
    void onClickBack() {
        flipVisibility();
    }

}
