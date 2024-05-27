package service;

import exceptions.TimeOverlapException;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    HistoryManager historyManager = Managers.getDefaultHistory();

    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    protected final HashMap<Integer, Epic> epics = new HashMap<>();
    protected final Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));

    protected int generatedId = 1;

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    protected boolean searchIsOverlappedTask(Task task) {
        return getPrioritizedTasks().stream()
                .noneMatch(priorityTask -> isOverlapped(task, priorityTask));
    }

    protected boolean isOverlapped(Task task, Task priorityTask) {
        return priorityTask.getEndTime().isAfter(task.getStartTime())
                && task.getEndTime().isAfter(priorityTask.getStartTime());
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(historyManager.getHistory());
    }

    @Override
    public void addNewTask(Task task) {
        task.setId(generatedId);
        if (task.getStartTime() != null) {
            if (searchIsOverlappedTask(task)) {
                prioritizedTasks.add(task);
            } else {
                throw new TimeOverlapException("Задача не может быть добавлена. Пересечение по времени.");
            }
        }
        tasks.put(task.getId(), task);
        generatedId++;
    }

    @Override
    public void addNewSubtask(Subtask subtask) {
        try {
            subtask.setId(generatedId);
            if (subtask.getStartTime() != null) {
                if (searchIsOverlappedTask(subtask)) {
                    prioritizedTasks.add(subtask);
                    setTimeEpic(epics.get(subtask.getEpicId()));
                } else {
                    throw new TimeOverlapException("Задача не может быть добавлена. Пересечение по времени.");
                }
            }
            subtasks.put(generatedId, subtask);

            Epic currentEpic = epics.get(subtask.getEpicId());
            currentEpic.getSubtaskListId().add(subtask.getId());
            checkStatus(currentEpic);
            generatedId++;
        } catch (NullPointerException e) {
            System.out.println("Такого эпика нет.");
        }
    }

    @Override
    public void addNewEpic(Epic epic) {
        setTimeEpic(epic);
        epic.setId(generatedId);
        epics.put(epic.getId(), epic);
        generatedId++;
    }

    protected void setTimeEpic(Epic epic) {
        if (!epic.getSubtaskListId().isEmpty()) {

            Optional<LocalDateTime> starTime = epic.getSubtaskListId().stream()
                    .map(subtasks::get)
                    .filter(subtask -> subtask.getStartTime() != null)
                    .min(Comparator.comparing(Task::getStartTime))
                    .map(Task::getStartTime);

            Optional<LocalDateTime> endTime = epic.getSubtaskListId().stream()
                    .map(subtasks::get)
                    .filter(subtask -> subtask.getStartTime() != null)
                    .max(Comparator.comparing(Task::getEndTime))
                    .map(Task::getEndTime);

            if (starTime.isPresent() && endTime.isPresent()) {
                Duration duration = Duration.between(starTime.get(), endTime.get());
                epic.setDuration(duration);
            }
            starTime.ifPresent(epic::setStartTime);
            endTime.ifPresent(epic::setEndTime);
        } else {
            epic.setStartTime(null);
            epic.setEndTime(null);
            epic.setDuration(Duration.ZERO);
        }
    }

    protected void checkStatus(Epic epic) {
        boolean statusNew = epic.getSubtaskListId().stream()
                .map(subtasks::get)
                .anyMatch(subtask -> subtask.getStatus() == Status.NEW);
        boolean statusInProgress = epic.getSubtaskListId().stream()
                .map(subtasks::get)
                .anyMatch(subtask -> subtask.getStatus() == Status.IN_PROGRESS);
        boolean statusDone = epic.getSubtaskListId().stream()
                .map(subtasks::get)
                .anyMatch(subtask -> subtask.getStatus() == Status.DONE);

        if (statusNew && !statusInProgress && !statusDone) {
            epic.setStatus(Status.NEW);
        } else if (statusDone && !statusInProgress && !statusNew) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    @Override
    public List<Subtask> getSubtaskForEpic(int epicId) {
        ArrayList<Subtask> subtasksList = new ArrayList<>();
        try {
            epics.get(epicId).getSubtaskListId().stream()
                    .map(subtasks::get)
                    .forEach(subtasksList::add);

        } catch (NullPointerException e) {
            System.out.println("Такого эпика нет.");
        }
        return subtasksList;
    }

    @Override
    public void updateTask(Task newTask, int taskId) {
        if (tasks.containsKey(taskId)) {
            newTask.setId(taskId);
            if (newTask.getStartTime() != null) {
                if (prioritizedTasks.contains(newTask)) {
                    prioritizedTasks.remove(subtasks.get(taskId));
                }
                if (searchIsOverlappedTask(newTask)) {
                    prioritizedTasks.add(newTask);
                } else {
                    throw new TimeOverlapException("Задача не может быть добавлена. Пересечение по времени.");
                }
            }
            tasks.put(taskId, newTask);
        } else {
            System.out.println("Такой задачи нет.");
        }
    }

    @Override
    public void updateSubtask(Subtask newSubtask, int subtaskId) {
        if (subtasks.containsKey(subtaskId)) {
            newSubtask.setId(subtaskId);
            if (newSubtask.getStartTime() != null) {
                if (prioritizedTasks.contains(newSubtask)) {
                    prioritizedTasks.remove(subtasks.get(subtaskId));
                }
                if (searchIsOverlappedTask(newSubtask)) {
                    prioritizedTasks.add(newSubtask);
                    setTimeEpic(epics.get(newSubtask.getEpicId()));
                } else {
                    throw new TimeOverlapException("Задача не может быть обновлена. Пересечение по времени.");
                }
            }
            subtasks.put(subtaskId, newSubtask);

            Epic currentEpic = epics.get(newSubtask.getEpicId());
            checkStatus(currentEpic);
            setTimeEpic(currentEpic);
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
            setTimeEpic(newEpic);
        } else {
            System.out.println("Такого эпика нет");
        }
    }

    @Override
    public void removeAllTasks() {
        tasks.values().forEach(task -> historyManager.remove(task.getId()));
        tasks.clear();

        subtasks.values().forEach(subtask -> historyManager.remove(subtask.getId()));
        subtasks.clear();

        epics.values().forEach(epic -> historyManager.remove(epic.getId()));
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
        int epicId = subtasks.get(subtaskId).getEpicId();
        subtasks.remove(subtaskId);

        epics.get(epicId).getSubtaskListId().removeIf(id -> id == subtaskId);

        checkStatus(epics.get(epicId));
        setTimeEpic(epics.get(epicId));
    }

    @Override
    public void removeEpicById(int epicId) {
        historyManager.remove(epicId);
        epics.get(epicId).getSubtaskListId().forEach(historyManager::remove);
        epics.get(epicId).getSubtaskListId().forEach(subtasks::remove);
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
