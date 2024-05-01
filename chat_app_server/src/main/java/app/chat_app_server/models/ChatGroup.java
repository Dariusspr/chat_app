package app.chat_app_server.models;

import app.chat_app_server.utils.CurrentTime;

import java.util.ArrayList;

public class ChatGroup {
    private String id;
    private String name;
    private ArrayList<Message> messages;
    private ArrayList<User> activeUsers;


    public ChatGroup(String name) {
        this.name = name;
        createId();
        activeUsers = new ArrayList<>();
        messages = new ArrayList<>();
    }

    public ChatGroup(String name, String id, ArrayList<Message> messages) {
        this.name = name;
        this.id = id;
        activeUsers = new ArrayList<>();
        this.messages = messages;
    }


    private void createId() {
        id = String.valueOf(CurrentTime.getCurrentTime());
    }

    public void addUser(User user) {
        activeUsers.add(user);
    }

    public void removeUser(User user)
    {
        activeUsers.remove(user);
    }

    public void addMessage(Message message) {
        messages.add(message);
    }
    public void sendMessage(Message message) {
        for (User user : activeUsers) {
            Client client = user.getClient();
            client.sendMessage(message);
            addMessage(message);
        }
    }

    public void sendMessage(User sender, Message message) {
        for (User user : activeUsers) {
            if (user.equals(sender)) {
                continue;
            }
            Client client = user.getClient();
            client.sendMessage(message);
            addMessage(message);
        }
    }

    public ArrayList<Message> getMessages() {
        return this.messages;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getId() {
        return id;
    }
}
