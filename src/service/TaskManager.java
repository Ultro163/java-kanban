package service;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {

    List<Task> getHistory();

    void addNewTask(Task task);

    void addNewSubtask(Subtask subtask);

    void addNewEpic(Epic epic);

    void checkStatus(Epic epic);

    List<Task> getSubtaskForEpic(int epicId);

    void updateTask(Task newTask, int idTask);

    void updateSubtask(Subtask newSubtask, int idSubtask);

    void updateEpic(Epic newEpic, int idEpic);

    void removeAllTasks();

    void removeTaskById(int idTask);

    void removeSubtaskById(int idSubtask);

    void removeEpicById(int idEpic);

    Task getTaskById(int idTask);

    Subtask getSubtaskById(int idSubtask);

    Epic getEpicById(int idEpic);

    ArrayList<Task> getAllTasks();

    ArrayList<Subtask> getAllSubtasks();

    ArrayList<Epic> getAllEpics();
}
