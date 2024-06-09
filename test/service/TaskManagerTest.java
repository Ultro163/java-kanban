package service;

import exceptions.NotFoundTaskException;
import exceptions.TimeOverlapException;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;

    void addTasks() {
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
    void addNewTask() {
        addTasks();
        assertFalse(taskManager.getAllTasks().isEmpty(), "Задачи не добавились");
    }

    @Test
    void addNewSubtask() {
        addTasks();
        assertFalse(taskManager.getAllSubtasks().isEmpty(), "Задачи не добавились");
    }

    @Test
    void addNewEpic() {
        addTasks();
        assertFalse(taskManager.getAllEpics().isEmpty(), "Задачи не добавились");
    }

    @Test
    void checkStatus() {
        addTasks();
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
    void getSubtaskForEpic() {
        addTasks();

        Subtask subtask1 = (new Subtask("подзадача 3 для эпика 2 (ИД 7)",
                "описание подзадачи 3", Status.NEW, 4));
        subtask1.setId(7);
        Subtask subtask2 = (new Subtask("подзадача 4 для эпика 2 (ИД 8)",
                "описание подзадачи 4", Status.NEW, 4));
        subtask2.setId(8);
        List<Task> subtaskList = List.of(subtask1, subtask2);

        assertEquals(subtaskList, taskManager.getSubtaskForEpic(4));
    }

    @Test
    void updateTask() {
        addTasks();
        final Task task0 = new Task("Задача 3",
                "описание задачи 3", Status.DONE,
                LocalDateTime.of(2024, 5, 5, 5, 0),
                Duration.ofHours(2));
        taskManager.addNewTask(task0);

        final Task task = new Task("Измененная Задача 1",
                "обновленное описание задачи 1", Status.DONE);
        taskManager.updateTask(task, 1);
        assertEquals(task, taskManager.getTaskById(1), "Задача не обновилась");

        final Task task2 = new Task("Измененная Задача 1",
                "обновленное описание задачи 1", Status.DONE,
                LocalDateTime.of(2024, 5, 6, 5, 0),
                Duration.ofHours(2));

        taskManager.updateTask(task2, 1);
        assertEquals(task2, taskManager.getPrioritizedTasks().getLast(), "Задача не обновлена.");

        final Task task3 = new Task("Измененная Задача 1",
                "обновленное описание задачи 1", Status.DONE,
                LocalDateTime.of(2024, 5, 5, 6, 0),
                Duration.ofHours(2));

        Assertions.assertThrows(TimeOverlapException.class, () ->
                        taskManager.updateTask(task3, 1),
                "Задача не может быть обновлена. Пересечение по времени.");

        assertEquals(task2, taskManager.getTaskById(1));
        assertEquals(task2, taskManager.getPrioritizedTasks().getLast(), "Задача обновлена.");
    }

    @Test
    void updateSubtask() {
        addTasks();

        final Subtask subtask0 = new Subtask("Измененная ПодЗадача 1",
                "обновленное описание ПодЗадачи 1", Status.DONE, 3,
                LocalDateTime.of(2024, 5, 5, 5, 0),
                Duration.ofHours(1));
        taskManager.addNewSubtask(subtask0);

        final Subtask subtask = new Subtask("Измененная подзадача 1 для эпика 1 (ИД 5)",
                "Измененное описание подзадачи 1", Status.DONE, 3);
        taskManager.updateSubtask(subtask, 5);
        assertEquals(subtask, taskManager.getSubtaskById(5), "Задача не обновилась");

        final Subtask subtask2 = new Subtask("Измененная ПодЗадача 1",
                "обновленное описание ПодЗадачи 1", Status.DONE, 3,
                LocalDateTime.of(2024, 5, 6, 5, 0),
                Duration.ofHours(1));

        taskManager.updateSubtask(subtask2, 5);
        assertEquals(subtask2, taskManager.getPrioritizedTasks().getFirst(), "Задача не обновлена.");

        final Subtask subtask3 = new Subtask("Измененная ПодЗадача 1",
                "ообновленное описание ПодЗадачи 1", Status.DONE, 3,
                LocalDateTime.of(2024, 5, 5, 4, 0),
                Duration.ofHours(2));

        Assertions.assertThrows(TimeOverlapException.class, () ->
                        taskManager.updateSubtask(subtask3, 5),
                "Задача не может быть обновлена. Пересечение по времени.");

        assertEquals(subtask2, taskManager.getSubtaskById(5));
    }

    @Test
    void updateEpic() {
        addTasks();
        final Epic epic = new Epic("Измененный Эпик 1 (ИД 3)", " Измененное описание эпика 1");
        taskManager.updateEpic(epic, 3);
        assertEquals(epic, taskManager.getEpicById(3), "Задача не обновилась");
    }

    @Test
    void removeAllTasks() {
        addTasks();
        taskManager.removeAllTasks();
        assertTrue(taskManager.getAllTasks().isEmpty(), "Задачи не удалились");
        assertTrue(taskManager.getAllSubtasks().isEmpty(), "Задачи не удалились");
        assertTrue(taskManager.getAllEpics().isEmpty(), "Задачи не удалились");
    }

    @Test
    void removeTaskById() {
        addTasks();
        taskManager.removeTaskById(2);
        Assertions.assertThrows(NotFoundTaskException.class, () ->
                taskManager.getTaskById(2), "Задача не удалена");
    }

    @Test
    void removeSubtaskById() {
        addTasks();
        taskManager.removeSubtaskById(5);
        Assertions.assertThrows(NotFoundTaskException.class, () ->
                taskManager.getSubtaskById(5), "Задача не удалена");
    }

    @Test
    void removeEpicById() {
        addTasks();
        taskManager.removeEpicById(3);
        Assertions.assertThrows(NotFoundTaskException.class, () ->
                taskManager.getEpicById(3), "Задача не удалена");
    }

    @Test
    void checkingForIsOverlappedTask() {
        taskManager.addNewTask(new Task("Задача 2", "описание задачи 2", Status.NEW,
                LocalDateTime.of(2024, 5, 21, 17, 0), Duration.ofHours(4)));

        Assertions.assertThrows(TimeOverlapException.class, () ->
                taskManager.addNewTask(new Task("Задача 3", "описание задачи 3",
                        Status.NEW,
                        LocalDateTime.of(2024, 5, 21, 17, 0),
                        Duration.ofHours(3))), "Задача не может быть добавлена. Пересечение по времени.");

        Assertions.assertDoesNotThrow(() -> taskManager.addNewTask(new Task("Задача 6",
                "описание задачи 6",
                Status.NEW,
                LocalDateTime.of(2024, 5, 21, 15, 0),
                Duration.ofHours(1))), "Задача должна быть добавлена.");
    }

    @Test
    void checkingForSetTimeEpic() {
        addTasks();
        assertNull(taskManager.getEpicById(3).getStartTime(), "Время эпика должно отсутствовать.");

        final Subtask subtask = new Subtask("Измененная Задача 1",
                "обновленное описание задачи 1", Status.DONE, 3,
                LocalDateTime.of(2024, 5, 5, 5, 0),
                Duration.ofHours(1));

        LocalDateTime timeEpic = LocalDateTime.of(2024, 5, 5, 5, 0);

        taskManager.updateSubtask(subtask, 5);
        assertEquals(timeEpic, taskManager.getEpicById(3).getStartTime(), "Время не изменилось.");
    }
}