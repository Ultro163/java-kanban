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


        taskManager.addNewEpic(new Epic("epic3", "des epic1"));
        taskManager.addNewEpic(new Epic("epic4", "des epic 2"));
        taskManager.addNewEpic(new Epic("epic5", "des epic 2"));

        taskManager.addNewSubtask(new Subtask("подзадача 1 для эпика 3", "des", Status.NEW, 3));
        taskManager.addNewSubtask(new Subtask("подзадача 2 для эпика 3", "des", Status.NEW, 3));
        taskManager.addNewSubtask(new Subtask("подзадача 1 для эпика 4", "des", Status.NEW, 4));
        taskManager.addNewSubtask(new Subtask("подзадача 2 для эпика 4", "des", Status.NEW, 4));
        taskManager.addNewSubtask(new Subtask("подзадача 1 для эпика 5", "des", Status.NEW, 5));

//        System.out.println(taskManager.getTask());
//        System.out.println("Удалил задачу 2");
//        taskManager.removeTaskById(2);
//        System.out.println(taskManager.getTask());
//        System.out.println();
        System.out.println("Получаем задачу по ИД");
        System.out.println(taskManager.getTaskById(1));
//        System.out.println();
        System.out.println("Получаем СУБзадачу по ИД");
        System.out.println(taskManager.getSubtaskById(7));
//        System.out.println();
        System.out.println("Получаем ЭПИК по ИД");
        System.out.println(taskManager.getEpicById(3));
//        System.out.println("новые эпики");
//        System.out.println(taskManager.getEpic());
//        System.out.println();
//        System.out.println("Новые подзадачи");
//        System.out.println(taskManager.getSubtask());
//        System.out.println();
//        System.out.println("Запрос подзадач эпика ид 3");
//        taskManager.getSubtaskForEpic(3);
        System.out.println();
        System.out.println(taskManager.getHistory());

//        taskManager.getSubtaskForEpic(4);
//        System.out.println("Заменил обычную задачу 2");
//        taskManager.newVersionTask(new Task("task3", "des2", Status.IN_PROGRESS), 1);
//        System.out.println(taskManager.getTask());
//        System.out.println();
//        System.out.println("Заменил подзадачу 1 для эпика ид3");
//        taskManager.newVersionSubtask(new Subtask("sub13", "des", Status.DONE, 3), 6);
//        System.out.println(taskManager.getSubtask());
//        taskManager.getSubtaskForEpic(3);
//        System.out.println(taskManager.getEpicById(3));
//        System.out.println();
//        System.out.println("Меняю эпик ид 3");
//        taskManager.newVersionEpic(new Epic("EPIK zamena", "new opis"), 3);
//        System.out.println(taskManager.getEpic());
//        System.out.println("Получаем ЭПИК по ИД");
//        System.out.println(taskManager.getEpicById(3));
//        taskManager.getSubtaskForEpic(3);
//        System.out.println();
//        System.out.println("Удаляю подзадачу 7");
//        taskManager.removeSubtaskById(7);
//        System.out.println(taskManager.getSubtask());
//        taskManager.getSubtaskForEpic(3);
//        System.out.println("Получаем СУБзадачу по ИД");
//        System.out.println(taskManager.getSubtaskById(6));
//        System.out.println();
//        System.out.println("Удаляем эпик 4");
//        taskManager.removeEpicById(4);
//        System.out.println();
//        System.out.println("Смотрим субы эпика 3");
//        taskManager.getSubtaskForEpic(4);
//        System.out.println(taskManager.getEpicById(3));

    }
}
