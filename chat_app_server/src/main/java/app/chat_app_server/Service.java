package app.chat_app_server;

import app.chat_app_server.micro.GroupManager;
import app.chat_app_server.models.Client;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Service {
    private static final int THREAD_COUNT = 15;
    public static final int PORT_NUM = 9797;

    private static Service service;

    private ServerSocket serverSocket;
    private final ExecutorService threadPool;

    private Service() {
        threadPool = Executors.newFixedThreadPool(THREAD_COUNT);
    };

    public static Service getInstance() {
        return service == null ? service = new Service() : service;
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(PORT_NUM);
            while (!serverSocket.isClosed()) {
                Socket connectionSocket = serverSocket.accept();
                Client client = new Client(connectionSocket);
                threadPool.execute(client);
            }
        } catch (IOException e) {
            if (!serverSocket.isClosed()) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        try {
            serverSocket.close();
            threadPool.shutdown();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
