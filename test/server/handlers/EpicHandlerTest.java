package server.handlers;

import server.HttpTaskServer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.Epic;
import model.Status;
import model.Subtask;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.FileBackedTaskManager;
import service.Managers;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EpicHandlerTest {
    File file;
    FileBackedTaskManager manager;
    HttpTaskServer server;
    Gson gson;

    EpicHandlerTest() {
    }

    static class SubtasksListTypeToken extends TypeToken<List<Subtask>> {
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

    @Test
    public void testAddEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Эпик 1", "Опись Эпика 1");

        String taskJson = gson.toJson(epic);

        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/epics");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        assertEquals(201, response.statusCode());

        List<Epic> epicsFromManager = manager.getAllEpics();

        assertNotNull(epicsFromManager, "Задачи не возвращаются");
        assertEquals(1, epicsFromManager.size(), "Некорректное количество задач");
        assertEquals(epic, epicsFromManager.getFirst(), "Некорректное имя задачи");
    }

    @Test
    public void testUpdateEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Эпик 1", "Опись Эпика 1");
        manager.addNewEpic(epic);
        Epic epic2 = new Epic("Эпик 2", "Опись Эпика 2");

        Subtask subtask = new Subtask("Test 1", "Testing task 1",
                Status.NEW, 1,
                LocalDateTime.of(2024, 5, 21, 17, 0), Duration.ofMinutes(5));
        manager.addNewSubtask(subtask);

        String taskJson = gson.toJson(epic2);

        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/epics/1");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        assertEquals(201, response.statusCode());

        List<Epic> tasksFromManager = manager.getAllEpics();
        assertEquals(epic2, tasksFromManager.getFirst(), "Некорректная задача");
    }

    @Test
    public void testDeleteEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Эпик 1", "Опись Эпика 1");
        manager.addNewEpic(epic);

        Subtask subtask = new Subtask("Test 1", "Testing task 1",
                Status.NEW, 1,
                LocalDateTime.of(2024, 5, 21, 17, 0), Duration.ofMinutes(5));
        manager.addNewSubtask(subtask);

        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/epics/1");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url).DELETE().build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        assertEquals(200, response.statusCode());

        List<Epic> tasksFromManager = manager.getAllEpics();

        assertEquals(0, tasksFromManager.size(), "Некорректное количество задачю");
        assertArrayEquals(new ArrayList<>().toArray(), tasksFromManager.toArray(), "Задача не удалена.");
    }

    @Test
    public void testOfGetSubtasksForEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Эпик 1", "Опись Эпика 1");
        manager.addNewEpic(epic);

        Subtask subtask = new Subtask("Test 1", "Testing task 1",
                Status.NEW, 1,
                LocalDateTime.of(2024, 5, 21, 17, 0), Duration.ofMinutes(5));
        manager.addNewSubtask(subtask);

        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/epics/1/subtasks");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url).GET().build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        assertEquals(200, response.statusCode());
        List<Subtask> subtasksFromManager = List.of(subtask);
        List<Subtask> subtasksFromResponse = gson.fromJson(response.body(),
                new SubtasksListTypeToken().getType());
        assertEquals(subtasksFromManager, subtasksFromResponse, "Задачи не вернулись");
    }
}
