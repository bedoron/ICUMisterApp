package com.icumister.notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.icumister.MainActivity;
import com.icumister.icumisterapp.R;
import com.microsoft.windowsazure.notifications.NotificationsHandler;

public class MyHandler extends NotificationsHandler {

    private NotificationManager mNotificationManager;
    Context ctx;

    @Override
    public void onReceive(Context context, Bundle bundle) {
        ctx = context;
        try {
            Notification notification = new Notification(bundle);
            sendNotification(notification);
            if (MainActivity.isVisible) {
                MainActivity.mainActivity.ToastNotify("Received a notification for " + notification.getNotifType().toString());
            }
        } catch(Exception e) {
            Log.e("MyHandler", "Received illegal notification");
        }
    }

    private void sendNotification(Notification notification) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(notification.getUrl()));
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        mNotificationManager = (NotificationManager)
                ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0,
                i, PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(ctx, Constants.NOTIFICATION_CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("ICUMISTER")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(notification.getMsg()))
                        .setSound(defaultSoundUri)
                        .setContentText(notification.getMsg());

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(Constants.NOTIFICATION_ID, mBuilder.build());
    }
}
