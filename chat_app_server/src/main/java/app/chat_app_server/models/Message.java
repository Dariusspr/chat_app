package app.chat_app_server.models;

import static app.chat_app_server.fileIO.DataSaver.MESSAGE_DELIMITER;

public class Message {
    private String sender;
    private String content;

    public Message(String sender, String content) {
        this.sender = sender;
        this.content = content;
    }


    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String format() {
        return sender + MESSAGE_DELIMITER + content;
    }

    public static Message parse(String line) {
        String[] splitLine = line.split(MESSAGE_DELIMITER);
        if (splitLine.length == 2) {
            return new Message(splitLine[0], splitLine[1]);
        }
        return null;
    }

}
