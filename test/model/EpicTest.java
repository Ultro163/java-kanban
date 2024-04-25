package model;

import org.junit.jupiter.api.Test;
import service.Managers;
import service.TaskManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    static TaskManager taskManager = Managers.getDefault();

    @Test
    void getSubtaskListId() {
        taskManager.addNewEpic(new Epic("Эпик 1 (ИД 3)", "описание эпика 1"));
        taskManager.addNewSubtask(new Subtask("подзадача 1 для эпика 1 (ИД 2)",
                "описание подзадачи 1", Status.NEW, 1));
        taskManager.addNewSubtask(new Subtask("подзадача 2 для эпика 1 (ИД 3)",
                "описание подзадачи 2", Status.NEW, 1));
        taskManager.addNewSubtask(new Subtask("подзадача 3 для эпика 1 (ИД 4)",
                "описание подзадачи 3", Status.NEW, 1));

        List<Integer> testList = List.of(2, 4);

        taskManager.removeSubtaskById(3);
        assertEquals(testList, taskManager.getEpicById(1).getSubtaskListId());
    }
}