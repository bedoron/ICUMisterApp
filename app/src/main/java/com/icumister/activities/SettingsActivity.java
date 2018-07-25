package com.icumister.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.icumister.icumisterapp.R;
import com.icumister.notification.NotificationFiltersActivity;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
    }

    public void startNotificationFiltersActivity(View view)
    {
        Intent intent = new Intent(SettingsActivity.this, NotificationFiltersActivity.class);
        startActivity(intent);
    }

    public void startQuickAssistActivity(View view) {
        //Intent intent = new Intent(MainActivity.this, NotificationFiltersActivity.class);
        //startActivity(intent);
    }

    public void startKnownPersonActivity(View view) {
        Intent addYourselfIntent = new Intent(this, AddKnownPersonActivity.class);
        startActivity(addYourselfIntent);
    }
}