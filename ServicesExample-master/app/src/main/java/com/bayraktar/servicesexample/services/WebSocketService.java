package com.bayraktar.servicesexample.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.bayraktar.servicesexample.views.MainActivity;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

import static com.bayraktar.servicesexample.App.CHANNEL_ID;

/**
 * Created by Emre BAYRAKTAR on 2/10/2021.
 */
public class WebSocketService extends Service {
    public static String IS_ACTION_START = "IS_ACTION_START";
    private static final String URL = "URL_HERE";

    private WebSocketClient mWebSocketClient;
    NotificationManagerCompat notificationManager;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        notificationManager = NotificationManagerCompat.from(this);

        if (intent.hasExtra(IS_ACTION_START)) {
            if (!intent.getBooleanExtra(IS_ACTION_START, false)) {
                if (mWebSocketClient != null && mWebSocketClient.isOpen()) {
                    mWebSocketClient.closeConnection(10,"Byee");
                }
                super.onStartCommand(intent, flags, startId);
                return START_NOT_STICKY;
            }
        }

        connectWebSocket();

        Notification notification = ManagerService
                .getNotification(this, "Web Socket Service", "Web Socket Service is running...", null);

        startForeground(2, notification);

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void connectWebSocket() {
        URI uri;
        try {
            uri = new URI(URL);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        mWebSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.i("Websocket", "Opened");
                mWebSocketClient.send("Hello from " + Build.MANUFACTURER + " " + Build.MODEL);
            }

            @Override
            public void onMessage(String s) {
                Log.i("Websocket", "Message: " + s);
                showMessageNotification(s);
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                Log.i("Websocket", "Close: " + s);
                showOtherNotification("CLOSE: " + s);
                stopForeground(true);
                stopSelf();
            }

            @Override
            public void onError(Exception e) {
                Log.i("Websocket", "Error: " + e.getMessage());
                showOtherNotification("ERROR: " + e.getMessage());
                stopForeground(true);
                stopSelf();
            }
        };
        mWebSocketClient.connect();
    }

    private void showMessageNotification(String input) {

        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.putExtra("inputExtra", input);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        Intent snoozeIntent = new Intent(this, SendDataService.class);
        snoozeIntent.setAction("ACTION_SNOOZE");
        snoozeIntent.putExtra("OLD_NOTIFICATION_ID", 99);
        PendingIntent sendData =
                PendingIntent.getService(this, 0, snoozeIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Web Socket Service")
                .setContentText(input)
                .setSmallIcon(android.R.drawable.ic_menu_sort_by_size)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .addAction(android.R.drawable.btn_plus, "Try To Send Data", sendData)
                .build();

        // Issue the notification.
        notificationManager.notify(99, notification);
    }

    private void showOtherNotification(String contentText) {
        Notification notification = ManagerService
                .getNotification(this, "Web Socket Service", contentText, null, android.R.drawable.bottom_bar);
        notificationManager.notify(98, notification);
    }
}
