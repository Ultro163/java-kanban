package httpServer.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.NotFoundTaskException;
import exceptions.TimeOverlapException;
import model.Task;
import service.FileBackedTaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {

    FileBackedTaskManager manager;
    Gson gson;

    public TaskHandler(FileBackedTaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        switch (method) {
            case "GET":
                handleGet(exchange);
                break;
            case "POST":
                handlePost(exchange);
                break;
            case "DELETE":
                handleDelete(exchange);
                break;
            default:
                sendCustomResponse(exchange, "Неверный путь", 405);
                break;
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        String[] path = exchange.getRequestURI().getPath().split("/");

        if (path.length <= 2) {
            sendText(exchange, gson.toJson(manager.getAllTasks()));
        } else {
            try {
                Task task = manager.getTaskById(Integer.parseInt(path[2]));
                sendText(exchange, gson.toJson(task));
            } catch (NotFoundTaskException e) {
                sendNotFound(exchange);
            }
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        String[] path = exchange.getRequestURI().getPath().split("/");
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

        if (path.length <= 2) {
            try {
                Task task = gson.fromJson(body, Task.class);
                manager.addNewTask(task);
                sendNoText(exchange);
            } catch (TimeOverlapException e) {
                sendHasInteractions(exchange);
            } catch (NullPointerException e) {
                sendCustomResponse(exchange, "Задача не передана в запросе", 400);
            }
        } else {
            try {
                Task task = gson.fromJson(body, Task.class);
                manager.updateTask(task, Integer.parseInt(path[2]));
                sendNoText(exchange);
            } catch (TimeOverlapException e) {
                sendHasInteractions(exchange);
            } catch (NullPointerException e) {
                sendCustomResponse(exchange, "Задача не передана в запросе", 400);
            } catch (NotFoundTaskException e) {
                sendNotFound(exchange);
            }
        }
    }

    private void handleDelete(HttpExchange exchange) throws IOException {
        try {
            String[] path = exchange.getRequestURI().getPath().split("/");
            manager.removeTaskById(Integer.parseInt(path[2]));
            sendText(exchange, "Задача удалена.");
        } catch (NumberFormatException | NullPointerException e) {
            sendNotFound(exchange);
        }
    }
}
