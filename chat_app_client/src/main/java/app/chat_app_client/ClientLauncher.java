package app.chat_app_client;

public class ClientLauncher {
    public static void main(String[] args) {
        ServerConnection.getInstance().start();
        ClientApp.main(args);

    }
}
