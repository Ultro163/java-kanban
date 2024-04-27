import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import service.Managers;
import service.TaskManager;

public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = Managers.getDefault();
        taskManager.addNewTask(new Task("Задача 1", "описание задачи 1", Status.NEW));
        taskManager.addNewTask(new Task("Задача 2", "описание задачи 2", Status.NEW));

        taskManager.addNewEpic(new Epic("Эпик 1 (ИД 3)", "описание эпика 1"));
        taskManager.addNewEpic(new Epic("Эпик 2 (ИД 4)", "описание эпика 2"));


        taskManager.addNewSubtask(new Subtask("подзадача 1 для эпика 1 (ИД 5)",
                "описание подзадачи 1", Status.NEW, 3));
        taskManager.addNewSubtask(new Subtask("подзадача 2 для эпика 1 (ИД 6)",
                "описание подзадачи 2", Status.NEW, 3));
        taskManager.addNewSubtask(new Subtask("подзадача 3 для эпика 1 (ИД 7)",
                "описание подзадачи 3", Status.NEW, 3));

        taskManager.getSubtaskById(5);
        taskManager.getSubtaskById(6);
        taskManager.getSubtaskById(7);
        taskManager.getEpicById(4);
        taskManager.getEpicById(3);
        taskManager.getTaskById(1);

        System.out.println(taskManager.getHistory());

        taskManager.getSubtaskById(5);
        taskManager.getTaskById(1);
        System.out.println(taskManager.getHistory());

        taskManager.removeSubtaskById(5);
        taskManager.removeEpicById(3);
        System.out.println(taskManager.getHistory());

        taskManager.getSubtaskById(5);
        taskManager.getSubtaskById(6);
        taskManager.getSubtaskById(7);
        taskManager.getEpicById(4);
        taskManager.getEpicById(3);
        taskManager.getTaskById(1);

        taskManager.removeAllTasks();
        System.out.println(taskManager.getHistory());
    }
}
