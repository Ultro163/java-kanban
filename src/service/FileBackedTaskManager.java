package service;

import exceptions.ManagerSaveException;
import model.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

public class FileBackedTaskManager extends InMemoryTaskManager {

    public static final String HEADER = "id,type,name,status,description,epic,dataTime,duration";
    private final File backFile;

    public FileBackedTaskManager(File file) {
        super();
        this.backFile = file;
    }

    public void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(backFile, StandardCharsets.UTF_8))) {
            writer.write(HEADER);
            writer.newLine();
            for (Task task : tasks.values()) {
                writer.write(toString(task));
                writer.newLine();
            }
            for (Epic epic : epics.values()) {
                writer.write(toString(epic));
                writer.newLine();
            }
            for (Subtask subtask : subtasks.values()) {
                writer.write(toString(subtask));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении файла.");
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            while (reader.ready()) {
                String line = reader.readLine();
                if (line.startsWith(HEADER)) {
                    continue;
                }
                Task task = fromString(line);
                manager.addTask(task);

                if (manager.generatedId <= task.getId()) {
                    manager.generatedId = task.getId() + 1;
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при загрузке файла.");
        }
        return manager;
    }

    public void addTask(Task task) {
        switch (task.getType()) {
            case TaskType.TASK:
                super.tasks.put(task.getId(), task);
                break;
            case TaskType.SUBTASK:
                Subtask subtask = (Subtask) task;
                final int epicId = subtask.getEpicId();
                super.subtasks.put(subtask.getId(), subtask);
                epics.get(epicId).getSubtaskListId().add(subtask.getId());
                super.setTimeEpic(epics.get(epicId));
                break;
            case TaskType.EPIC:
                Epic epic = (Epic) task;
                super.setTimeEpic(epic);
                super.epics.put(epic.getId(), epic);
                break;
        }
    }

    public String toString(Task task) {
        StringBuilder sb = new StringBuilder();
        sb.append(task.getId())
                .append(",")
                .append(task.getType())
                .append(",")
                .append(task.getTaskName())
                .append(",")
                .append(task.getStatus())
                .append(",")
                .append(task.getTaskDescription());

        if (task.getType() == TaskType.SUBTASK) {
            Subtask subtask = (Subtask) task;
            sb.append(",").append(subtask.getEpicId());
        }
        Optional<LocalDateTime> startTime = Optional.ofNullable(task.getStartTime());
        if (startTime.isPresent()) {
            sb.append(",").append(task.getStartTime()).append(",").append(task.getDuration());
        }
        return sb.toString();
    }

    public static Task fromString(String value) {
        Task task = new Task();
        String[] taskSplit = value.split(",");

        switch (TaskType.valueOf(taskSplit[1])) {
            case TaskType.TASK:
                if (taskSplit.length > 6) {
                    task = new Task(taskSplit[2], taskSplit[4], Status.valueOf(taskSplit[3]),
                            LocalDateTime.parse(taskSplit[6]),
                            Duration.parse(taskSplit[7]));
                    return task;
                }
                task = new Task(taskSplit[2], taskSplit[4], Status.valueOf(taskSplit[3]));
                task.setId(Integer.parseInt(taskSplit[0]));
                return task;
            case TaskType.SUBTASK:
                if (taskSplit.length > 6) {
                    task = new Subtask(taskSplit[2], taskSplit[4], Status.valueOf(taskSplit[3]),
                            Integer.parseInt(taskSplit[5]),
                            LocalDateTime.parse(taskSplit[6]), Duration.parse(taskSplit[7]));
                    task.setId(Integer.parseInt(taskSplit[0]));
                    return task;
                }
                task = new Subtask(taskSplit[2], taskSplit[4], Status.valueOf(taskSplit[3]),
                        Integer.parseInt(taskSplit[5]));
                task.setId(Integer.parseInt(taskSplit[0]));
                return task;
            case TaskType.EPIC:
                task = new Epic(taskSplit[2], taskSplit[4], Status.valueOf(taskSplit[3]));
                task.setId(Integer.parseInt(taskSplit[0]));
                return task;
        }
        return task;
    }

    @Override
    public void addNewTask(Task task) {
        super.addNewTask(task);
        save();
    }

    public void addNewSubtask(Subtask subtask) {
        super.addNewSubtask(subtask);
        save();
    }

    @Override
    public void addNewEpic(Epic epic) {
        super.addNewEpic(epic);
        save();
    }

    @Override
    public void updateTask(Task newTask, int taskId) {
        super.updateTask(newTask, taskId);
        save();
    }

    @Override
    public void updateSubtask(Subtask newSubtask, int subtaskId) {
        super.updateSubtask(newSubtask, subtaskId);
        save();
    }

    @Override
    public void updateEpic(Epic newEpic, int epicId) {
        super.updateEpic(newEpic, epicId);
        save();
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeTaskById(int taskId) {
        super.removeTaskById(taskId);
        save();
    }

    @Override
    public void removeSubtaskById(int subtaskId) {
        super.removeSubtaskById(subtaskId);
        save();
    }

    @Override
    public void removeEpicById(int epicId) {
        super.removeEpicById(epicId);
        save();
    }
}
