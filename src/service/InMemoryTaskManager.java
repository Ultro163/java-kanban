package service;

import exceptions.NotFoundTaskException;
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
        epic.setId(generatedId);
        checkStatus(epic);
        setTimeEpic(epic);
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
        if (epic.getSubtaskListId().isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }
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
        Epic epic = epics.get(epicId);
        if (epic == null) {
            throw new NotFoundTaskException();
        }
        ArrayList<Subtask> subtasksList = new ArrayList<>();

        epic.getSubtaskListId().stream()
                .map(subtasks::get)
                .forEach(subtasksList::add);

        return subtasksList;
    }

    @Override
    public void updateTask(Task newTask, int taskId) {
        if (tasks.get(taskId) != null) {
            newTask.setId(taskId);
            Task oldTask = tasks.get(taskId);
            if (newTask.getStartTime() != null) {
                if (oldTask.getStartTime() != null) {
                    prioritizedTasks.remove(oldTask);
                }
                if (searchIsOverlappedTask(newTask)) {
                    prioritizedTasks.add(newTask);
                } else {
                    if (oldTask.getStartTime() != null) {
                        prioritizedTasks.add(oldTask);
                    }
                    throw new TimeOverlapException("Задача не может быть добавлена. Пересечение по времени.");
                }
            }
            tasks.put(taskId, newTask);
        } else {
            throw new NotFoundTaskException();
        }
    }

    @Override
    public void updateSubtask(Subtask newSubtask, int subtaskId) {
        if (subtasks.get(subtaskId) != null) {
            newSubtask.setId(subtaskId);
            Subtask oldSubtask = subtasks.get(subtaskId);
            if (newSubtask.getStartTime() != null) {
                if (oldSubtask.getStartTime() != null) {
                    prioritizedTasks.remove(oldSubtask);
                }
                if (searchIsOverlappedTask(newSubtask)) {
                    prioritizedTasks.add(newSubtask);
                } else {
                    if (oldSubtask.getStartTime() != null) {
                        prioritizedTasks.add(oldSubtask);

                    }
                    throw new TimeOverlapException("Задача не может быть обновлена. Пересечение по времени.");
                }
            }
            subtasks.put(subtaskId, newSubtask);

            if (oldSubtask.getStartTime() != null && newSubtask.getStartTime() == null) {
                prioritizedTasks.remove(oldSubtask);
            }

            Epic oldEpic = epics.get(oldSubtask.getEpicId());
            Epic newEpic = epics.get(newSubtask.getEpicId());
            if (oldSubtask.getEpicId() != newSubtask.getEpicId()) {
                oldEpic.getSubtaskListId().removeIf(id -> id == subtaskId);
                newEpic.getSubtaskListId().add(subtaskId);
                checkStatus(oldEpic);
                setTimeEpic(oldEpic);
            }
            checkStatus(newEpic);
            setTimeEpic(newEpic);
        } else {
            throw new NotFoundTaskException();
        }
    }

    @Override
    public void updateEpic(Epic newEpic, int epicId) {
        if (epics.get(epicId) != null) {
            ArrayList<Integer> listID = epics.get(epicId).getSubtaskListId();
            newEpic.getSubtaskListId().addAll(listID);

            newEpic.setId(epicId);
            epics.put(epicId, newEpic);
            checkStatus(newEpic);
            setTimeEpic(newEpic);
        } else {
            throw new NotFoundTaskException();
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
        if (tasks.get(taskId).getStartTime() != null) {
            prioritizedTasks.remove(tasks.get(taskId));
        }
        tasks.remove(taskId);
    }

    @Override
    public void removeSubtaskById(int subtaskId) {
        historyManager.remove(subtaskId);
        int epicId = subtasks.get(subtaskId).getEpicId();

        epics.get(epicId).getSubtaskListId().removeIf(id -> id == subtaskId);
        checkStatus(epics.get(epicId));

        if (subtasks.get(subtaskId).getStartTime() != null) {
            prioritizedTasks.remove(subtasks.get(subtaskId));
            setTimeEpic(epics.get(epicId));
        }
        subtasks.remove(subtaskId);
    }

    @Override
    public void removeEpicById(int epicId) {
        historyManager.remove(epicId);
        epics.get(epicId).getSubtaskListId().forEach(historyManager::remove);
        epics.get(epicId).getSubtaskListId().stream()
                .map(subtasks::get)
                .filter(subtask -> subtask.getStartTime() != null)
                .forEach(prioritizedTasks::remove);
        epics.get(epicId).getSubtaskListId().forEach(subtasks::remove);
        epics.remove(epicId);
    }

    @Override
    public Task getTaskById(int taskId) {
        Task task = tasks.get(taskId);
        if (task == null) {
            throw new NotFoundTaskException();
        }
        historyManager.addHistory(task);
        return task;
    }

    @Override
    public Subtask getSubtaskById(int subtaskId) {
        Subtask subtask = subtasks.get(subtaskId);
        if (subtask == null) {
            throw new NotFoundTaskException();
        }
        historyManager.addHistory(subtask);
        return subtask;
    }

    @Override
    public Epic getEpicById(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            throw new NotFoundTaskException();
        }
        historyManager.addHistory(epic);
        return epic;
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
