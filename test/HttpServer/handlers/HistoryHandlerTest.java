package HttpServer.handlers;

import HttpServer.HttpTaskServer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.Status;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.FileBackedTaskManager;
import service.Managers;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HistoryHandlerTest {
    File file;
    FileBackedTaskManager manager;
    HttpTaskServer server;
    Gson gson;

    HistoryHandlerTest() {
    }

    @BeforeEach
    public void startServer() throws Exception {
        file = File.createTempFile("temp", ".csv");
        manager = new FileBackedTaskManager(file);
        gson = Managers.getGson();
        server = new HttpTaskServer(manager);
        server.start();
    }

    @AfterEach
    public void stopServer() {
        server.stop();
    }

    static class TasksListTypeToken extends TypeToken<List<Task>> {

    }

    @Test
    public void testForGetHistory() throws Exception {
        manager.addNewTask(new Task("Задача 1", "описание задачи 1", Status.NEW,
                LocalDateTime.of(2024, 5, 21, 17, 0), Duration.ofHours(4)));

        manager.addNewTask(new Task("Задача 2", "описание задачи 2", Status.NEW,
                LocalDateTime.of(2024, 5, 22, 17, 0), Duration.ofHours(4)));

        manager.addNewTask(new Task("Задача 3", "описание задачи 3", Status.NEW));

        Task task1 = manager.getTaskById(1);
        Task task2 = manager.getTaskById(2);
        Task task3 = manager.getTaskById(3);
        manager.getTaskById(1);

        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/history");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url).GET().build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        assertEquals(200, response.statusCode());
        List<Task> tasksHistoryList = List.of(task2, task3, task1);
        List<Task> responseListTasks = gson.fromJson(response.body(), new TasksListTypeToken().getType());
        assertEquals(tasksHistoryList, responseListTasks);
    }
}
