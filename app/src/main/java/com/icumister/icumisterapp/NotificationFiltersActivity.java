package com.icumister.icumisterapp;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioGroup;

import com.icumister.notification.MyHandler;
import com.icumister.notification.NotificationFilter;
import com.icumister.notification.NotificationSettings;
import com.microsoft.windowsazure.notifications.NotificationsManager;

public class NotificationFiltersActivity extends AppCompatActivity {

    private NotificationFilter notificationFilter = NotificationFilter.ALL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_filters);
        RadioGroup radioGroup = findViewById(R.id.RGroup);
        radioGroup.check(notificationFilter.getValue());
    }

    public void saveNotificationFilters(View view) {
        RadioGroup radioGroup = findViewById(R.id.RGroup);
        int radioButtonID = radioGroup.getCheckedRadioButtonId();
        View radioButton = radioGroup.findViewById(radioButtonID);
        this.notificationFilter = NotificationFilter.valueOf(radioButton.toString());
    }

}
