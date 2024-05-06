package app.chat_app_server;

import app.chat_app_server.fileIO.DataSaver;
import app.chat_app_server.gui.ServerApp;

public class ServerLauncher {
    public static void main(String[] args) {
        DataSaver dataSaver = new DataSaver();
        dataSaver.loadUsers();
        dataSaver.loadGroups();
        new Thread(() -> Service.getInstance().start()).start();
        ServerApp.main();
    }
}
