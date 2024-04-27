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

    void updateTask(Task newTask, int taskId);

    void updateSubtask(Subtask newSubtask, int subtaskId);

    void updateEpic(Epic newEpic, int epicId);

    void removeAllTasks();

    void removeTaskById(int taskId);

    void removeSubtaskById(int subtaskId);

    void removeEpicById(int epicId);

    Task getTaskById(int taskId);

    Subtask getSubtaskById(int subtaskId);

    Epic getEpicById(int epicId);

    ArrayList<Task> getAllTasks();

    ArrayList<Subtask> getAllSubtasks();

    ArrayList<Epic> getAllEpics();
}
