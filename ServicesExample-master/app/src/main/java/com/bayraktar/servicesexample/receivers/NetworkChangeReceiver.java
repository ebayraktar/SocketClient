package com.bayraktar.servicesexample.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.bayraktar.servicesexample.services.WebSocketService;

import org.java_websocket.server.WebSocketServer;

/**
 * Created by Emre BAYRAKTAR on 2/13/2021.
 */
public class NetworkChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (context != null) {
            webSocketServiceOperation(context, isOnline(context));
        }
    }

    public boolean isOnline(@org.jetbrains.annotations.NotNull Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //should check null because in airplane mode it will be null
        return (netInfo != null && netInfo.isConnected());
    }

    private void webSocketServiceOperation(Context context, boolean isStart) {
        Intent intent = new Intent(context, WebSocketService.class);
        intent.putExtra(WebSocketService.IS_ACTION_START, isStart);
        context.startService(intent);
//        ContextCompat.startForegroundService(context, intent);
    }
}
