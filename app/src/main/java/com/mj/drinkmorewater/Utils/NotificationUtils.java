package com.mj.drinkmorewater.Utils;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.mj.drinkmorewater.Activities.InsertWater;
import com.mj.drinkmorewater.R;

public class NotificationUtils {

    public static NotificationCompat.Builder notificationBuilder(Context context, String channelID, String title, String body) {
        // @TODO add custom intent and icon....
        return new NotificationCompat.Builder(context, channelID)
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setContentTitle(title)
                .setContentText(body)
                .setContentIntent(PendingIntent.getActivity(context, 0,
                        new Intent(context, InsertWater.class), PendingIntent.FLAG_UPDATE_CURRENT))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
    }

    public static NotificationManagerCompat buildNotificationManagerCompat(Context context) {
        return NotificationManagerCompat.from(context);
    }

    public static void sendNotification(NotificationManagerCompat notificationManager, int id, NotificationCompat.Builder builder) {
        notificationManager.notify(id, builder.build());
    }
}
