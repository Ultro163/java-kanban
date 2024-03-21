import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private int generatedId = 1;

    public void addNewTask(Task task) {
        task.setId(generatedId);
        this.tasks.put(task.getId(), task);
        generatedId++;
    }

    public void addNewSubtask(Subtask subtask) {
        subtask.setId(generatedId);
        this.subtasks.put(subtask.getId(), subtask);

        Epic currentEpic = epics.get(subtask.getEpicId());
        currentEpic.getSubtaskListId().add(subtask.getId());
        generatedId++;
    }

    public void addNewEpic(Epic epic) {
        epic.setId(generatedId);
        epics.put(epic.getId(), epic);
        generatedId++;
    }

    public void checkStatus(Epic epic) {
        int counterNew = 0;
        int counterInProgress = 0;
        int counterDone = 0;
        for (Integer subtaskId : epic.getSubtaskListId()) {
            Subtask currentSubtask = subtasks.get(subtaskId);
            if (currentSubtask.getStatus() == Status.NEW) {
                counterNew++;
            } else if (currentSubtask.getStatus() == Status.IN_PROGRESS) {
                counterInProgress++;
            } else if (currentSubtask.getStatus() == Status.DONE) {
                counterDone++;
            }
        }
        if (counterNew >= 0 && counterInProgress == 0 && counterDone == 0) {
            epics.get(epic.getId()).setStatus(Status.NEW);
        } else if (counterDone > 0 && counterNew == 0 && counterInProgress == 0) {
            epics.get(epic.getId()).setStatus(Status.DONE);
        } else {
            epics.get(epic.getId()).setStatus(Status.IN_PROGRESS);
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
            newTask.setId(idTask);
            tasks.put(idTask, newTask);
        } else {
            System.out.println("Такой задачи нет.");
        }
    }

    public void newVersionSubtask(Subtask newSubtask, int idSubtask) {
        if (subtasks.containsKey(idSubtask)) {
            newSubtask.setId(idSubtask);
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

            newEpic.setId(idEpic);
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
        for (Integer id : epics.get(idEpic).getSubtaskListId()) {
            subtasks.remove(id);
        }
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
