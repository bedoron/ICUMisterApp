package com.icumister.icumisterapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.icumister.MainActivity;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
    }

    public void openNotificationFiltersActivity(View view)
    {
        Intent intent = new Intent(SettingsActivity.this, NotificationFiltersActivity.class);
        startActivity(intent);
    }

    public void openQuickAssist(View view) {
        //Intent intent = new Intent(MainActivity.this, NotificationFiltersActivity.class);
        //startActivity(intent);
    }
}
