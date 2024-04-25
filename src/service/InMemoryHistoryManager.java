package service;

import model.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private final Map<Integer, Node<Task>> taskHistory;
    private Node<Task> head;
    private Node<Task> tail;

    public InMemoryHistoryManager() {
        this.tail = null;
        this.head = null;
        this.taskHistory = new HashMap<>();
    }

    @Override
    public void addHistory(Task task) {
        if (task == null) {
            return;
        }
        remove(task.getId());
        linkLast(task);
        taskHistory.put(task.getId(), tail);
    }

    private void linkLast(Task task) {
        final Node<Task> oldTail = tail;
        final Node<Task> newNode = new Node<>(tail, task, null);
        tail = newNode;
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.next = newNode;
        }
    }

    private void removeNode(Node<Task> node) {
        final Node<Task> backNode = node.prev;
        final Node<Task> nextNode = node.next;

        if (backNode != null) {
            backNode.next = nextNode;
        }
        if (nextNode != null) {
            nextNode.prev = backNode;
        }

        if (head == node) {
            head = nextNode;
        }
        if (tail == node) {
            tail = backNode;
        }
    }

    @Override
    public void remove(int id) {
        if (taskHistory.containsKey(id)) {
            removeNode(taskHistory.remove(id));
        }
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    private List<Task> getTasks() {
        Node<Task> node = head;
        final List<Task> historyTasks = new ArrayList<>();

        while (node != null) {
            historyTasks.add(node.data);
            node = node.next;
        }
        return historyTasks;
    }
}
