package model;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subtaskListId;
    private LocalDateTime endTime;

    public Epic(String taskName, String taskDescription) {
        super(taskName, taskDescription);
        setStatus(Status.NEW);
        this.subtaskListId = new ArrayList<>();
    }

    public Epic(String taskName, String taskDescription, Status status) {
        super(taskName, taskDescription, status);
        this.subtaskListId = new ArrayList<>();
    }

    public void setSubtaskListId(ArrayList<Integer> subtaskListId) {
        this.subtaskListId = subtaskListId;
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
