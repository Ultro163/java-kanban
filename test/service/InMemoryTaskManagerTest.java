package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    static TaskManager taskManager = Managers.getDefault();

    @BeforeAll
    public static void setUp() {
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
        final Task task1 = taskManager.getTaskById(1);
        final Task task2 = taskManager.getSubtaskById(5);
        taskManager.getEpicById(3);
        taskManager.getTaskById(1);
        taskManager.getSubtaskById(5);
        taskManager.getEpicById(3);
        taskManager.getTaskById(1);
        taskManager.getSubtaskById(5);
        taskManager.getEpicById(3);
        taskManager.getTaskById(1);

        final List<Task> historyList = taskManager.getHistory();
        assertEquals(task1, historyList.getFirst(), "Задача не соответствует");
        assertEquals(task2, historyList.get(1), "Задача не соответствует");

        taskManager.getEpicById(4);
        taskManager.getTaskById(2);
        taskManager.getSubtaskById(6);

        assertTrue(taskManager.getHistory().size() <= 10, "История привысила установленный объем");
        assertSame(taskManager.getHistory().getLast(), taskManager.getSubtaskById(6),
                "Неверная задача");
    }

    @Test
    public void checkingForAddedAllTasks() {
        assertFalse(taskManager.getAllTasks().isEmpty(), "Задачи не добавились");
        assertFalse(taskManager.getAllSubtasks().isEmpty(), "Задачи не добавились");
        assertFalse(taskManager.getAllEpics().isEmpty(), "Задачи не добавились");
    }

    @Test
    public void checkingForUpdateAllTasks() {
        final Task task = new Task("Измененная Задача 1",
                "обновленное описание задачи 1", Status.DONE);
        final Subtask subtask = new Subtask("Измененная подзадача 1 для эпика 1 (ИД 5)",
                "Измененное описание подзадачи 1", Status.DONE, 3);
        final Epic epic = new Epic("Измененный Эпик 1 (ИД 3)", " Измененное описание эпика 1");

        taskManager.updateTask(task, 1);
        taskManager.updateSubtask(subtask, 5);
        taskManager.updateEpic(epic, 3);

        assertEquals(task, taskManager.getTaskById(1), "Задача не обновилась");
        assertEquals(subtask, taskManager.getSubtaskById(5), "Задача не обновилась");
        assertEquals(epic, taskManager.getEpicById(3), "Задача не обновилась");
    }

    @Test
    public void checkingUpdateEpicStatus() {
        taskManager.updateSubtask(new Subtask("Измененная подзадача 1 для эпика 1 (ИД 5)",
                "Измененное описание подзадачи 1", Status.DONE, 3), 5);
        assertSame(taskManager.getEpicById(3).getStatus(), Status.IN_PROGRESS);

        taskManager.updateSubtask(new Subtask("Измененная подзадача 2 для эпика 1 (ИД 6)",
                "Измененное описание подзадачи 2", Status.DONE, 3), 6);
        assertSame(taskManager.getEpicById(3).getStatus(), Status.DONE);

        taskManager.addNewEpic(new Epic("Эпик 3 (ИД 9)", "описание эпика 3"));
        assertSame(taskManager.getEpicById(9).getStatus(), Status.NEW);
    }

    @AfterAll
    static void checkingForDeleteAllTasks() {
        taskManager.removeTaskById(2);
        taskManager.removeSubtaskById(5);
        taskManager.removeEpicById(3);
        assertNull(taskManager.getTaskById(2), "Задача не удалена");
        assertNull(taskManager.getSubtaskById(5), "Задача не удалена");
        assertNull(taskManager.getEpicById(3), "Задача не удалена");

        taskManager.removeAllTasks();
        assertTrue(taskManager.getAllTasks().isEmpty(), "Задачи не удалились");
        assertTrue(taskManager.getAllSubtasks().isEmpty(), "Задачи не удалились");
        assertTrue(taskManager.getAllEpics().isEmpty(), "Задачи не удалились");
    }
}