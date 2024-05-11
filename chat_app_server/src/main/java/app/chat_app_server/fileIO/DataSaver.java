package app.chat_app_server.fileIO;

import app.chat_app_server.micro.GroupManager;
import app.chat_app_server.micro.UserManager;
import app.chat_app_server.models.ChatGroup;
import app.chat_app_server.models.Message;
import app.chat_app_server.models.User;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class DataSaver {
    private GroupManager groupManager;
    private UserManager userManager;

    private static final String USERS_FILE_PATH = "./server-users.csv";
    private static final String GROUPS_FILE_PATH = "./server-groups.csv";
    private static final String DELIMITER = ",";
    private static final String GROUP_DELIMITER = "#!";
    public static final String MESSAGE_DELIMITER = "@@";
    public DataSaver() {
        userManager = UserManager.getInstance();
        groupManager = GroupManager.getInstance();
    }

    public void loadUsers() {
        File file = new File(USERS_FILE_PATH);
        if (!file.isFile()) {
            userManager.loadData(new ArrayList<>());
            return;
        }
        ArrayList<User> users = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE_PATH))) {
            String line;
            while((line = reader.readLine()) != null) {
                String[] userData = line.split(DELIMITER);
                if (userData.length == 2) {
                    users.add(new User(userData[0], userData[1]));
                    continue;
                }
                ArrayList<String> memberOf;
                if (userData.length > 2) {
                    memberOf = new ArrayList<>(Arrays.asList(userData).subList(2, userData.length));
                } else {
                    memberOf = new ArrayList<>();
                }
                users.add(new User(userData[0], userData[1], memberOf));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        userManager.loadData(users);
    }

    public void saveUsers() {
        ArrayList<User> users = userManager.getUsers();
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(USERS_FILE_PATH))) {
            for (User user : users) {
                writer.write(user.getName() + DELIMITER + user.getPassword());
                for (String memberOf : user.getMemberOf()) {
                   writer.write(DELIMITER + memberOf);
                }
                writer.newLine();
            }
        }
        catch (IOException e) {
            throw  new RuntimeException(e);
        }

    }

    public void loadGroups() {
        File file = new File(GROUPS_FILE_PATH);
        if (!file.isFile()) {
            groupManager.loadData(new ArrayList<>());
            groupManager.initGlobal();
            return;
        }
        ArrayList<ChatGroup> groups = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(GROUPS_FILE_PATH))) {
            String line;

            while((line = reader.readLine()) != null) {
                String name;
                String id;
                String type;
                ArrayList<Message> messages = new ArrayList<>();

                String[] groupData = line.split(DELIMITER);
                name = groupData[0];
                id = groupData[1];
                type = groupData[2];
                while ((line = reader.readLine()) != null && !line.equalsIgnoreCase(GROUP_DELIMITER)) {
                    String[] messageData = line.split(MESSAGE_DELIMITER);
                    messages.add(new Message(messageData[0], messageData[1]));
                }

                groups.add(new ChatGroup(name, id, messages, type));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        groupManager.loadData(groups);
    }

    public void saveGroups() {
        ArrayList<ChatGroup> groups = groupManager.getGroups();
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(GROUPS_FILE_PATH))) {
            for (ChatGroup group : groups) {
                writer.write(group.getName() + DELIMITER + group.getId() + DELIMITER + group.getType());
                writer.newLine();
                for (Message message : group.getMessages()) {
                    writer.write(message.getSender() + MESSAGE_DELIMITER + message.getContent());
                    writer.newLine();
                }
                writer.write(GROUP_DELIMITER);
                writer.newLine();
            }
        }
        catch (IOException e) {
            throw  new RuntimeException(e);
        }
    }
}
