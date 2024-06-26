package server.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Task;
import service.FileBackedTaskManager;

import java.io.IOException;
import java.util.List;

public class PrioritizedTasksHandler extends BaseHttpHandler implements HttpHandler {

    public PrioritizedTasksHandler(FileBackedTaskManager manager, Gson gson) {
        super(manager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        if (method.equals("GET")) {
            List<Task> tasks = manager.getPrioritizedTasks();
            sendText(exchange, gson.toJson(tasks));
        } else {
            sendCustomResponse(exchange, "Неверный путь", 405);
        }
    }
}
