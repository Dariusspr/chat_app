package app.chat_app_server.micro;

import app.chat_app_server.models.User;

import java.util.ArrayList;

import static app.chat_app_server.micro.GroupManager.GLOBAL_GROUP_ID;

public class UserManager {
    private static UserManager userManager;
    private ArrayList<User> users;

    private UserManager() {}

    public static UserManager getInstance() {
        return userManager == null ? userManager = new UserManager() : userManager;
    }

    public void loadData(ArrayList<User> users) {
        this.users = users;
    }

    public boolean createUser(String name, String password) {
        if (getUser(name) != null) {
            return false;
        }
        User user = new User(name, password);
        users.add(user);
        user.addMemberOf(GLOBAL_GROUP_ID);
        return true;
    }

    public User getUser(String name) {
        for (User user: users) {
            if (user.getName().equals(name)) {
                return user;
            }
        }

        return null;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public boolean isValidUserInfo(String name, String password) {
        User user = getUser(name);
        return user != null && user.getPassword().equals(password);
    }
}


