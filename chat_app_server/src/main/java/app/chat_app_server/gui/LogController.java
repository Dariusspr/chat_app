package app.chat_app_server.gui;

import app.chat_app_server.utils.CurrentTime;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

public class LogController {
    @FXML
    private TextArea taServerInfo;

    public void createMessage(String messageInfo) {
        taServerInfo.appendText(CurrentTime.getCurrentTimeFormatted() + " " + messageInfo + "\n");
    }
}
