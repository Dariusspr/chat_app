package app.chat_app_server.micro;

import app.chat_app_server.models.ChatGroup;
import app.chat_app_server.models.User;

import java.util.ArrayList;

public class GroupManager {
    private static GroupManager groupManager;

    public static final String GLOBAL_GROUP = "Global";
    public static final String GLOBAL_GROUP_ID = "000000000";

    private ArrayList<ChatGroup> groups;
    private ChatGroup globalGroup;

    private GroupManager() {
            globalGroup = new ChatGroup(GLOBAL_GROUP);
            globalGroup.setId(GLOBAL_GROUP_ID);

            groups = new ArrayList<>();
    }

    public void loadData(ArrayList<ChatGroup> groups, ChatGroup globalGroup) {
        this.globalGroup = globalGroup;
        this.groups = groups;
    }

    public static GroupManager getInstance() {
        return groupManager == null ? groupManager = new GroupManager() : groupManager;
    }


    public ChatGroup createGroup(String name) {
        ChatGroup group = new ChatGroup(name);
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

}
