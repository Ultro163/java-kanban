import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subtaskListId = new ArrayList<>();

    public Epic(String taskName, String taskDescription) {
        super(taskName, taskDescription);
        setStatus(Status.NEW);
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
                "}" +
                "\n";
    }
}
