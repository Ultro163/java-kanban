import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private int generateID = 1;

    public void addNewTask(Task task) {
        task.setTaskId(generateID);
        this.tasks.put(task.getTaskId(), task);
        generateID++;
    }

    public void addNewSubtask(Subtask subtask) {
        subtask.setTaskId(generateID);
        this.subtasks.put(subtask.getTaskId(), subtask);

        Epic currentEpic = epics.get(subtask.getEpicId());
        currentEpic.getSubtaskListId().add(subtask.getTaskId());
        generateID++;
    }

    public void addNewEpic(Epic epic) {
        epic.setTaskId(generateID);
        epic.setStatus(Status.NEW);
        epics.put(epic.getTaskId(), epic);
        generateID++;
    }

    public void checkStatus(Epic epic) {
        int counterNEW = 0;
        int counterIN_PROGRESS = 0;
        int counterDONE = 0;
        for (Integer subtaskId : epic.getSubtaskListId()) {
            Subtask currentSubtask = subtasks.get(subtaskId);
            if (currentSubtask.getStatus() == Status.NEW) {
                counterNEW++;
            } else if (currentSubtask.getStatus() == Status.IN_PROGRESS) {
                counterIN_PROGRESS++;
            } else if (currentSubtask.getStatus() == Status.DONE) {
                counterDONE++;
            }
        }
        if (counterNEW >= 0 && counterIN_PROGRESS == 0 && counterDONE == 0) {
            epics.get(epic.getTaskId()).setStatus(Status.NEW);
        } else if (counterDONE > 0 && counterNEW == 0 && counterIN_PROGRESS == 0) {
            epics.get(epic.getTaskId()).setStatus(Status.DONE);
        } else {
            epics.get(epic.getTaskId()).setStatus(Status.IN_PROGRESS);
        }
    }

    public void getSubtaskForEpic(int epicId) {
        if (epics.containsKey(epicId)) {
            Epic epic1 = epics.get(epicId);
            ArrayList<Subtask> subtasksList = new ArrayList<>();
            for (Integer subId : epic1.getSubtaskListId()) {
                subtasksList.add(subtasks.get(subId));
            }
            System.out.println(subtasksList);
        } else {
            System.out.println("Такого эпика нет.");
        }
    }

    public void newVersionTask(Task newTask, int idTask) {
        if (tasks.containsKey(idTask)) {
            newTask.setTaskId(idTask);
            tasks.put(idTask, newTask);
        } else {
            System.out.println("Такой задачи нет.");
        }
    }

    public void newVersionSubtask(Subtask newSubtask, int idSubtask) {
        if (subtasks.containsKey(idSubtask)) {
            newSubtask.setTaskId(idSubtask);
            subtasks.put(idSubtask, newSubtask);

            checkStatus(epics.get(newSubtask.getEpicId()));
        } else {
            System.out.println("Такой подзадачи нет.");
        }
    }

    public void newVersionEpic(Epic newEpic, int idEpic) {
        if (epics.containsKey(idEpic)) {
            ArrayList<Integer> listID = epics.get(idEpic).getSubtaskListId();
            newEpic.getSubtaskListId().addAll(listID);

            newEpic.setTaskId(idEpic);
            epics.put(idEpic, newEpic);
            checkStatus(newEpic);
        } else {
            System.out.println("Такого эпика нет");
        }

    }

    public void removeAllTasks() {
        tasks.clear();
        subtasks.clear();
        epics.clear();
    }

    public void removeTaskById(int idTask) {
        tasks.remove(idTask);
    }

    public void removeSubtaskById(int idSubtask) {
        int subtasksId = subtasks.get(idSubtask).getEpicId();
        subtasks.remove(idSubtask);
        Epic epic = epics.get(subtasksId);
        for (int i = 0; i < epic.getSubtaskListId().size(); i++) {
            if (epic.getSubtaskListId().get(i) == idSubtask) {
                epic.getSubtaskListId().remove(i);
                break;
            }
        }
        checkStatus(epic);
    }

    public void removeEpicById(int idEpic) {
        epics.remove(idEpic);
    }

    public Task getTaskById(int idTask) {
        return tasks.get(idTask);
    }

    public Subtask getSubtaskById(int idSubtask) {
        return subtasks.get(idSubtask);
    }

    public Epic getEpicById(int idEpic) {
        return epics.get(idEpic);
    }

    public ArrayList<Task> getTask() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Subtask> getSubtask() {
        return new ArrayList<>(subtasks.values());
    }

    public ArrayList<Epic> getEpic() {
        return new ArrayList<>(epics.values());
    }
}
