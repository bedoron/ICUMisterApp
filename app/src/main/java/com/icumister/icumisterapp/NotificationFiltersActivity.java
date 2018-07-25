package com.icumister.icumisterapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioGroup;

import com.icumister.AppState;
import com.icumister.notification.NotificationFilter;

public class NotificationFiltersActivity extends AppCompatActivity {

    public NotificationFilter notificationFilter = AppState.notificationFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_filters);
        RadioGroup radioGroup = findViewById(R.id.RGroup);
        radioGroup.check(radioGroup.getChildAt(this.notificationFilter.getValue()).getId());
    }

    public void saveNotificationFilters(View view) {
        RadioGroup radioGroup = findViewById(R.id.RGroup);
        int radioButtonID = radioGroup.getCheckedRadioButtonId();
        View radioButton = radioGroup.findViewById(radioButtonID);
        String resourceEntryName = radioButton.getResources().getResourceEntryName(radioButton.getId());
        this.notificationFilter = NotificationFilter.valueOf(resourceEntryName);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("notificationFilter", notificationFilter.name());
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        this.notificationFilter = NotificationFilter.valueOf(savedInstanceState.getString("notificationFilter"));
    }

    public boolean onOptionsItemSelected (MenuItem item) {
        if (android.R.id.home == item.getItemId()) {
            RadioGroup radioGroup = findViewById(R.id.RGroup);
            int radioButtonID = radioGroup.getCheckedRadioButtonId();
            View radioButton = radioGroup.findViewById(radioButtonID);
            String resourceEntryName = radioButton.getResources().getResourceEntryName(radioButton.getId());
            AppState.notificationFilter = NotificationFilter.valueOf(resourceEntryName);;
            finish();
            return true;
        }
        return false;
    }
}
