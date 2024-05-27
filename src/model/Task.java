package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    protected String taskName;
    protected String taskDescription;
    protected int id;
    protected Duration duration;
    protected LocalDateTime startTime;
    private Status status;

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public Task() {

    }

    public Task(String taskName, String taskDescription, Status status) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.status = status;
    }

    public Task(String taskName, String taskDescription) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
    }

    public Task(String taskName, String taskDescription, LocalDateTime startTime, Duration duration) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(String taskName, String taskDescription, Status status, LocalDateTime startTime, Duration duration) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(duration);
    }

    public TaskType getType() {
        return TaskType.TASK;
    }

    public String getTaskName() {
        return taskName;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public int getId() {
        return id;
    }

    public Status getStatus() {
        return status;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null) return false;
        if (this.getClass() != object.getClass()) return false;
        Task otherTask = (Task) object;
        return Objects.equals(taskName, otherTask.taskName) &&
                Objects.equals(taskDescription, otherTask.taskDescription);
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return "Задача {" +
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
