package app.chat_app_client;

import static app.chat_app_client.ServerConnection.MESSAGE_DELIMITER;

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

    public String MessageFormat() {
        if (LocalData.getInstance().getUsername().equals(sender)) {
            return "ME: " + content;
        }
        return sender + ": " + content;
    }

    public static Message parse(String line) {
        String[] splitLine = line.split(MESSAGE_DELIMITER);
        if (splitLine.length == 2) {
            return new Message(splitLine[0], splitLine[1]);
        }
        return null;
    }
}
