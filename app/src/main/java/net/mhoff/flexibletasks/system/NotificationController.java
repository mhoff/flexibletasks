package net.mhoff.flexibletasks.system;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import net.mhoff.flexibletasks.activity.TaskOverviewActivity;
import net.mhoff.flexibletasks.model.TaskManager;

public class NotificationController {

    private Context context;

    private TaskManager taskManager;

    private AlarmManager alarmManager;

    private boolean alarmSet = false;

    public NotificationController(Context context) {
        this.context = context.getApplicationContext();
        alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

    }

    private void cancelAlarm() {
        Intent i = new Intent(context, TaskOverviewActivity.class);
        PendingIntent p = PendingIntent.getBroadcast(context, 0, i, 0);
        alarmManager.cancel(p);
        p.cancel();
    }

    private void updateAlarm() {
        if (alarmSet) {
            cancelAlarm();
        }

        /*

        Intent notificationIntent = new Intent(this, NotificationController.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
        */
    }

}
