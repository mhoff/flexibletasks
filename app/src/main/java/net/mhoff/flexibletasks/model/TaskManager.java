package net.mhoff.flexibletasks.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.util.Log;

import com.fatboyindustrial.gsonjodatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import org.joda.time.Period;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class TaskManager {

    private List<Task> tasks;
    private List<DataSetObserver> observers;

    private PersistenceManager pm;

    public TaskManager(Context context) {
        tasks = new ArrayList<>();
        observers = new ArrayList<>();
        pm = new PersistenceManager(context);
        tasks.addAll(pm.loadTasks());
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void addTask(Task newTask) {
        tasks.add(newTask);
        Collections.sort(tasks);
        notifyObservers();
        pm.persistTasks(tasks);
    }

    public void removeTask(Task oldTask) {
        if (tasks.contains(oldTask)) {
            tasks.remove(oldTask);
            notifyObservers();
            pm.persistTasks(tasks);
        } else {
            throw new IllegalArgumentException();
        }
    }

    public void executeTask(Task task) {
        task.setDone();
        Collections.sort(tasks);
        notifyObservers();
        pm.persistTasks(tasks);
    }

    public void toggleTaskEnabled(Task task) {
        task.setEnabled(!task.isEnabled());
        Collections.sort(tasks);
        notifyObservers();
        pm.persistTasks(tasks);
    }

    public void postponeTask(Task task, Period period, boolean permanent) {
        if (!permanent) {
            task.postponeOccurrence(period);
        } else {
            task.extendInterval(period);
        }
        notifyObservers();
    }

    private void notifyObservers() {
        for (DataSetObserver observer : observers) {
            observer.onChanged();
        }
    }

    public void registerObserver(DataSetObserver observer) {
        observers.add(observer);
    }

    public Task getNextDueTask() {
        return tasks.isEmpty() ? null : tasks.get(0);
    }

    static class PersistenceManager {

        private static final String PACKAGE_KEY = "net.mhoff.flexibletasks.storage";
        private static final String TASKS_KEY = "tasks";

        private final Context context;

        private final SharedPreferences loader;

        private final Gson gson;

        public PersistenceManager(Context context) {
            this.context = context;
            loader = context.getSharedPreferences(PACKAGE_KEY, Context.MODE_PRIVATE);
            gson = createGson();
        }

        private Gson createGson() {
            GsonBuilder gsonBuilder = new GsonBuilder();
            Converters.registerDateTime(gsonBuilder);
            gsonBuilder = gsonBuilder.registerTypeAdapter(new TypeToken<Period>() {
            }.getType(), new PeriodTypeConverter());
            return gsonBuilder.create();
        }

        private void writeTasksDump(String dump) {
            SharedPreferences.Editor writer = loader.edit();
            writer.putString(TASKS_KEY, dump);
            writer.apply();
        }

        private String loadTasksDump() {
            return loader.getString(TASKS_KEY, null);
        }

        public void persistTasks(Collection<Task> tasks) {
            String dump = gson.toJson(tasks);
            Log.i("FlexibleTasks", "Persisting Tasks: " + dump);
            writeTasksDump(dump);
        }

        public Collection<Task> loadTasks() {
            String dump = loadTasksDump();
            if (dump != null) {
                Type listType = new TypeToken<Collection<Task>>() {
                }.getType();
                return gson.fromJson(dump, listType);
            } else {
                return Collections.emptyList();
            }
        }

        private class PeriodTypeConverter implements JsonSerializer<Period>, JsonDeserializer<Period>, InstanceCreator<Period> {
            @Override
            public JsonElement serialize(Period src, Type srcType, JsonSerializationContext context) {
                return new JsonPrimitive(src.toString());
            }

            @Override
            public Period deserialize(JsonElement json, Type type, JsonDeserializationContext context)
                    throws JsonParseException {
                return new Period(json.getAsString());
            }

            @Override
            public Period createInstance(Type type) {
                return new Period();
            }
        }

    }

}
