package server.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import service.FileBackedTaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class BaseHttpHandler {
    protected static final int PATH_SEGMENT = 2;
    protected final FileBackedTaskManager manager;
    protected final Gson gson;

    public BaseHttpHandler(FileBackedTaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    protected void sendText(HttpExchange exchange, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(200, resp.length);
        exchange.getResponseBody().write(resp);
        exchange.close();
    }

    protected void sendNoText(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(201, 0);
        exchange.getResponseBody().write("".getBytes(StandardCharsets.UTF_8));
        exchange.close();
    }

    protected void sendNotFound(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(404, 0);
        exchange.getResponseBody().write("Задача не найдена.".getBytes(StandardCharsets.UTF_8));
        exchange.close();
    }

    protected void sendHasInteractions(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(406, 0);
        exchange.getResponseBody().write("Найдено пересечение во времени, задача не добавлена."
                .getBytes(StandardCharsets.UTF_8));
        exchange.close();
    }

    protected void sendCustomResponse(HttpExchange exchange, String text, Integer rCode) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(rCode, resp.length);
        exchange.getResponseBody().write(resp);
        exchange.close();
    }
}
