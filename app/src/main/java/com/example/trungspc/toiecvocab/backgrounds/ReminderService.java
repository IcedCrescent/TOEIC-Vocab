package com.example.trungspc.toiecvocab.backgrounds;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.example.trungspc.toiecvocab.R;
import com.example.trungspc.toiecvocab.activities.MainActivity;
import com.example.trungspc.toiecvocab.utils.CommonConst;

public class ReminderService extends Service {

    private final int NOTIFICATION_ID = 100;

    public ReminderService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Intent intent1 = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent snoozeIntent = new Intent(this, SnoozeReceiver.class);
        snoozeIntent.putExtra("notificaitonId", NOTIFICATION_ID);
        PendingIntent snoozePendingIntent = PendingIntent.getBroadcast(this, 0, snoozeIntent, 0);

        NotificationCompat.Action snoozeAction = new NotificationCompat.Action.Builder(
                R.drawable.ic_access_time_black_20dp,
                "Snooze for 5 mins",
                snoozePendingIntent)
                .build();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CommonConst.REMINDER_CHANNEL_ID)
                .setContentTitle("Time to study")
                .setContentText("Tap to start:")
                .setSmallIcon(R.drawable.ic_access_time_black_20dp)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .addAction(snoozeAction);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());

        return START_NOT_STICKY;
    }
}