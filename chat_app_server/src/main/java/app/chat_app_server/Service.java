package app.chat_app_server;

import app.chat_app_server.gui.LogStage;
import app.chat_app_server.models.Client;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Service {
    private static final int THREAD_COUNT  = 15;
    public static final int PORT_NUM = 8945;

    private static Service service;

    private ServerSocket serverSocket;
    private final ExecutorService threadpool;

    private Service() {
        threadpool = Executors.newFixedThreadPool(THREAD_COUNT);
    };


    public static Service getInstance() {
        return service == null ? service = new Service() : service;
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(PORT_NUM);
            while (!serverSocket.isClosed()) {
                Socket connectionSocket = serverSocket.accept();
                threadpool.execute(new Client(connectionSocket));
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
            threadpool.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
