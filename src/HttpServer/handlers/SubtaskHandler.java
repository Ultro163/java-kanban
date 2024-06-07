package HttpServer.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.NotFoundTaskException;
import exceptions.TimeOverlapException;
import model.Subtask;
import service.FileBackedTaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {

    FileBackedTaskManager manager;
    Gson gson;

    public SubtaskHandler(FileBackedTaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        switch (method) {
            case "GET":
                handleSubtaskGet(exchange);
                break;
            case "POST":
                handleSubtaskPost(exchange);
                break;
            case "DELETE":
                handleSubtaskDelete(exchange);
                break;
            default:
                sendCustomResponse(exchange, "Неверный путь", 405);
                break;
        }
    }

    private void handleSubtaskGet(HttpExchange exchange) throws IOException {
        String[] path = exchange.getRequestURI().getPath().split("/");

        if (path.length <= 2) {
            sendText(exchange, gson.toJson(manager.getAllSubtasks()));
        } else {
            try {
                Subtask subtask = manager.getSubtaskById(Integer.parseInt(path[2]));
                sendText(exchange, gson.toJson(subtask));
            } catch (NotFoundTaskException e) {
                sendNotFound(exchange);
            }
        }
    }

    private void handleSubtaskPost(HttpExchange exchange) throws IOException {
        String[] path = exchange.getRequestURI().getPath().split("/");
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

        if (path.length <= 2) {
            try {
                Subtask subtask = gson.fromJson(body, Subtask.class);
                manager.addNewSubtask(subtask);
                sendNoText(exchange);
            } catch (TimeOverlapException e) {
                sendHasInteractions(exchange);
            } catch (NullPointerException e) {
                sendCustomResponse(exchange, "Задача не передана в запросе", 400);
            }
        } else {
            try {
                Subtask subtask = gson.fromJson(body, Subtask.class);
                manager.updateSubtask(subtask, Integer.parseInt(path[2]));
                sendNoText(exchange);
            } catch (TimeOverlapException e) {
                sendHasInteractions(exchange);
            } catch (NotFoundTaskException e) {
                sendNotFound(exchange);
            } catch (NullPointerException e) {
                sendCustomResponse(exchange, "Задача не передана в запросе", 400);
            }
        }
    }

    private void handleSubtaskDelete(HttpExchange exchange) throws IOException {
        try {
            String[] path = exchange.getRequestURI().getPath().split("/");
            manager.removeSubtaskById(Integer.parseInt(path[2]));
            sendText(exchange, "Задача удалена.");
        } catch (NumberFormatException | NullPointerException e) {
            sendNotFound(exchange);
        }
    }
}
