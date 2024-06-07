package server.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.NotFoundTaskException;
import model.Epic;
import model.Subtask;
import service.FileBackedTaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {

    FileBackedTaskManager manager;
    Gson gson;

    public EpicHandler(FileBackedTaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        switch (method) {
            case "GET":
                handleEpicGet(exchange);
                break;
            case "POST":
                handleEpicPost(exchange);
                break;
            case "DELETE":
                handleEpicDelete(exchange);
                break;
            default:
                sendCustomResponse(exchange, "Неверный путь", 405);
                break;
        }
    }

    private void handleEpicGet(HttpExchange exchange) throws IOException {
        String[] path = exchange.getRequestURI().getPath().split("/");

        if (path.length <= 2) {
            sendText(exchange, gson.toJson(manager.getAllEpics()));
        } else if (path.length == 3) {
            try {
                Epic epic = manager.getEpicById(Integer.parseInt(path[2]));
                sendText(exchange, gson.toJson(epic));
            } catch (NotFoundTaskException e) {
                sendNotFound(exchange);
            }
        } else {
            try {
                List<Subtask> subtasks = manager.getSubtaskForEpic(Integer.parseInt(path[2]));
                sendText(exchange, gson.toJson(subtasks));
            } catch (NullPointerException | NotFoundTaskException e) {
                sendNotFound(exchange);
            }
        }
    }

    private void handleEpicPost(HttpExchange exchange) throws IOException {
        String[] path = exchange.getRequestURI().getPath().split("/");
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

        if (path.length <= 2) {
            try {
                Epic epic = gson.fromJson(body, Epic.class);
                epic.setSubtaskListId(new ArrayList<>());
                manager.addNewEpic(epic);
                sendNoText(exchange);
            } catch (NullPointerException e) {
                sendCustomResponse(exchange, "Задача не передана в запросе", 400);
            }
        } else {
            try {
                Epic epic = gson.fromJson(body, Epic.class);
                epic.setSubtaskListId(new ArrayList<>());
                manager.updateEpic(epic, Integer.parseInt(path[2]));
                sendNoText(exchange);
            } catch (NullPointerException e) {
                sendCustomResponse(exchange, "Задача не передана в запросе", 400);
            } catch (NotFoundTaskException e) {
                sendNotFound(exchange);
            }
        }
    }

    private void handleEpicDelete(HttpExchange exchange) throws IOException {
        try {
            String[] path = exchange.getRequestURI().getPath().split("/");
            manager.removeEpicById(Integer.parseInt(path[2]));
            sendText(exchange, "Задача удалена.");
        } catch (NumberFormatException | NullPointerException e) {
            sendNotFound(exchange);
        }
    }
}
