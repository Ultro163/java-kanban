package model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private final int epicId;

    public Subtask(String taskName, String taskDescription, Status status, int epicId) {
        super(taskName, taskDescription, status);
        this.epicId = epicId;
    }

    public Subtask(String taskName, String taskDescription, Status status, int epicId,
                   LocalDateTime localDateTime, Duration duration) {
        super(taskName, taskDescription, status, localDateTime, duration);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public TaskType getType() {
        return TaskType.SUBTASK;
    }

    @Override
    public String toString() {
        return "Подзадача {" +
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
