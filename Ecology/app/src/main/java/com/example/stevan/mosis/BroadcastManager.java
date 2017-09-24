package com.example.stevan.mosis;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

/**
 * Created by Stevan Zivkovic on 22-Sep-17.
 */

public class BroadcastManager extends BroadcastReceiver {

    Integer notificationId = 0;
    SharedPreferences prefs;
    Integer userId;


    @Override
    public void onReceive(Context context, Intent intent) {

        System.out.println("DETECTED");

        // Notification which we need
        prefs = context.getSharedPreferences("Logging info",
                Context.MODE_PRIVATE);
        userId = prefs.getInt("userId", 0);
        notificationId = intent.getExtras().getInt("notificationId");
        if(notificationId.equals(1))
        {

            Notification(context, "You have friends near you!");
        }
        else if(notificationId.equals(2))
        {
            Notification(context, "You have some objects near you!");
        }
    }

    public void Notification(Context context, String message)
    {
        String notificationTitle = context.getString(R.string.app_name);

        Intent intent = new Intent(context, MainActivity.class);

        PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                context)
                // Set Icon
                .setSmallIcon(R.mipmap.ic_launcher)
                // Set Ticker Message
                .setTicker(message)
                // Set Title
                .setContentTitle(notificationTitle)
                // Set Text
                .setContentText(message)
                // Set PendingIntent into Notification
                .setContentIntent(pIntent)
                // Dismiss Notification
                .setAutoCancel(true);

        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        builder.setSound(uri);

        // Create Notification Manager
        NotificationManager notificationmanager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        // Build Notification with Notification Manager
        notificationmanager.notify(notificationId, builder.build());
    }
}
