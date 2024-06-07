package server;

import server.handlers.*;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import service.FileBackedTaskManager;
import service.Managers;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private static final int PORT = 8080;
    FileBackedTaskManager manager;
    HttpServer httpServer;
    Gson gson;

    public HttpTaskServer(FileBackedTaskManager manager) throws IOException {
        this.manager = manager;
        this.httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        this.gson = Managers.getGson();
    }

    public void start() {
        httpServer.createContext("/tasks", new TaskHandler(manager, gson));
        httpServer.createContext("/epics", new EpicHandler(manager, gson));
        httpServer.createContext("/subtasks", new SubtaskHandler(manager, gson));
        httpServer.createContext("/prioritized", new PrioritizedTasksHandler(manager, gson));
        httpServer.createContext("/history", new HistoryHandler(manager, gson));
        httpServer.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }

    public void stop() {
        httpServer.stop(0);
        System.out.println("HTTP-сервер остановлен!");
    }

    public static void main(String[] args) throws IOException {
        HttpTaskServer server = new HttpTaskServer(Managers.getDefaultBackedTaskManager());
        server.start();
    }
}
