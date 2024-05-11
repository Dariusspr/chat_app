package app.chat_app_server.models;

import app.chat_app_server.utils.CurrentTime;

import java.util.ArrayList;

import static app.chat_app_server.fileIO.DataSaver.MESSAGE_DELIMITER;

public class ChatGroup {
    private String id;
    private String name;
    private String type = "large";
    private ArrayList<Message> messages;
    private ArrayList<User> activeUsers;


    public ChatGroup(String name) {
        this.name = name;
        createId();
        activeUsers = new ArrayList<>();
        messages = new ArrayList<>();
    }


    public ChatGroup(String name, String id, ArrayList<Message> messages, String type) {
        this.name = name;
        this.id = id;
        activeUsers = new ArrayList<>();
        this.messages = messages;
        this.type = type;
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

    public void sendAllMessage(Message message) {
        for (User user : activeUsers) {
            Client client = user.getClient();
            client.sendMessage(id, message);
            addMessage(message);
        }
    }

    public void sendMessage(User sender, Message message) {
        for (User user : activeUsers) {
            if (user.equals(sender)) {
                continue;
            }
            Client client = user.getClient();
            if (client == null) {
                continue;
            }
            client.sendMessage(id, message);
        }
        addMessage(message);
    }

    public int getMemberSize() {
        return activeUsers.size();
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFormattedGroupData() {
        StringBuilder data = new StringBuilder();
        for (int i = 0; i < messages.size(); i++) {
            if (i != 0) {
                data.append(MESSAGE_DELIMITER);
            }
            data.append(messages.get(i).format());
        }
        return data.toString();
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
