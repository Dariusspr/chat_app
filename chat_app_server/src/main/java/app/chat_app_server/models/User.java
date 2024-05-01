package app.chat_app_server.models;

import java.util.ArrayList;

public class User {
    private String name;
    private String password;
    private Client client;
    private ArrayList<String> memberOf;

    public User(String name, String password, ArrayList<String> memberOf) {
        this.name = name;
        this.password = password;
        this.memberOf = memberOf;
        client = null;
    }

    public User(String name, String password) {
        this.name  = name;
        this.password = password;
        memberOf = new ArrayList<>();
        client = null;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public ArrayList<String> getMemberOf() {
        return memberOf;
    }

    public void setMemberOf(ArrayList<String> memberOf) {
        this.memberOf = memberOf;
    }

    public void addMemberOf(String id) {
        this.memberOf.add(id);
    }
    public void removeMemberOf(String id) {
        this.memberOf.remove(id);
    }


    public void setActive(Client client) {
        this.client = client;
    }

    public void setInactive() {
        this.client = null;
    }

    public boolean isActive() {
        return client != null;
    }

    public Client getClient() {
        return client;
    }
}
