package service;

import model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> browsingHistory = new ArrayList<>();
    private static final int HISTORY_MAX_COUNT = 10;

    @Override
    public void addHistory(Task task) {
        if (browsingHistory.size() == HISTORY_MAX_COUNT) {
            browsingHistory.removeFirst();
        }
        browsingHistory.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return this.browsingHistory;
    }
}
