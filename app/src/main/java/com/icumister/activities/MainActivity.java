package com.icumister.activities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.icumister.icumisterapp.R;
import com.icumister.Constants;
import com.icumister.icumisterapp.SettingsActivity;

import com.icumister.notification.MyHandler;
import com.icumister.notification.NotificationSettings;
import com.icumister.notification.RegistrationIntentService;
import com.microsoft.windowsazure.notifications.NotificationsManager;

public class MainActivity extends AppCompatActivity {

    public static MainActivity mainActivity;
    public static Boolean isVisible = false;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupNotificationChannel(getNotificationChannelParameters());
        NotificationsManager.handleNotifications(this, NotificationSettings.SenderId, MyHandler.class);
        registerWithNotificationHubs();

        mainActivity = this;
    }

    private NotifParams getNotificationChannelParameters() {
        return new NotifParams();
    }

    public void openSettingsActivity(View view) {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    private void setupNotificationChannel(NotifParams notificationParameters) {
        NotificationManager notificationsManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationsManager == null) {
            Log.w(TAG, "Couldn't get Notification Service");
            return;
        }

        String notificationChannelName = getString(R.string.notification_channel_name);
        int notificationChannelImportance;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            notificationChannelImportance = NotificationManager.IMPORTANCE_LOW;
        } else {
            notificationChannelImportance = 0;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(Constants.NOTIFICATION_CHANNEL_ID, notificationChannelName, notificationChannelImportance);
            notificationChannel.setDescription(notificationParameters.notificationChannelDescription);
            notificationChannel.enableLights(notificationParameters.enableLights);
            notificationChannel.setLightColor(notificationParameters.lightColor);
            notificationChannel.enableVibration(notificationParameters.enableVibration);
            notificationChannel.setVibrationPattern(notificationParameters.vibrationPattern);
            notificationsManager.createNotificationChannel(notificationChannel);
        }
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, Constants.PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported by Google Play Services.");
                ToastNotify("This device is not supported by Google Play Services.");
                finish();
            }
            return false;
        }
        return true;
    }

    public void registerWithNotificationHubs() {
        Log.i(TAG, " Registering with Notification Hubs");

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        isVisible = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isVisible = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isVisible = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isVisible = false;
    }

    public void ToastNotify(final String notificationMessage) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView helloText = findViewById(R.id.text_hello);
                helloText.setText(notificationMessage);
                Handler h=new Handler();
                h.postDelayed(new Runnable(){
                    public void run(){
                        TextView helloText = findViewById(R.id.text_hello);
                        helloText.setText("");
            }
                }, 10000);
            }
        });
    }

    public void startAddYouselfActivity(View view) {
        Intent addYourselfIntent = new Intent(this, AddYourselfActivity.class);
        startActivity(addYourselfIntent);
    }

    private class NotifParams {
        private String notificationChannelDescription = "Default";
        private boolean enableLights = true;
        private int lightColor = Color.RED;
        private boolean enableVibration = true;
        private long[] vibrationPattern = new long[]{100, 100, 100, 300, 300, 300, 100, 100, 100};
    }
}