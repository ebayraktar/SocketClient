package com.bayraktar.servicesexample.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.bayraktar.servicesexample.App;
import com.bayraktar.servicesexample.R;
import com.bayraktar.servicesexample.receivers.NetworkChangeReceiver;

/**
 * Created by Emre BAYRAKTAR on 2/13/2021.
 */
public class ManagerService extends Service {
    private NetworkChangeReceiver networkChangeReceiver;

    public ManagerService() {
        networkChangeReceiver = new NetworkChangeReceiver();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        initializeBroadcastReceivers();

        Notification notification = getNotification(this, getString(R.string.app_name), "ServiceManager is running...", null);
        startForeground(1, notification);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(networkChangeReceiver);
        } catch (Exception ignored) {

        }
        stopSelf();
    }

    private void initializeBroadcastReceivers() {
        //Network State Change Receiver
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        networkChangeReceiver = new NetworkChangeReceiver();

        // Registration
        registerReceiver(networkChangeReceiver, filter);
    }

    public static Notification getNotification(Context context, String contentTitle, String contentText, PendingIntent pendingIntent) {
        return new NotificationCompat.Builder(context, App.CHANNEL_ID)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setSmallIcon(android.R.drawable.star_big_on)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();
    }

    public static Notification getNotification(Context context, String contentTitle, String contentText, PendingIntent pendingIntent, int icon) {
        return new NotificationCompat.Builder(context, App.CHANNEL_ID)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setSmallIcon(icon)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();
    }
}
