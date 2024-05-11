package app.chat_app_client.controller;

import app.chat_app_client.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import java.awt.datatransfer.StringSelection;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.net.URL;
import java.util.ResourceBundle;

import static app.chat_app_client.LocalData.GLOBAL_GROUP;

public class ChatController implements Initializable {

    @FXML
    private TextArea taChat;

    @FXML
    private TableView<Room> tbGroups;

    @FXML
    private Label tfGroupTitle;

    @FXML
    private TextField tfMessage;

    @FXML
    private TableColumn<Room, String> colGroups;

    @FXML
    void onClickChangeGroup(MouseEvent event) {
        Room group = tbGroups.getSelectionModel().getSelectedItem();
        if (group == null)
            return;
        LocalData.getInstance().changeRoom(group);
        ServerConnection.getInstance().updateGroupMessages(group.getId());
        tfGroupTitle.setText(group.getName());
        taChat.clear();
        for (Message msg : LocalData.getInstance().getCurrentRoom().getMessages()) {
            addMessage(msg);
        }

    }

    @FXML
    void onClickNewGroup(MouseEvent event) {
        SecondaryStage.getInstance().show();
    }

    @FXML
    void onClickSend(MouseEvent event) {
        String msg = tfMessage.getText();
        if (msg.isEmpty() || msg.contains("@@")) {
            return;
        }
        Message message = new Message(LocalData.getInstance().getUsername(), msg);
        if (ServerConnection.getInstance().sendMessage(message)) {
            LocalData.getInstance().getCurrentRoom().addMessage(message);
            addMessage(message);
            tfMessage.clear();
        }
    }

    public void addMessage(Message msg) {
        taChat.appendText(msg.MessageFormat());
        taChat.appendText("\n");
    }

    public void updateRoomList() {
        tbGroups.getItems().clear();
        new Thread(() -> LocalData.getInstance().updateRooms()).start();
        while (LocalData.getInstance().notReadyForMainChat()) {Thread.onSpinWait();}
        tbGroups.setItems(LocalData.getInstance().getRooms());

        taChat.clear();

        for (Message msg : LocalData.getInstance().getCurrentRoom().getMessages()) {
            addMessage(msg);
        }
    }

    @FXML
    private void onClickDeleteGroup() {
        Room group = tbGroups.getSelectionModel().getSelectedItem();
        if (group.getName().equals(GLOBAL_GROUP)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Can't leave global group");
            alert.showAndWait();
            return;
        }
        ServerConnection.getInstance().leaveGroup(group);
        updateRoomList();
    }

    @FXML
    private void onClickGetId() {
        Room room = LocalData.getInstance().getCurrentRoom();
        if (room.getType().equalsIgnoreCase("small")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Can't get group id.");
            alert.showAndWait();
            return;
        }
        StringSelection stringSelection = new StringSelection(room.getId());
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);

    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colGroups.setCellValueFactory(new PropertyValueFactory<>("name"));
        updateRoomList();
        tfGroupTitle.setText(LocalData.getInstance().getCurrentRoom().getName());
        LocalData.getInstance().setChatController(this);
    }
}
