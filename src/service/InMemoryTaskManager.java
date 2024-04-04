package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    HistoryManager historyManager = Managers.getDefaultHistory();
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();

    private int generatedId = 1;



    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(historyManager.getHistory());
    }

    @Override
    public void addNewTask(Task task) {
        task.setId(generatedId);
        this.tasks.put(task.getId(), task);
        generatedId++;
    }

    @Override
    public void addNewSubtask(Subtask subtask) {
        subtask.setId(generatedId);
        this.subtasks.put(subtask.getId(), subtask);

        Epic currentEpic = epics.get(subtask.getEpicId());
        currentEpic.getSubtaskListId().add(subtask.getId());
        generatedId++;
    }

    @Override
    public void addNewEpic(Epic epic) {
        epic.setId(generatedId);
        epics.put(epic.getId(), epic);
        generatedId++;
    }

    @Override
    public void checkStatus(Epic epic) {
        int counterNew = 0;
        int counterInProgress = 0;
        int counterDone = 0;
        for (Integer subtaskId : epic.getSubtaskListId()) {
            Subtask currentSubtask = subtasks.get(subtaskId);
            if (currentSubtask.getStatus() == Status.NEW) {
                counterNew++;
            } else if (currentSubtask.getStatus() == Status.IN_PROGRESS) {
                counterInProgress++;
            } else if (currentSubtask.getStatus() == Status.DONE) {
                counterDone++;
            }
        }
        if (counterNew >= 0 && counterInProgress == 0 && counterDone == 0) {
            epics.get(epic.getId()).setStatus(Status.NEW);
        } else if (counterDone > 0 && counterNew == 0 && counterInProgress == 0) {
            epics.get(epic.getId()).setStatus(Status.DONE);
        } else {
            epics.get(epic.getId()).setStatus(Status.IN_PROGRESS);
        }
    }

    @Override
    public void getSubtaskForEpic(int epicId) {
        if (epics.containsKey(epicId)) {
            Epic epic1 = epics.get(epicId);
            ArrayList<Subtask> subtasksList = new ArrayList<>();
            for (Integer subId : epic1.getSubtaskListId()) {
                subtasksList.add(subtasks.get(subId));
            }
            System.out.println(subtasksList);
        } else {
            System.out.println("Такого эпика нет.");
        }
    }

    @Override
    public void updateTask(Task newTask, int idTask) {
        if (tasks.containsKey(idTask)) {
            newTask.setId(idTask);
            tasks.put(idTask, newTask);
        } else {
            System.out.println("Такой задачи нет.");
        }
    }

    @Override
    public void updateSubtask(Subtask newSubtask, int idSubtask) {
        if (subtasks.containsKey(idSubtask)) {
            newSubtask.setId(idSubtask);
            subtasks.put(idSubtask, newSubtask);

            checkStatus(epics.get(newSubtask.getEpicId()));
        } else {
            System.out.println("Такой подзадачи нет.");
        }
    }

    @Override
    public void updateEpic(Epic newEpic, int idEpic) {
        if (epics.containsKey(idEpic)) {
            ArrayList<Integer> listID = epics.get(idEpic).getSubtaskListId();
            newEpic.getSubtaskListId().addAll(listID);

            newEpic.setId(idEpic);
            epics.put(idEpic, newEpic);
            checkStatus(newEpic);
        } else {
            System.out.println("Такого эпика нет");
        }

    }

    @Override
    public void removeAllTasks() {
        tasks.clear();
        subtasks.clear();
        epics.clear();
    }

    @Override
    public void removeTaskById(int idTask) {
        tasks.remove(idTask);
    }

    @Override
    public void removeSubtaskById(int idSubtask) {
        int subtasksId = subtasks.get(idSubtask).getEpicId();
        subtasks.remove(idSubtask);
        Epic epic = epics.get(subtasksId);
        for (int i = 0; i < epic.getSubtaskListId().size(); i++) {
            if (epic.getSubtaskListId().get(i) == idSubtask) {
                epic.getSubtaskListId().remove(i);
                break;
            }
        }
        checkStatus(epic);
    }

    @Override
    public void removeEpicById(int idEpic) {
        for (Integer id : epics.get(idEpic).getSubtaskListId()) {
            subtasks.remove(id);
        }
        epics.remove(idEpic);
    }

    @Override
    public Task getTaskById(int idTask) {
        historyManager.addHistory(tasks.get(idTask));
        return tasks.get(idTask);
    }

    @Override
    public Subtask getSubtaskById(int idSubtask) {
        historyManager.addHistory(subtasks.get(idSubtask));
        return subtasks.get(idSubtask);
    }

    @Override
    public Epic getEpicById(int idEpic) {
        historyManager.addHistory(epics.get(idEpic));
        return epics.get(idEpic);

    }

    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }
}
