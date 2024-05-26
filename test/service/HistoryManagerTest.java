package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {

    static public void setUp(TaskManager taskManager) {
        taskManager.addNewTask(new Task("Задача 1", "описание задачи 1", Status.NEW));
        taskManager.addNewTask(new Task("Задача 2", "описание задачи 2", Status.NEW));

        taskManager.addNewEpic(new Epic("Эпик 1 (ИД 3)", "описание эпика 1"));
        taskManager.addNewEpic(new Epic("Эпик 2 (ИД 4)", "описание эпика 2"));

        taskManager.addNewSubtask(new Subtask("подзадача 1 для эпика 1 (ИД 5)",
                "описание подзадачи 1", Status.NEW, 3));
        taskManager.addNewSubtask(new Subtask("подзадача 2 для эпика 1 (ИД 6)",
                "описание подзадачи 2", Status.NEW, 3));
        taskManager.addNewSubtask(new Subtask("подзадача 3 для эпика 2 (ИД 7)",
                "описание подзадачи 3", Status.NEW, 4));
        taskManager.addNewSubtask(new Subtask("подзадача 4 для эпика 2 (ИД 8)",
                "описание подзадачи 4", Status.NEW, 4));
    }

    @Test
    public void checkingForAddHistory() {
        TaskManager taskManager = Managers.getDefault();
        setUp(taskManager);

        taskManager.getTaskById(1);
        taskManager.getSubtaskById(5);
        taskManager.getEpicById(3);
        taskManager.getTaskById(1);
        Task task3 = new Task("1", "1", Status.DONE);
        taskManager.updateTask(task3, 1);
        taskManager.getSubtaskById(5);
        taskManager.getEpicById(3);
        taskManager.getTaskById(1);
        taskManager.getSubtaskById(5);
        taskManager.getEpicById(3);
        taskManager.getTaskById(1);
        taskManager.removeTaskById(1);

        final List<Task> historyList = taskManager.getHistory();
        assertEquals("подзадача 1 для эпика 1 (ИД 5)", historyList.getFirst().getTaskName(),
                "Задача не соответствует");
        assertEquals("Эпик 1 (ИД 3)", historyList.getLast().getTaskName(), "Задача не соответствует");
    }
}