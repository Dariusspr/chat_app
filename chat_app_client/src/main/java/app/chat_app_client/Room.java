package app.chat_app_client;

import java.util.ArrayList;

public class Room {
    private String name;
    private String id;
    private String type;
    private ArrayList<Message> messages;

    public Room(String name, String id, String type, ArrayList<Message> messages) {
        this.name = name;
        this.id = id;
        this.type = type;
        this.messages = messages;
    }

    public Room(String name, String id, String type) {
        this.name = name;
        this.id = id;
        this.type = type;
        messages = new ArrayList<>();
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isLoaded() {
        return !messages.isEmpty();
    }

    public void addMessage(Message msg) {
        messages.add(msg);
    }

    public void setMessages(ArrayList<Message> tmp) {
        messages = tmp;
    }

    public ArrayList<Message> getMessages() {
        return  messages;
    }

    public String getType() {
        return type;
    }

    public String getOther() {
        String[] users = name.split(" & ");
        if (users[0].equals(LocalData.getInstance().getUsername())) {
            return users[1];
        } else {
            return users[0];
        }
    }
}
