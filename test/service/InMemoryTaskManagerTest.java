package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class InMemoryTaskManagerTest {

    static TaskManager taskManager = Managers.getDefault();

    @BeforeAll
    static public void setUp() {
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
    @Order(1)
    public void checkingForAddHistory() {
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


        final List<Task> historyList = taskManager.getHistory();
        assertEquals("подзадача 1 для эпика 1 (ИД 5)", historyList.getFirst().getTaskName(),
                "Задача не соответствует");
        assertEquals("1", historyList.getLast().getTaskName(), "Задача не соответствует");


    }

    @Test
    @Order(2)
    public void checkingForAddedAllTasks() {
        assertFalse(taskManager.getAllTasks().isEmpty(), "Задачи не добавились");
        assertFalse(taskManager.getAllSubtasks().isEmpty(), "Задачи не добавились");
        assertFalse(taskManager.getAllEpics().isEmpty(), "Задачи не добавились");
    }

    @Test
    @Order(3)
    public void checkingUpdateEpicStatus() {
        taskManager.updateSubtask(new Subtask("Измененная подзадача 1 для эпика 1 (ИД 5)",
                "Измененное описание подзадачи 1", Status.DONE, 3), 5);
        assertSame(taskManager.getEpicById(3).getStatus(), Status.IN_PROGRESS);

        taskManager.updateSubtask(new Subtask("Измененная подзадача 2 для эпика 1 (ИД 6)",
                "Измененное описание подзадачи 2", Status.DONE, 3), 6);
        assertEquals(taskManager.getEpicById(3).getStatus(), Status.DONE);

        taskManager.addNewEpic(new Epic("Эпик 3 (ИД 9)", "описание эпика 3"));
        assertSame(taskManager.getEpicById(9).getStatus(), Status.NEW);
    }

    @Test
    @Order(4)
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
    @Order(5)
    public void checkingForGetSubtaskForEpic() {
        Subtask subtask1 = (new Subtask("подзадача 3 для эпика 2 (ИД 7)",
                "описание подзадачи 3", Status.NEW, 4));
        subtask1.setId(7);
        Subtask subtask2 = (new Subtask("подзадача 4 для эпика 2 (ИД 8)",
                "описание подзадачи 4", Status.NEW, 4));
        subtask2.setId(8);
        List<Task> subtaskList = List.of(subtask1, subtask2);

        assertEquals(subtaskList, taskManager.getSubtaskForEpic(4));
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