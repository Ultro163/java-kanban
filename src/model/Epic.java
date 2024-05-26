package model;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> subtaskListId = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(String taskName, String taskDescription) {
        super(taskName, taskDescription);
        setStatus(Status.NEW);
    }

    public Epic(String taskName, String taskDescription, Status status) {
        super(taskName, taskDescription, status);
    }

    @Override
    public LocalDateTime getEndTime() {
        return this.endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public TaskType getType() {
        return TaskType.EPIC;
    }

    public ArrayList<Integer> getSubtaskListId() {
        return subtaskListId;
    }

    @Override
    public String toString() {
        return "Эпик {" +
                "id=" + getId() +
                ", Название='" + getTaskName() + '\'' +
                ", Описание='" + getTaskDescription() + '\'' +
                ", Статус='" + getStatus() + '\'' +
                ", Время начала задачи='" + getStartTime() + '\'' +
                ", Продолжительность задачи='" + getDuration() + '\'' +
                "}" +
                "\n";
    }
}
