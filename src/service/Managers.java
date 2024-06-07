package service;

import server.adapters.DurationAdapter;
import server.adapters.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;

public class Managers {

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static FileBackedTaskManager getDefaultBackedTaskManager() {
        return new FileBackedTaskManager(new File("resources\\data.csv"));
    }

    public static Gson getGson() {
        GsonBuilder builder = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter());
        return builder.create();
    }
}