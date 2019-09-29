package com.mj.drinkmorewater.Activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.provider.Settings;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.mj.drinkmorewater.NotificationReciever;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.mj.drinkmorewater.R;
import com.mj.drinkmorewater.Utils.DateUtils;
import com.mj.drinkmorewater.api.HttpHandler;
import com.mj.drinkmorewater.components.resources.CoreResourceBundle;
import com.mj.drinkmorewater.db.DatabaseHandler;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;
import com.android.volley.RequestQueue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity implements GestureDetector.OnGestureListener {

    FloatingActionButton floatingActionButton;

    TextView txtAlreadyWaterPerDay;
    TextView txtAllWaterPerDay;
    GestureDetector gestureDetector;
    public AlertDialog alert;


    final public static String getAMountLocation="amountlocation.txt";
    public static Location location;
    static String cityName="";
    static String weatherInfo="";
    static double currentTemp=0;
    static String countryName="";
    public boolean isPaused=false;


    int alreadyAmount=0;

    String allwater = "0";

    private CoreResourceBundle coreResourceBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        txtAlreadyWaterPerDay = findViewById(R.id.txtDataWaterToday);
        txtAllWaterPerDay = findViewById(R.id.txtDataWaterTotalToday);


        Intent intent = new Intent(getApplicationContext(),NotificationReciever.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),0,intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + DateUtils.TWO_HOURS_IN_MILIS, DateUtils.TWO_HOURS_IN_MILIS, pendingIntent ); //every 2 hours

        gestureDetector=new GestureDetector(MainActivity.this,MainActivity.this);

        if (!isNetworkAvailable()) {
            showInternetDisabledAlertToUser();
        }

        loadData();

        Intent i = getIntent();
        cityName = i.getStringExtra("cityName");
        weatherInfo = i.getStringExtra("weatherInfo");
        currentTemp=i.getDoubleExtra("currentTemp",0);
        countryName=i.getStringExtra("countryName");

        floatingActionButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addNewWater = new Intent(MainActivity.this, InsertWater.class);
                startActivity(addNewWater);
            }
        });
    }

    //Gesture Methods
    @Override
    public boolean onFling(MotionEvent motionEvent1, MotionEvent motionEvent2, float X, float Y) {


        if(motionEvent1.getX() - motionEvent2.getX() > 50){
            Intent intent=new Intent(MainActivity.this,InsertWater.class);
            startActivity(intent);
            this.gestureDetector=null;
            return true;
        }

        if(motionEvent2.getX() - motionEvent1.getX() > 50) {
            Intent intent=new Intent(MainActivity.this,InsertWater.class);
            startActivity(intent);
            this.gestureDetector=null;
            return true;
        }
        else {
            return true;
        }
    }

    @Override
    public void onLongPress(MotionEvent arg0) {

        // TODO Auto-generated method stub

    }

    @Override
    public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2, float arg3) {

        // TODO Auto-generated method stub

        return false;
    }

    @Override
    public void onShowPress(MotionEvent arg0) {

        // TODO Auto-generated method stub

    }

    @Override
    public boolean onSingleTapUp(MotionEvent arg0) {

        // TODO Auto-generated method stub

        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        // TODO Auto-generated method stub

        return gestureDetector.onTouchEvent(motionEvent);
    }

    @Override
    public boolean onDown(MotionEvent arg0) {

        // TODO Auto-generated method stub

        return false;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    private void showInternetDisabledAlertToUser(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(coreResourceBundle.getMessage("disabled_internet"))
                .setCancelable(false)
                .setPositiveButton(coreResourceBundle.getMessage("disabled_internet_instructions"),
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                Intent callGPSSettingIntent = new Intent(
                                        Settings.ACTION_WIFI_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        dialog.dismiss();
                    }
                });
        alert = alertDialogBuilder.create();
        alert.show();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isPaused=true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        gestureDetector=new GestureDetector(MainActivity.this,MainActivity.this);
        loadData();



        DatabaseHandler databaseHandler = new DatabaseHandler(getApplicationContext());
        databaseHandler.open();

        Cursor cursor=databaseHandler.getSumWaterToday();

        if(cursor.moveToFirst()) {
            int amount=cursor.getInt(0);
            txtAlreadyWaterPerDay.setText(String.valueOf(amount) +" ml");
            alreadyAmount=amount;

        }

        CircularProgressBar circularProgressBar = findViewById(R.id.CircularProgressbar);
        circularProgressBar.setColor(Color.parseColor("#1976D2"));
        circularProgressBar.setBackgroundColor(Color.parseColor("#80c4fc"));
        circularProgressBar.setProgressBarWidth(24);
        circularProgressBar.setBackgroundProgressBarWidth(4);
        int animationDuration = 2500; // 2500ms = 2,5s
        int progress = (int) (alreadyAmount / Double.parseDouble(allwater) *100);
        Log.d("test",Integer.toString(alreadyAmount));
        Log.d("test",(String) txtAllWaterPerDay.getText());
        Log.d("test",Integer.toString(progress));

        circularProgressBar.setProgressWithAnimation(progress, animationDuration); // Default duration = 1500ms


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.Settings:
                Intent intent=new Intent(MainActivity.this,SettingsActivity.class);
                startActivity(intent);
                return super.onOptionsItemSelected(item);

            case R.id.History:
                Intent intentHistory=new Intent(MainActivity.this,HistoryActivity.class);
                startActivity(intentHistory);
                return super.onOptionsItemSelected(item);

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { //load toolbar
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return true;
    }

    public void loadData() {
        try {
            FileInputStream stream = openFileInput(getAMountLocation);
            Scanner scanner = new Scanner(stream);
            allwater = scanner.nextLine();
            txtAllWaterPerDay.setText(allwater + " ml");

            String[] separete=scanner.nextLine().split(" ");
            double longitude=Double.parseDouble(separete[0]);
            double latitude=Double.parseDouble(separete[1]);

            location=new Location("provider");
            location.setLongitude(longitude);
            location.setLatitude(latitude);
            scanner.close();

        }
        catch (IOException e) {
            txtAllWaterPerDay.setText("0");
        }
    }

}

