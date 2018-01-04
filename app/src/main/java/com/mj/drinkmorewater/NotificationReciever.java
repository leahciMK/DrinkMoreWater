package com.mj.drinkmorewater;

/**
 * Created by mihaa on 4. 01. 2018.
 */

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.mj.drinkmorewater.Activities.InsertWater;
import com.mj.drinkmorewater.Activities.MainActivity;
import com.mj.drinkmorewater.db.DatabaseHandler;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class NotificationReciever extends BroadcastReceiver {

    int id=1;
    private String lastWaterEntry="";
    @Override
    public void onReceive(Context context, Intent intent) {

        DatabaseHandler databaseHandler = new DatabaseHandler(context.getApplicationContext());
        Cursor cursor = databaseHandler.getLastWaterEntry();
        if(cursor.moveToFirst()) {
            lastWaterEntry = cursor.getString(0);
        }

        int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY); //Current hour
        //return currentHour < 18 //False if after 6pm

        // display toast
        Toast.makeText(context.getApplicationContext(), "Service is running", Toast.LENGTH_SHORT).show();

        if (checkLastEntryFor2Hours(lastWaterEntry) && (currentHour >= 7 && currentHour <= 23)) {
            sendNotification(context);
            Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            // Vibrate for 500 milliseconds
            v.vibrate(500);

        }


    }

    public void sendNotification(Context context) {

        //Get an instance of NotificationManager//

        Notification n  = new Notification.Builder(context)
                .setContentTitle("Drink Water!")
                .setContentText("You didn't drink water for 1 hour!")
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setAutoCancel(true).build();

        n.contentIntent=PendingIntent.getActivity(context, 0,
                new Intent(context, InsertWater.class), PendingIntent.FLAG_UPDATE_CURRENT);



        // Gets an instance of the NotificationManager service//

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // When you issue multiple notifications about the same type of event,
        // it’s best practice for your app to try to update an existing notification
        // with this new information, rather than immediately creating a new notification.
        // If you want to update this notification at a later date, you need to assign it an ID.
        // You can then use this ID whenever you issue a subsequent notification.
        // If the previous notification is still visible, the system will update this existing notification,
        // rather than create a new one. In this example, the notification’s ID is 001//


        mNotificationManager.notify(id, n);

        id++;
    }

    public boolean checkLastEntryFor2Hours(String lastWaterEntry) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date minustwoHours=new Date(System.currentTimeMillis() - 3600*1000); //1hour


        try {
            Date lastEntry=df.parse(lastWaterEntry);

            if(lastEntry.before(minustwoHours)) {
                return true;
            }



        } catch (ParseException e) {
            e.printStackTrace();
        }



        return false;
    }
}