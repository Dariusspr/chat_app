package app.chat_app_server.models;

import app.chat_app_server.micro.GroupManager;
import app.chat_app_server.micro.UserManager;

import java.io.*;
import java.net.Socket;

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


//    private void get() {
//        new Thread(() -> {
//            try {
//                while(isRunning) {
//                    rawMessage = null;
//
//                    rawMessage = in.readLine();
//
//                    if (rawMessage == null) {
//                        continue;
//                    }
//                    System.out.println("Server received: " + rawMessage);
//                }
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }).start();
//    }

    private void reply() throws IOException {
        while(isRunning) {
            String rawMessage = null;

            rawMessage = in.readLine();
            if (rawMessage == null) {
                continue;
            }

            System.out.println("Server received: " + rawMessage);
            String[] splitMessage = rawMessage.split(MESSAGE_DELIMITER);
            String response = getResponse(splitMessage);
            System.out.println("Server sent: " + response);
            out.println(response);
        }
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
                    return "success";
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
                    return "success";
                } else {
                    return "invalid";
                }
            }
            case "logout" -> { // [ "logout"]
                connectedUser.setInactive();
                groupManager.updateRemove(connectedUser);
                isRunning = false;
                return "success";
            }
            case "create_two_group" -> { // ["create_two_group", "other_user_name"]
                User otherUser = userManager.getUser(splitMessage[1]);
                if (otherUser == null) {
                    return "invalid";
                }
                ChatGroup group = groupManager.createGroup(connectedUser.getName() + ", " + otherUser.getName());

                group.addUser(connectedUser);
                if (otherUser.isActive()) {
                    group.addUser(otherUser);
                }

                otherUser.addMemberOf(group.getId());
                connectedUser.addMemberOf(group.getId());
                return "success";
            }
            case "leave_two_group" -> { // ["leave_two_group", "other_user_name", "group_id"]
                User otherUser = userManager.getUser(splitMessage[1]);
                ChatGroup group = groupManager.getGroup(splitMessage[2]);
                if (group == null || otherUser == null) {
                    return "invalid";
                }
                otherUser.removeMemberOf(group.getId());
                connectedUser.removeMemberOf(group.getId());

                group.removeUser(connectedUser);
                group.removeUser(otherUser);
                groupManager.removeGroup(group.getId());

                return "success";
            }
            case "create_group" -> { // ["create_group", group_name]
                ChatGroup group = groupManager.createGroup(splitMessage[1]);
                group.addUser(connectedUser);
                connectedUser.addMemberOf(group.getId());
                return "success" + MESSAGE_DELIMITER + group.getId();
            }
            case "join_group" -> { // ["join_group", "group_id"]
                ChatGroup group = groupManager.getGroup(splitMessage[1]);
                if (group == null) {
                    return "invalid";
                }
                group.addUser(connectedUser);
                connectedUser.addMemberOf(group.getId());
                return "success";
            }
            case "leave_group" -> { // ["leave_group", "group_id"]
                ChatGroup group = groupManager.getGroup(splitMessage[1]);
                if (group == null) {
                    return "invalid";
                }
                group.removeUser(connectedUser);
                connectedUser.removeMemberOf(group.getId());

                if (group.getMemberSize() == 0) {
                    groupManager.removeGroup(group.getId());
                }

                return "success";
            }
            case "message" -> { // [ "message", "group_id", "content"]
                ChatGroup group = groupManager.getGroup(splitMessage[1]);
                if (group == null || splitMessage.length != 4) {
                    return "invalid";
                }
                group.sendMessage(connectedUser, new Message(connectedUser.getName(), splitMessage[2]));
                return  "success";
            }
            default -> {
                return "error";
            }
        }
    }


    public void sendMessage(Message message) {
        out.println("message" + MESSAGE_DELIMITER + message.getSender() + MESSAGE_DELIMITER + message.getContent());
    }

    public User getConnectedUser() {
        return connectedUser;
    }

}


