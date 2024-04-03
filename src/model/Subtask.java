package model;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String taskName, String taskDescription, Status status, int epicId) {
        super(taskName, taskDescription, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "Подзадача {" +
                "id=" + getId() +
                ", Название='" + getTaskName() + '\'' +
                ", Описание='" + getTaskDescription() + '\'' +
                ", Статус='" + getStatus() + '\'' +
                "}" +
                "\n";
    }
}
