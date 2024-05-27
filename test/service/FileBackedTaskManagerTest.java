package service;

import exceptions.ManagerSaveException;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    File file;

    @BeforeEach
    void setUp() {
        try {
            File file = File.createTempFile("temp", ".csv");
            taskManager = new FileBackedTaskManager(file);
        } catch (IOException e) {
            System.out.println("Ошибка создания файла");
        }
    }

    @Test
    void testForSavingToFile() {
        try {
            file = File.createTempFile("temp", ".csv");
            TaskManager manager = new FileBackedTaskManager(file);

            manager.addNewTask(new Task("Задача 1", "описание задачи 1", Status.NEW));
            manager.addNewEpic(new Epic("Эпик 1 (ИД 3)", "описание эпика 1"));
            manager.addNewSubtask(new Subtask("подзадача 1 для эпика 1 (ИД 2)",
                    "описание подзадачи 1", Status.NEW, 2));
            manager.addNewTask(new Task("Измененная Задача 1",
                    "обновленное описание задачи 1", Status.DONE,
                    LocalDateTime.of(2024, 5, 5, 5, 0),
                    Duration.ofHours(1)));

            List<String> taskList = new ArrayList<>();
            taskList.add("id,type,name,status,description,epic,dataTime,duration");
            taskList.add("1,TASK,Задача 1,NEW,описание задачи 1");
            taskList.add("4,TASK,Измененная Задача 1,DONE,обновленное описание задачи 1,2024-05-05T05:00,PT1H");
            taskList.add("2,EPIC,Эпик 1 (ИД 3),NEW,описание эпика 1");
            taskList.add("3,SUBTASK,подзадача 1 для эпика 1 (ИД 2),NEW,описание подзадачи 1,2");


            List<String> savedTasks = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {

                while (reader.ready()) {
                    savedTasks.add(reader.readLine());
                }
            } catch (IOException e) {
                throw new ManagerSaveException("Ошибка при чтении файла.");
            }
            assertEquals(taskList, savedTasks);
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении файла.");
        }
    }

    @Test
    void testForLoadFromFile() {
        try {
            file = File.createTempFile("temp", ".csv");
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
                writer.write("id,type,name,status,description,epic,dataTime,duration");
                writer.newLine();
                writer.write("1,TASK,Задача 1,NEW,описание задачи 1,");
                writer.newLine();
                writer.write("2,EPIC,Эпик 1 (ИД 2),IN_PROGRESS,описание эпика 1,");
                writer.newLine();
                writer.write("3,SUBTASK,подзадача 1 для эпика 1 (ИД 2),NEW,описание подзадачи 1,2");
                writer.newLine();
                writer.write("4,SUBTASK,Измененная ПодЗадача 1,DONE,обновленное описание ПодЗадачи 1,2,2024-05-05T05:00,PT1H");
            } catch (IOException e) {
                throw new ManagerSaveException("Ошибка при сохранении файла.");
            }

            FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);

            final Task task = new Task("Задача 1", "описание задачи 1", Status.NEW);
            task.setId(1);
            final Epic epic = new Epic("Эпик 1 (ИД 2)", "описание эпика 1");
            epic.setId(2);
            final Subtask subtask = new Subtask("подзадача 1 для эпика 1 (ИД 2)",
                    "описание подзадачи 1", Status.NEW, 2);
            subtask.setId(3);
            final Subtask subtask2 = new Subtask("Измененная ПодЗадача 1",
                    "обновленное описание ПодЗадачи 1", Status.DONE, 2,
                    LocalDateTime.of(2024, 5, 5, 5, 0),
                    Duration.ofHours(1));

            LocalDateTime timeEpic = LocalDateTime.of(2024, 5, 5, 5, 0);

            assertEquals(task, loadedManager.getTaskById(1));
            assertEquals(epic, loadedManager.getEpicById(2));
            assertEquals(subtask, loadedManager.getSubtaskById(3));
            assertEquals(subtask2, loadedManager.getSubtaskById(4));
            assertEquals(5, loadedManager.generatedId);
            assertEquals(timeEpic, loadedManager.getEpicById(2).getStartTime(), "Время не изменилось.");
            assertEquals(Status.IN_PROGRESS, loadedManager.getEpicById(2).getStatus());
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при загрузке файла.");
        }
    }
}