package server.handlers;

import server.HttpTaskServer;
import com.google.gson.Gson;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
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

public class SubtasksHandlerTest {
    File file;
    FileBackedTaskManager manager;
    HttpTaskServer server;
    Gson gson;

    SubtasksHandlerTest() {
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
    public void testAddSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Эпик 1", "Опись Эпика 1");
        manager.addNewEpic(epic);

        Subtask subtask = new Subtask("Test 1", "Testing task 1",
                Status.NEW, 1, LocalDateTime.now(), Duration.ofMinutes(5));

        String taskJson = gson.toJson(subtask);

        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/subtasks");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        assertEquals(201, response.statusCode());

        List<Subtask> subtasksFromManager = manager.getAllSubtasks();

        assertNotNull(subtasksFromManager, "Задачи не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество задач");
        assertEquals(subtask, subtasksFromManager.getFirst(), "Некорректное имя задачи");
    }

    @Test
    public void testUpdateSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Эпик 1", "Опись Эпика 1");
        manager.addNewEpic(epic);

        Subtask subtask = new Subtask("Test 1", "Testing task 1",
                Status.NEW, 1,
                LocalDateTime.of(2024, 5, 21, 17, 0), Duration.ofMinutes(5));
        manager.addNewSubtask(subtask);

        Subtask subtask2 = new Subtask("Test 1", "Testing task 1",
                Status.NEW, 1, LocalDateTime.of(2024, 6, 21, 17, 0), Duration.ofMinutes(5));

        String taskJson = gson.toJson(subtask2);

        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/subtasks/2");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        assertEquals(201, response.statusCode());

        List<Subtask> tasksFromManager = manager.getAllSubtasks();
        assertEquals(subtask2, tasksFromManager.getFirst(), "Некорректное имя задачи");
    }

    @Test
    public void testDeleteSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Эпик 1", "Опись Эпика 1");
        manager.addNewEpic(epic);

        Subtask subtask = new Subtask("Test 1", "Testing task 1",
                Status.NEW, 1,
                LocalDateTime.of(2024, 5, 21, 17, 0), Duration.ofMinutes(5));
        manager.addNewSubtask(subtask);

        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/subtasks/2");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url).DELETE().build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        assertEquals(200, response.statusCode());

        List<Subtask> tasksFromManager = manager.getAllSubtasks();

        assertEquals(0, tasksFromManager.size(), "Некорректное количество задачю");
        assertArrayEquals(new ArrayList<>().toArray(), tasksFromManager.toArray(), "Задача не удалена.");
    }

    @Test
    public void testTimeOverlappingSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Эпик 1", "Опись Эпика 1");
        manager.addNewEpic(epic);

        Subtask subtask1 = new Subtask("Задача 1", "описание задачи 1", Status.NEW, 1,
                LocalDateTime.of(2024, 5, 21, 17, 0), Duration.ofHours(4));
        manager.addNewSubtask(subtask1);

        Subtask subtask2 = new Subtask("Задача 2", "описание задачи 2", Status.NEW, 1,
                LocalDateTime.of(2024, 5, 22, 17, 0), Duration.ofHours(4));
        manager.addNewSubtask(subtask2);

        Subtask subtask3 = new Subtask("Test 1", "Testing task 1",
                Status.NEW, 1, LocalDateTime.of(2024, 5, 21, 17, 1), Duration.ofHours(4));

        String taskJson = gson.toJson(subtask3);

        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/subtasks");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        assertEquals(406, response.statusCode());
        List<Task> tasksFromManager1 = manager.getPrioritizedTasks();
        assertEquals(2, tasksFromManager1.size(), "Некорректное количество задач");

        HttpResponse<String> response2;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/subtasks/3");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

            response2 = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        assertEquals(406, response2.statusCode());
        List<Task> tasksFromManager2 = manager.getPrioritizedTasks();
        assertEquals(2, tasksFromManager2.size(), "Некорректное количество задач");

        HttpResponse<String> response3;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/prioritized");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url).GET().build();

            response3 = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        assertEquals(200, response3.statusCode());
        List<Task> tasksFromManager = manager.getPrioritizedTasks();
        List<Task> tasksList = List.of(subtask1, subtask2);

        assertEquals(2, tasksFromManager.size(), "Некорректное количество задач");
        assertArrayEquals(tasksList.toArray(), tasksFromManager.toArray(), "Задачи не верные.");
    }
}
