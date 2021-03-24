package com.bayraktar.servicesexample.views;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bayraktar.servicesexample.R;
import com.bayraktar.servicesexample.widgets.ExampleAppWidgetProvider;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        findViewById(R.id.askForPin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPinAppWidget();
            }
        });
    }

    void requestPinAppWidget() {

        AppWidgetManager appWidgetManager =
                getSystemService(AppWidgetManager.class);
        ComponentName myProvider =
                new ComponentName(this, ExampleAppWidgetProvider.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            assert appWidgetManager != null;
            if (appWidgetManager.isRequestPinAppWidgetSupported()) {
                // Create the PendingIntent object only if your app needs to be notified
                // that the user allowed the widget to be pinned. Note that, if the pinning
                // operation fails, your app isn't notified.
                Intent pinnedWidgetCallbackIntent = new Intent(this, MainActivity.class);

                // Configure the intent so that your app's broadcast receiver gets
                // the callback successfully. This callback receives the ID of the
                // newly-pinned widget (EXTRA_APPWIDGET_ID).
                PendingIntent successCallback = PendingIntent.getBroadcast(this, 0,
                        pinnedWidgetCallbackIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                appWidgetManager.requestPinAppWidget(myProvider, null, successCallback);
            }
        }
    }
}
