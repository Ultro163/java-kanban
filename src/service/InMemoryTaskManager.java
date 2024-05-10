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

    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    protected final HashMap<Integer, Epic> epics = new HashMap<>();

    protected int generatedId = 1;

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
    public List<Task> getSubtaskForEpic(int epicId) {
        Epic epic1 = epics.get(epicId);
        ArrayList<Task> subtasksList = new ArrayList<>();
        try {
            for (Integer subId : epic1.getSubtaskListId()) {
                subtasksList.add(subtasks.get(subId));
            }
        } catch (NullPointerException e) {
            System.out.println("Такого эпика нет.");
        }

        return subtasksList;
    }

    @Override
    public void updateTask(Task newTask, int taskId) {
        if (tasks.containsKey(taskId)) {
            newTask.setId(taskId);
            tasks.put(taskId, newTask);
        } else {
            System.out.println("Такой задачи нет.");
        }
    }

    @Override
    public void updateSubtask(Subtask newSubtask, int subtaskId) {
        if (subtasks.containsKey(subtaskId)) {
            newSubtask.setId(subtaskId);
            subtasks.put(subtaskId, newSubtask);

            checkStatus(epics.get(newSubtask.getEpicId()));
        } else {
            System.out.println("Такой подзадачи нет.");
        }
    }

    @Override
    public void updateEpic(Epic newEpic, int epicId) {
        if (epics.containsKey(epicId)) {
            ArrayList<Integer> listID = epics.get(epicId).getSubtaskListId();
            newEpic.getSubtaskListId().addAll(listID);

            newEpic.setId(epicId);
            epics.put(epicId, newEpic);
            checkStatus(newEpic);
        } else {
            System.out.println("Такого эпика нет");
        }

    }

    @Override
    public void removeAllTasks() {
        for (Task task : tasks.values()) {
            historyManager.remove(task.getId());
        }
        tasks.clear();
        for (Task task : subtasks.values()) {
            historyManager.remove(task.getId());
        }
        subtasks.clear();
        for (Epic epic : epics.values()) {
            historyManager.remove(epic.getId());
        }
        epics.clear();
    }

    @Override
    public void removeTaskById(int taskId) {
        historyManager.remove(taskId);
        tasks.remove(taskId);
    }

    @Override
    public void removeSubtaskById(int subtaskId) {
        historyManager.remove(subtaskId);
        int subtasksId = subtasks.get(subtaskId).getEpicId();
        subtasks.remove(subtaskId);
        Epic epic = epics.get(subtasksId);
        for (int i = 0; i < epic.getSubtaskListId().size(); i++) {
            if (epic.getSubtaskListId().get(i) == subtaskId) {
                epic.getSubtaskListId().remove(i);
                break;
            }
        }
        checkStatus(epic);
    }

    @Override
    public void removeEpicById(int epicId) {
        historyManager.remove(epicId);
        for (Integer id : epics.get(epicId).getSubtaskListId()) {
            historyManager.remove(id);
            subtasks.remove(id);
        }
        epics.remove(epicId);
    }

    @Override
    public Task getTaskById(int taskId) {
        historyManager.addHistory(tasks.get(taskId));
        return tasks.get(taskId);
    }

    @Override
    public Subtask getSubtaskById(int subtaskId) {
        historyManager.addHistory(subtasks.get(subtaskId));
        return subtasks.get(subtaskId);
    }

    @Override
    public Epic getEpicById(int epicId) {
        historyManager.addHistory(epics.get(epicId));
        return epics.get(epicId);

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
