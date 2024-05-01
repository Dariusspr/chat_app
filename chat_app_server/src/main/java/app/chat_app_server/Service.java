package app.chat_app_server;

import app.chat_app_server.micro.GroupManager;
import app.chat_app_server.models.Client;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Service {
    private static final int THREAD_COUNT = 15;
    public static final int PORT_NUM = 9797;

    private static Service service;

    private ServerSocket serverSocket;
    private final ExecutorService threadPool;
    private boolean isRunning = true;

    private Service() {
        threadPool = Executors.newFixedThreadPool(THREAD_COUNT);
    };

    public static Service getInstance() {
        return service == null ? service = new Service() : service;
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(PORT_NUM);
            while (isRunning) {
                Socket connectionSocket = serverSocket.accept();

                Client client = new Client(connectionSocket);

                threadPool.execute(client);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        try {
            isRunning = false;
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
