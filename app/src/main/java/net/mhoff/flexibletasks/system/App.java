package net.mhoff.flexibletasks.system;

import android.app.Application;

import net.mhoff.flexibletasks.model.TaskManager;

public class App extends Application {

    private TaskManager taskManager = null;

    public synchronized TaskManager getTaskManager() {
        if (taskManager == null) {
            taskManager = new TaskManager(this);
        }
        return taskManager;
    }

}
