package com.bayraktar.servicesexample.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.bayraktar.servicesexample.R;

import static com.bayraktar.servicesexample.App.CHANNEL_ID;

/**
 * Created by Emre BAYRAKTAR on 2/11/2021.
 */
public class SendDataService extends Service {
    private final Object lock = new Object();
    NotificationManagerCompat notificationManager;

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = NotificationManagerCompat.from(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.hasExtra("OLD_NOTIFICATION_ID"))
            notificationManager.cancel(intent.getIntExtra("OLD_NOTIFICATION_ID", -1));

        downloadProcess();
        return START_STICKY;
    }

    private void downloadProcess() {
        new Thread(() -> {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
            builder.setContentTitle("Send Data")
                    .setContentText("Upload in progress")
                    .setSmallIcon(R.drawable.ic_beach)
                    .setPriority(NotificationCompat.PRIORITY_LOW);

            // Issue the initial notification with zero progress
            int PROGRESS_MAX = 100;
            int PROGRESS_CURRENT = 0;
            builder.setProgress(PROGRESS_MAX, PROGRESS_CURRENT, false);
            notificationManager.notify(33, builder.build());

            while (PROGRESS_CURRENT < PROGRESS_MAX) {
                PROGRESS_CURRENT += 10;
                builder.setProgress(PROGRESS_MAX, PROGRESS_CURRENT, false)
                        .setNotificationSilent();
                notificationManager.notify(33, builder.build());

                synchronized (lock) {
                    try {
                        lock.wait(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            // Do the job here that tracks the progress.
            // Usually, this should be in a
            // worker thread
            // To show progress, update PROGRESS_CURRENT and update the notification with:
            // builder.setProgress(PROGRESS_MAX, PROGRESS_CURRENT, false);
            // notificationManager.notify(notificationId, builder.build());

            // When done, update the notification one more time to remove the progress bar
            builder.setContentText("Upload complete")
                    .setProgress(0, 0, false);
            notificationManager.notify(33, builder.build());
        }).start();
    }
}
