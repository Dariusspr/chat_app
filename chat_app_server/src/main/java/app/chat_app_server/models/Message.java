package app.chat_app_server.models;

public class Message {
    private String sender;
    private String content;

    Message(String sender, String content) {
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

}
