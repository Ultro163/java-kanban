package service;

import exceptions.ManagerSaveException;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {
    File file;

    @Test
    void testForSavingToFile() {
        try {
            file = File.createTempFile("temp", ".csv");
            TaskManager manager = new FileBackedTaskManager(file);

            manager.addNewTask(new Task("Задача 1", "описание задачи 1", Status.NEW));
            manager.addNewEpic(new Epic("Эпик 1 (ИД 3)", "описание эпика 1"));
            manager.addNewSubtask(new Subtask("подзадача 1 для эпика 1 (ИД 2)",
                    "описание подзадачи 1", Status.NEW, 2));

            List<String> taskList = new ArrayList<>();
            taskList.add("id,type,name,status,description,epic");
            taskList.add("1,TASK,Задача 1,NEW,описание задачи 1,");
            taskList.add("2,EPIC,Эпик 1 (ИД 3),NEW,описание эпика 1,");
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
                writer.write("id,type,name,status,description,epic");
                writer.newLine();
                writer.write("1,TASK,Задача 1,NEW,описание задачи 1,");
                writer.newLine();
                writer.write("2,EPIC,Эпик 1 (ИД 3),NEW,описание эпика 1,");
                writer.newLine();
                writer.write("3,SUBTASK,подзадача 1 для эпика 1 (ИД 2),NEW,описание подзадачи 1,2");
                writer.newLine();
            } catch (IOException e) {
                throw new ManagerSaveException("Ошибка при сохранении файла.");
            }

            FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);

            Task task = new Task("Задача 1", "описание задачи 1", Status.NEW);
            task.setId(1);
            Epic epic = new Epic("Эпик 1 (ИД 3)", "описание эпика 1");
            epic.setId(2);
            Subtask subtask = new Subtask("подзадача 1 для эпика 1 (ИД 2)",
                    "описание подзадачи 1", Status.NEW, 2);
            subtask.setId(3);

            assertEquals(task, loadedManager.getTaskById(1));
            assertEquals(epic, loadedManager.getEpicById(2));
            assertEquals(subtask, loadedManager.getSubtaskById(3));
            assertEquals(4, loadedManager.generatedId);
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при загрузке файла.");
        }
    }
}