package server.handlers;

import server.HttpTaskServer;
import com.google.gson.Gson;
import model.Status;
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

class TaskHandlerTest {
    File file;
    FileBackedTaskManager manager;
    HttpTaskServer server;
    Gson gson;

    TaskHandlerTest() {
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
    public void testAddTask() throws IOException, InterruptedException {
        Task task = new Task("Test 1", "Testing task 1",
                Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5));

        String taskJson = gson.toJson(task);

        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/tasks");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = manager.getAllTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 1", tasksFromManager.getFirst().getTaskName(), "Некорректное имя задачи");
    }

    @Test
    public void testUpdateTask() throws IOException, InterruptedException {
        Task task = new Task("Test 1", "Testing task 1",
                Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        manager.addNewTask(task);

        Task task2 = new Task("Test 2", "Testing task 2",
                Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5));

        String taskJson = gson.toJson(task2);

        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/tasks/1");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = manager.getAllTasks();
        assertEquals(task2, tasksFromManager.getFirst(), "Некорректное имя задачи");
    }

    @Test
    public void testDeleteTask() throws IOException, InterruptedException {
        Task task = new Task("Test 1", "Testing task 1",
                Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        manager.addNewTask(task);

        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/tasks/1");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url).DELETE().build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        assertEquals(200, response.statusCode());

        List<Task> tasksFromManager = manager.getAllTasks();

        assertEquals(0, tasksFromManager.size(), "Некорректное количество задачю");
        assertArrayEquals(new ArrayList<>().toArray(), tasksFromManager.toArray(), "Задача не удалена.");
    }

    @Test
    public void testTimeOverlappingTasks() throws IOException, InterruptedException {
        Task task1 = new Task("Задача 1", "описание задачи 1", Status.NEW,
                LocalDateTime.of(2024, 5, 21, 17, 0), Duration.ofHours(4));
        manager.addNewTask(task1);

        Task task2 = new Task("Задача 2", "описание задачи 2", Status.NEW,
                LocalDateTime.of(2024, 5, 22, 17, 0), Duration.ofHours(4));
        manager.addNewTask(task2);

        Task task3 = new Task("Test 1", "Testing task 1",
                Status.NEW, LocalDateTime.of(2024, 5, 21, 17, 1), Duration.ofHours(4));

        String taskJson = gson.toJson(task3);

        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/tasks");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        assertEquals(406, response.statusCode());
        List<Task> tasksFromManager1 = manager.getPrioritizedTasks();
        assertEquals(2, tasksFromManager1.size(), "Некорректное количество задач");

        HttpResponse<String> response2;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/tasks/2");
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
        List<Task> tasksList = List.of(task1, task2);

        assertEquals(2, tasksFromManager.size(), "Некорректное количество задач");
        assertArrayEquals(tasksList.toArray(), tasksFromManager.toArray(), "Задачи не верные.");
    }
}