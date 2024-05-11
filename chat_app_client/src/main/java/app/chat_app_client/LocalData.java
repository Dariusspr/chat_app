package app.chat_app_client;

import app.chat_app_client.controller.ChatController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class LocalData {
    public static final String GLOBAL_GROUP = "Global";
    public static final String GLOBAL_GROUP_ID = "000000000";

    private static LocalData localData;
    private String username = "";
    private ObservableList<Room> rooms;

    private Room currentRoom = null;

    private boolean readyForMainChat = false;
    private ChatController chatController = null;
    private LocalData() {
        rooms = FXCollections.observableArrayList();
    }

    public static LocalData getInstance() {
        if (localData == null) {
            localData = new LocalData();
        }
        return localData;
    }

    private Room findGlobal() {
        for (Room group : rooms) {
            if (group.getId().equals(GLOBAL_GROUP_ID)) {
                return group;
            }
        }
        return null;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setCurrentGlobal() {
        currentRoom = findGlobal();
    }

    public void addRoom(Room room) {
        rooms.add(room);
    }

    public Room getRoom(String id) {
        for (Room room : rooms) {
            if (room.getId().equals(id)) {
                return room;
            }
        }
        return null;
    }

    public ObservableList<Room> getRooms() {
        return rooms;
    }

    public void updateRooms() {
        ServerConnection.getInstance().updateGroupList();

        if (currentRoom == null) {
            currentRoom = findGlobal();
        }
        ServerConnection.getInstance().updateGroupMessages(currentRoom.getId());
        readyForMainChat = true;
    }

    public boolean notReadyForMainChat() {
        return !readyForMainChat;
    }

    public Room getCurrentRoom() {
        return currentRoom;
    }

    public void setChatController(ChatController chatController) {
        this.chatController = chatController;
    }

    public ChatController getChatController() {
        return chatController;
    }

    public void changeRoom(Room group) {
        currentRoom = group;
    }
}
