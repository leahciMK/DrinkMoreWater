package com.mj.drinkmorewater;

/**
 * Created by mihaa on 4. 01. 2018.
 */

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Parcel;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.common.util.Strings;
import com.mj.drinkmorewater.Activities.InsertWater;
import com.mj.drinkmorewater.Utils.DateUtils;
import com.mj.drinkmorewater.Utils.NotificationUtils;
import com.mj.drinkmorewater.db.DatabaseHandler;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;


public class NotificationReciever extends BroadcastReceiver {

    int id=1;
    private String lastWaterEntry="";
    private static String CHANNEL_ID = "notification";

    /*
        @TODO
        rewrite Date to java8 LocalDate etc... correct Notification....
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        createNotificationChannel(context);
        DatabaseHandler databaseHandler = new DatabaseHandler(context.getApplicationContext());
        String lastEntryDate = databaseHandler.getLastDrinkEntryDate();
        if(!Strings.isEmptyOrWhitespace(lastEntryDate)) {
            lastWaterEntry = lastEntryDate;

            int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY); //Current hour
            //return currentHour < 18 //False if after 6pm

            if(currentHour >= 7 && currentHour < 9) {
                sendNotificationWakeUp(context);
            }

            if (checkLastEntryFor2Hours(lastWaterEntry) && (currentHour >= 9 && currentHour <= 23)) {
                sendNotification(context);
                Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                // Vibrate for 500 milliseconds
                v.vibrate(500);
                v.vibrate(VibrationEffect.createOneShot(500,1));

            }
        } else {
            sendNotificationNoInsert(context);
        }




    }

    public void sendNotificationNoInsert(Context context) {

        //Get an instance of NotificationManager//

        Log.e("Loooog" , "Okkk sm noter");
        Notification n  = new Notification.Builder(context)
                .setContentTitle("Drink Water!")
                .setContentText("You didn't drink any water yet!")
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setAutoCancel(true).build();

        NotificationCompat.Builder builder = NotificationUtils.notificationBuilder(context, CHANNEL_ID,
                "Drink Water!", "You didn't drink any water yet!");

        /*builder.contentIntent=PendingIntent.getActivity(context, 0,
                new Intent(context, InsertWater.class), PendingIntent.FLAG_UPDATE_CURRENT);*/



        // Gets an instance of the NotificationManager service//

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // When you issue multiple notifications about the same type of event,
        // it’s best practice for your app to try to update an existing notification
        // with this new information, rather than immediately creating a new notification.
        // If you want to update this notification at a later date, you need to assign it an ID.
        // You can then use this ID whenever you issue a subsequent notification.
        // If the previous notification is still visible, the system will update this existing notification,
        // rather than create a new one. In this example, the notification’s ID is 001//

        NotificationManagerCompat notificationManager = NotificationUtils.buildNotificationManagerCompat(context);

        // notificationId is a unique int for each notification that you must define
        NotificationUtils.sendNotification(notificationManager, id, builder);
        id++;


        /*mNotificationManager.notify(id, n);

        id++;*/
    }


    public void sendNotificationWakeUp(Context context) {

        //Get an instance of NotificationManager//

        Notification n  = new Notification.Builder(context)
                .setContentTitle("Drink Water!")
                .setContentText("Good morning, remember to drink water today!")
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



    public void sendNotification(Context context) {

        //Get an instance of NotificationManager//

        Notification n  = new Notification.Builder(context)
                .setContentTitle("Drink Water!")
                .setContentText("You didn't drink water for more than 2 hours!")
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setAutoCancel(true).build();

        n.contentIntent=PendingIntent.getActivity(context, 0,
                new Intent(context, InsertWater.class), PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder builder = NotificationUtils.notificationBuilder(context, CHANNEL_ID,
                "Drink Water!", "You didn't drink water for more than 2 hours!");

        // Gets an instance of the NotificationManager service//

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // When you issue multiple notifications about the same type of event,
        // it’s best practice for your app to try to update an existing notification
        // with this new information, rather than immediately creating a new notification.
        // If you want to update this notification at a later date, you need to assign it an ID.
        // You can then use this ID whenever you issue a subsequent notification.
        // If the previous notification is still visible, the system will update this existing notification,
        // rather than create a new one. In this example, the notification’s ID is 001//

        NotificationManagerCompat notificationManager = NotificationUtils.buildNotificationManagerCompat(context);

        // notificationId is a unique int for each notification that you must define
        NotificationUtils.sendNotification(notificationManager, id, builder);
        id++;


        /*mNotificationManager.notify(id, n);

        id++;*/
    }

    public boolean checkLastEntryFor2Hours(String lastDrinkEntryDate) {

        LocalDateTime currentDateAndTime = DateUtils.getCurrentDateAndTime();
        LocalDateTime lastTwoHours = DateUtils.substractHours(currentDateAndTime, 2);

        LocalDateTime lastDrinkEntry = LocalDateTime.parse(lastDrinkEntryDate,DateTimeFormatter.ofPattern(DateUtils.DATE_AND_TIME));


        if(lastDrinkEntry.isBefore(lastTwoHours)) {
            return true;
        }

        return false;
    }

    public static void createNotificationChannel(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // @TODO rename
            CharSequence name = "Channel name";
            String description = "Channel description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}