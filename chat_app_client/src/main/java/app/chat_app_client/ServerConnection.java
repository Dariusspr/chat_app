package app.chat_app_client;

import javafx.application.Platform;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ServerConnection {
    public final static String MESSAGE_DELIMITER = "@@";
    private static ServerConnection connection;
    private Socket clientSocket;

    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private PrintWriter out;
    private BufferedReader in;
    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 8945;

    private boolean isReceiving = true;
    private String response = "";
    private ServerConnection() {
        try {
            clientSocket = new Socket(SERVER_IP, SERVER_PORT);
            if  (clientSocket.isConnected()) {
                System.out.println("Connected");
            } else {
                System.out.println("Failed to connect");
                System.exit(-1);
            }
            outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            out = new PrintWriter(new OutputStreamWriter(outputStream), true);
            inputStream = new ObjectInputStream(clientSocket.getInputStream());
            in = new BufferedReader(new InputStreamReader(inputStream));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ServerConnection getInstance() {
        if (connection == null) {
            connection = new ServerConnection();
        }
        return connection;
    }

    public void start() {
        isReceiving = true;
        startReceivingMessages();
    }

    public void startReceivingMessages() {
        Thread receivingThread = new Thread(() -> {
            try {
                String receivedMessage;
                while (isReceiving) {
                    receivedMessage = in.readLine();
                    if (receivedMessage == null || receivedMessage.isEmpty()) {
                        continue;
                    }
                    //System.out.println("Received: " + receivedMessage);
                    String[] splitMessage = receivedMessage.split(MESSAGE_DELIMITER);
                    response = respond(splitMessage);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        receivingThread.start();
    }

    private String respond(String[] received) {
        switch (received[0]) {
            case "register_success", "message_success" -> {
                return "success";
            }
            case "login_success" -> { //["login_success", "username"]
                LocalData.getInstance().setUsername(received[1]);
                return "success";
            }
            case "group_list_success" -> { //["login_success", ("group_name", "group_id", "type"))^n ]
                ArrayList<Room> rooms = new ArrayList<>();
                for (int i = 1; i < received.length - 2; i+=3) {
                    rooms.add(new Room(received[i], received[i + 1], received[i + 2]));
                }
                LocalData.getInstance().getRooms().setAll(rooms);
                return "success";
            }
            case "group_data_success" -> { //["group_data", "id", ("sender", "content")^n ]
                Room room = LocalData.getInstance().getRoom(received[1]);
                ArrayList<Message> tmp = new ArrayList<>();
                for (int i = 2; i < received.length - 1; i += 2) {
                    tmp.add(new Message(received[i], received[i + 1]));
                }
                room.setMessages(tmp);

                return "success";
            }
            case "message" -> { // ["message", "id", "sender", "content"]
                if (received.length != 4) {
                    return "";
                }
                Room room = LocalData.getInstance().getRoom(received[1]);
                Message msg = new Message(received[2], received[3]);
                room.addMessage(msg);
                if (room.getId().equalsIgnoreCase(LocalData.getInstance().getCurrentRoom().getId()))
                    LocalData.getInstance().getChatController().addMessage(msg);
                return "success";
            }
            case "create_group_success" -> { // [ "create_group_success" , "group_name", "group_id"]
                Platform.runLater(() -> {
                            LocalData.getInstance().getChatController().updateRoomList();
                        });
                return "success";
            }
            case "create_two_group_success" -> {
                Platform.runLater(() -> {
                    LocalData.getInstance().getChatController().updateRoomList();
                });
                return "success";
            }
            case "leave_group_success" -> {
                LocalData.getInstance().setCurrentGlobal();
                return "success";
            }
            case "leave_two_group_success" -> {
                LocalData.getInstance().setCurrentGlobal();
                return "success";
            }

            case "join_group_success" -> {
                Platform.runLater(() -> {
                    LocalData.getInstance().getChatController().updateRoomList();
                });
                return "success";
            }

            case "update" -> {
                Platform.runLater(() -> {
                    LocalData.getInstance().getChatController().updateRoomList();
                });
                return "success";
            }

            default ->  {
                return "invalid";
            }
        }
    }

    public Boolean send(String msg) {
        response = "";
        out.println(msg);
        while (response.isEmpty()) {
            Thread.onSpinWait();
        }
        return response.equals("success");
    }

    public void stop() {
        try {
            isReceiving = false;
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }

            if (outputStream != null) {
                outputStream.close();
            }

            if (inputStream != null) {
                inputStream.close();
            }
            if (clientSocket != null) {
                clientSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public boolean sendRegister(String name, String password) {
        return send("register" + MESSAGE_DELIMITER + name + MESSAGE_DELIMITER + password);
    }
    public boolean sendMessage(Message msg) {
        return send("message" + MESSAGE_DELIMITER + LocalData.getInstance().getCurrentRoom().getId() +  MESSAGE_DELIMITER + msg.format());
    }

    public boolean login(String name, String password) {
        return send("login" + MESSAGE_DELIMITER + name + MESSAGE_DELIMITER + password);
    }

    public void updateGroupList() {
        send("group_list");
    }

    public void updateGroupMessages(String id) {
        send("get_group_data" + MESSAGE_DELIMITER + id);

    }

    public void logout() {
        send("logout");
    }

    public void leaveGroup(Room group) {
        if (group.getType().equals("large")) {
            send("leave_group" + MESSAGE_DELIMITER + group.getId());
        } else {
            send("leave_two_group" + MESSAGE_DELIMITER + group.getOther() + MESSAGE_DELIMITER + group.getId());
        }
    }

    public boolean createGroup(String name, String type) {
        if (type.equals("large")) {
            return send("create_group" + MESSAGE_DELIMITER + name);
        } else {
            return send("create_two_group" + MESSAGE_DELIMITER + name);
        }
    }

    public boolean joinGroup(String id) {
        return send("join_group" + MESSAGE_DELIMITER + id);
    }
}
