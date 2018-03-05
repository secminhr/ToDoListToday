package com.example.secminhr.todolisttoday;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class NotificationPusher extends BroadcastReceiver {

    final static String ID = "TODO";
    final static String NOTIFICATION = "notification";

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = intent.getParcelableExtra(NOTIFICATION);
        int id = intent.getIntExtra(ID, 0);
        System.err.println(id);
        System.err.println(notification);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(new NotificationChannel("TODO", "TODO", NotificationManager.IMPORTANCE_HIGH));
        }
        manager.notify(id, notification);
    }
}
