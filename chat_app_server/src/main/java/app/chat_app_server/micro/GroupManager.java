package app.chat_app_server.micro;

import app.chat_app_server.models.ChatGroup;
import app.chat_app_server.models.Message;
import app.chat_app_server.models.User;
import app.chat_app_server.utils.CurrentTime;

import java.util.ArrayList;

public class GroupManager {
    private static GroupManager groupManager;

    public static final String GLOBAL_GROUP = "Global";
    public static final String GLOBAL_GROUP_ID = "000000000";

    private ArrayList<ChatGroup> groups;
    private ChatGroup globalGroup;

    private GroupManager() {}

    public void loadData(ArrayList<ChatGroup> groups) {
        this.groups = groups;
        globalGroup = findGlobal();
        if (globalGroup == null) {
            initGlobal();
        }
    }

    public static GroupManager getInstance() {
        return groupManager == null ? groupManager = new GroupManager() : groupManager;
    }

    private ChatGroup findGlobal() {
        for (ChatGroup group : groups) {
            if (group.getId().equals(GLOBAL_GROUP_ID)) {
                return group;
            }
        }
        return null;
    }

    public void initGlobal() {
        globalGroup = new ChatGroup(GLOBAL_GROUP);
        globalGroup.setId(GLOBAL_GROUP_ID);
        globalGroup.setType("large");
        groups.add(globalGroup);
    }

    public ChatGroup createGroup(String name, String type) {
        ChatGroup group = new ChatGroup(name);
        if (type.equalsIgnoreCase("large")) {
            group.setName(name);
        }
        group.setType(type);
        groups.add(group);
        return group;
    }

    public ChatGroup getGroup(String id) {
        for (ChatGroup group : groups) {
            if (group.getId().equals(id)) {
                return group;
            }
        }
        return null;
    }

    private int getGroupIndex(String id) {
        for (int i  = 0; i < groups.size(); i++) {
            if (groups.get(i).getId().equals(id)) {
                return i;
            }
        }
        return -1;
    }

    public void removeGroup(String id) {
        int index = getGroupIndex(id);
        if (index != -1) {
            groups.remove(index);
        }
    }

    public void updateRemove(User user) {
        for (ChatGroup group : groups) {
            group.removeUser(user);
        }
    }

    public void updateAdd(User user) {
        ArrayList<String> memberOf = user.getMemberOf();
        for (ChatGroup group : groups) {
            if (memberOf.contains(group.getId())) {
                group.addUser(user);
            }
        }
    }

    public ArrayList<ChatGroup> getGroups() {
        return groups;
    }
}
