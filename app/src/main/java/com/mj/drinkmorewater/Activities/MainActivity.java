package com.mj.drinkmorewater.Activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
import com.mj.drinkmorewater.db.DatabaseHandler;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;
import com.android.volley.RequestQueue;


public class MainActivity extends AppCompatActivity implements GestureDetector.OnGestureListener {

    FloatingActionButton floatingActionButton;
    private ProgressDialog pDialog;
    TextView txtAlreadyWaterPerDay;
    TextView txtAllWaterPerDay;
    TextView txtTemperatureWarning;
    public TextView currentLocation;
    public TextView currentWeatherInfo;
    final public static String getAMountLocation = "amountlocation.txt";
    public static Location location;
    static String cityName = "";
    static String weatherInfo = "";
    static int currentTemp = 0;
    static String countryName = "";
    public AlertDialog alert;
    public boolean isPaused = false;
    GestureDetector gestureDetector;

    int alreadyAmount = 0;

    String allwater = "0"; //julijan pomožna spremenljivka

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtAlreadyWaterPerDay = (TextView) findViewById(R.id.txtDataWaterToday);
        txtAllWaterPerDay = (TextView) findViewById(R.id.txtDataWaterTotalToday);
        currentLocation = (TextView) findViewById(R.id.txtCurrentLocation);
        currentWeatherInfo = (TextView) findViewById(R.id.txtWeatherInfo);
        txtTemperatureWarning = (TextView) findViewById(R.id.temperatureWarning);

        //startService(new Intent(MainActivity.this, NotificationService.class));

        Intent intent = new Intent(getApplicationContext(), NotificationReciever.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + AlarmManager.INTERVAL_FIFTEEN_MINUTES, 7200 * 1000, pendingIntent); //every 2 hours

        gestureDetector = new GestureDetector(MainActivity.this, MainActivity.this);

//        if (!isNetworkAvailable()) {
//            showInternetDisabledAlertToUser();
//        }



        //  TODO tukaj se potem prebere lokacija pa pokliče

        SharedPreferences prefs = getSharedPreferences("isMainFirstOpen", MODE_PRIVATE);
        boolean previouslyStarted = prefs.getBoolean("hasRun", false);

        if (!previouslyStarted) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("hasRun", true);
            editor.commit(); //

            loadData();

            currentLocation.setVisibility(View.INVISIBLE);
            currentWeatherInfo.setVisibility(View.INVISIBLE);


        } else {
            Intent i = getIntent();
            cityName = i.getStringExtra("cityName");
            weatherInfo = i.getStringExtra("weatherInfo");
            currentTemp = (int) Math.round(i.getDoubleExtra("currentTemp", -150));
            countryName = i.getStringExtra("countryName");

            loadData();

            if (currentTemp >= 28) {
                txtTemperatureWarning.setText("Stay Hydrated!");
            }


            if (!cityName.equals("") && !weatherInfo.equals("") && !countryName.equals("")) {
                currentLocation.setText("Location: " + cityName); //I removed countryName
                currentWeatherInfo.setText("Temperature: " + currentTemp + "°C"); //I removed weatherInfo line
            }
            if(cityName.equals("") || currentTemp == -150) {
                currentLocation.setVisibility(View.INVISIBLE);
                currentWeatherInfo.setVisibility(View.INVISIBLE);
            }
        }


        floatingActionButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent addNewWater = new Intent(MainActivity.this, InsertWater.class);
                    startActivity(addNewWater);}
            });


        }


        //Gesture Methods


        @Override
        public boolean onFling (MotionEvent motionEvent1, MotionEvent motionEvent2,float X, float Y)
        {


            if (motionEvent1.getX() - motionEvent2.getX() > 50) {

                //Toast.makeText(MainActivity.this , " Swipe Left " , Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MainActivity.this, InsertWater.class);
                startActivity(intent);
                this.gestureDetector = null;
                return true;
            }

            if (motionEvent2.getX() - motionEvent1.getX() > 50) {

                //Toast.makeText(MainActivity.this, " Swipe Right ", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MainActivity.this, InsertWater.class);
                startActivity(intent);
                this.gestureDetector = null;
                return true;
            } else {

                return true;
            }
        }

        @Override
        public void onLongPress (MotionEvent arg0){

            // TODO Auto-generated method stub

        }

        @Override
        public boolean onScroll (MotionEvent arg0, MotionEvent arg1,float arg2, float arg3){

            // TODO Auto-generated method stub

            return false;
        }

        @Override
        public void onShowPress (MotionEvent arg0){

            // TODO Auto-generated method stub

        }

        @Override
        public boolean onSingleTapUp (MotionEvent arg0){

            // TODO Auto-generated method stub

            return false;
        }

        @Override
        public boolean onTouchEvent (MotionEvent motionEvent){

            // TODO Auto-generated method stub

            return gestureDetector.onTouchEvent(motionEvent);
        }

        @Override
        public boolean onDown (MotionEvent arg0){

            // TODO Auto-generated method stub

            return false;
        }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    private void showInternetDisabledAlertToUser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Internet is disabled in your device. Would you like to enable it?")
                .setCancelable(false)
                .setPositiveButton("Goto Settings Page To Enable Internet",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent callGPSSettingIntent = new Intent(
                                        Settings.ACTION_WIFI_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
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
        isPaused = true;

    }

    @Override
    protected void onResume() {
        super.onResume();
        gestureDetector = new GestureDetector(MainActivity.this, MainActivity.this);
        loadData();


        DatabaseHandler databaseHandler = new DatabaseHandler(getApplicationContext());
        databaseHandler.open();

        Cursor cursor = databaseHandler.getSumWaterToday();

        if (cursor.moveToFirst()) {
            int amount = cursor.getInt(0);
            txtAlreadyWaterPerDay.setText(String.valueOf(amount) + " ml");
            alreadyAmount = amount;

        }

        CircularProgressBar circularProgressBar = (CircularProgressBar) findViewById(R.id.CircularProgressbar);
        circularProgressBar.setColor(Color.parseColor("#1976D2"));
        circularProgressBar.setBackgroundColor(Color.parseColor("#80c4fc"));
        circularProgressBar.setProgressBarWidth(24);
        circularProgressBar.setBackgroundProgressBarWidth(4);
        int animationDuration = 2500; // 2500ms = 2,5s
        int progress = (int) (alreadyAmount / Double.parseDouble(allwater) * 100);
        Log.d("test", Integer.toString(alreadyAmount));
        Log.d("test", (String) txtAllWaterPerDay.getText());
        Log.d("test", Integer.toString(progress));

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
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);

                return super.onOptionsItemSelected(item);


            case R.id.History:
                Intent intentHistory = new Intent(MainActivity.this, HistoryActivity.class);
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

            String[] separete = scanner.nextLine().split(" ");
            double longitude = Double.parseDouble(separete[0]);
            double latitude = Double.parseDouble(separete[1]);

            location = new Location("provider");
            location.setLongitude(longitude);
            location.setLatitude(latitude);
            scanner.close();

        } catch (IOException e) {
            txtAllWaterPerDay.setText("0");
        }
    }
}



