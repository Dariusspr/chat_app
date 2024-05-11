package app.chat_app_server.models;

import app.chat_app_server.gui.LogController;
import app.chat_app_server.gui.LogStage;
import app.chat_app_server.micro.GroupManager;
import app.chat_app_server.micro.UserManager;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

import static app.chat_app_server.fileIO.DataSaver.MESSAGE_DELIMITER;

public class Client implements  Runnable{
    private Socket clientSocket;

    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private PrintWriter out;
    private BufferedReader in;

    private User connectedUser;

    private boolean isRunning = true; // todo: running only if successfully logged in

    private UserManager userManager = UserManager.getInstance();
    private GroupManager groupManager = GroupManager.getInstance();

    public Client(Socket socket) throws IOException {
        this.clientSocket = socket;

        outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
        inputStream = new ObjectInputStream(clientSocket.getInputStream());
        out = new PrintWriter(new OutputStreamWriter(outputStream), true);
        in = new BufferedReader(new InputStreamReader(inputStream));
        connectedUser = null;
    }


    private void reply() throws IOException {
        while(isRunning) {
            String rawMessage = null;
            rawMessage = in.readLine();
            if (rawMessage == null) {
                continue;
            }

            String[] splitMessage = rawMessage.split(MESSAGE_DELIMITER);
            String response = getResponse(splitMessage);
            if (response != null) {
                out.println(response);
            }
        }
    }

    private String getFormattedMemberOf() {
        StringBuilder formattedGroups = new StringBuilder();
        ArrayList<String> ids = connectedUser.getMemberOf();
        for (int i = 0; i < ids.size(); i++)
        {
            ChatGroup group = groupManager.getGroup(ids.get(i));
            if (group == null) {
                continue;
            }
            if (i != 0) {
                formattedGroups.append(MESSAGE_DELIMITER);
            }
            formattedGroups.append(group.getName()).append(MESSAGE_DELIMITER).append(ids.get(i)).append(MESSAGE_DELIMITER).append(group.getType());
        }
        return formattedGroups.toString();
    }

    @Override
    public void run() {
        try {
            reply();

        } catch (EOFException e) {
            System.out.println("Closed");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
                if (clientSocket != null) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                // Handle any exceptions that occur during cleanup
                e.printStackTrace();
            }
        }
    }

    // TODO: sending data of groups when user log ins
    private String getResponse(String[] splitMessage) {
        switch (splitMessage[0]) {
            case "register" -> {  // [ "register", "name", "password"]
                if (splitMessage.length != 3) {
                    return "invalid";
                }
                if (userManager.createUser(splitMessage[1], splitMessage[2])) {
                    LogStage.getInstance().getController().createMessage("Account " + splitMessage[1] + " was created.");

                    return "register_success";
                } else {
                    return "invalid";
                }
            }
            case "login" -> { // [ "login", "name", "password"]
                if (splitMessage.length != 3) {
                    return "invalid";
                }
                if (userManager.isValidUserInfo(splitMessage[1], splitMessage[2])) {
                    connectedUser = userManager.getUser(splitMessage[1]);
                    connectedUser.setActive(this);
                    groupManager.updateAdd(connectedUser);
                    LogStage.getInstance().getController().createMessage("'" + connectedUser.getName() + "' is online.");
                    return "login_success" +  MESSAGE_DELIMITER + connectedUser.getName();
                } else {
                    return "invalid";
                }
            }
            case "logout" -> { // [ "logout"]
                LogStage.getInstance().getController().createMessage("'" +connectedUser.getName() + "' is offline.");
                connectedUser.setInactive();
                groupManager.updateRemove(connectedUser);
                isRunning = false;
                return "success";
            }
            case "get_group_data" -> { // ["get_group_data". "group_id"]
                String id = splitMessage[1];
                if (!connectedUser.isMember(id)) {
                    return "invalid";
                }
                ChatGroup group = groupManager.getGroup(splitMessage[1]);
                if (group == null) {
                    return "invalid";
                }
                return "group_data_success" + MESSAGE_DELIMITER + group.getId() + MESSAGE_DELIMITER + group.getFormattedGroupData();
            }
            case "group_list" -> { // "group_list"
                return "group_list_success" + MESSAGE_DELIMITER + getFormattedMemberOf();
            }

            case "message" -> { // [ "message", "group_id", "content"]
                ChatGroup group = groupManager.getGroup(splitMessage[1]);
                if (group == null || splitMessage.length != 4) {
                    return "invalid";
                }
                group.sendMessage(connectedUser, new Message(splitMessage[2], splitMessage[3]));

                return  "message_success";
            }

            case "create_group" -> { // ["create_group", group_name]
                String name = splitMessage[1];
                ChatGroup group = groupManager.createGroup(name, "large");
                group.addUser(connectedUser);
                connectedUser.addMemberOf(group.getId());
                LogStage.getInstance().getController().createMessage("Group '" + splitMessage[1] + "' was created.");
                return "create_group_success" + MESSAGE_DELIMITER + name;
            }
            case "leave_group" -> { // ["leave_group", "group_id"]
                ChatGroup group = groupManager.getGroup(splitMessage[1]);
                if (group == null) {
                    return "invalid";
                }
                group.removeUser(connectedUser);
                connectedUser.removeMemberOf(group.getId());

                if (group.getMemberSize() == 1) {
                    LogStage.getInstance().getController().createMessage("Group '" + group.getName() + "' was deleted.");
                    groupManager.removeGroup(group.getId());
                }
                return "leave_group_success";
            }


            case "create_two_group" -> { // ["create_two_group", "other_user_name"]
                User otherUser = userManager.getUser(splitMessage[1]);
                if (otherUser == null || otherUser.getName().equalsIgnoreCase(connectedUser.getName())) {
                    return "invalid";
                }
                ChatGroup group = groupManager.createGroup(connectedUser.getName() + " & " + otherUser.getName(), "small");
                LogStage.getInstance().getController().createMessage("Group '" + group.getName() + "' was created.");

                group.addUser(connectedUser);
                if (otherUser.isActive()) {
                    group.addUser(otherUser);
                }

                otherUser.addMemberOf(group.getId());
                new Thread(() -> otherUser.getClient().update()).start();
                connectedUser.addMemberOf(group.getId());
                return "create_two_group_success";
            }
            case "leave_two_group" -> { // ["leave_two_group", "other_user_name", "group_id"]
                User otherUser = userManager.getUser(splitMessage[1]);
                ChatGroup group = groupManager.getGroup(splitMessage[2]);
                if (group == null || otherUser == null) {
                    return "invalid";
                }
                otherUser.removeMemberOf(group.getId());
                new Thread(() -> otherUser.getClient().update()).start();
                connectedUser.removeMemberOf(group.getId());

                LogStage.getInstance().getController().createMessage("Group '" + group.getName() + "' was deleted.");
                group.removeUser(connectedUser);
                group.removeUser(otherUser);
                groupManager.removeGroup(group.getId());

                return "leave_two_group_success";
            }

            case "join_group" -> { // ["join_group", "group_id"]
                ChatGroup group = groupManager.getGroup(splitMessage[1]);
                if (group == null) {
                    return "invalid";
                }
                group.addUser(connectedUser);
                connectedUser.addMemberOf(group.getId());
                return "join_group_success";
            }

            default -> {
                return "error";
            }
        }
    }


    public void update() {
        out.println("update");
    }

    public void sendMessage(String id, Message message) {
        out.println("message" + MESSAGE_DELIMITER + id + MESSAGE_DELIMITER + message.getSender() + MESSAGE_DELIMITER + message.getContent());
    }

    public User getConnectedUser() {
        return connectedUser;
    }

}


