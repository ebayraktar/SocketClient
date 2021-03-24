package com.bayraktar.servicesexample.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.bayraktar.servicesexample.R;
import com.bayraktar.servicesexample.services.ExampleService;
import com.bayraktar.servicesexample.services.ManagerService;
import com.bayraktar.servicesexample.services.WebSocketService;

public class MainActivity extends AppCompatActivity {
    private EditText etInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etInput = findViewById(R.id.etInput);

        if (getIntent() != null) {
            String text = getIntent().getStringExtra("inputExtra");
            etInput.setText(text);
        }
        initServices();
    }

    private void initServices() {
        Intent serviceIntent = new Intent(this, ManagerService.class);
        startService(serviceIntent);
//        ContextCompat.startForegroundService(this, serviceIntent);
    }

    public void startService(View v) {
        String input = etInput.getText().toString();

        Intent serviceIntent = new Intent(this, ExampleService.class);
        serviceIntent.putExtra("inputExtra", input);

//        ContextCompat.startForegroundService(this, serviceIntent);
        startService(serviceIntent);
    }

    public void stopService(View v) {
        Intent serviceIntent = new Intent(this, ExampleService.class);
        stopService(serviceIntent);

    }


}
