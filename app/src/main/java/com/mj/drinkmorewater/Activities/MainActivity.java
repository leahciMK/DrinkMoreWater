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
import com.mj.drinkmorewater.SplashScreen;
import com.mj.drinkmorewater.api.HttpHandler;
import com.mj.drinkmorewater.db.DatabaseHandler;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.stream.Stream;

import com.android.volley.RequestQueue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


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

    public RequestQueue requestQueue;
    static String cityName = "";
    static String weatherInfo = "";
    static int currentTemp = 0;
    static String countryName = "";
    static String lastWaterEntry = "";

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
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 7200 * 1000, 7200 * 1000, pendingIntent); //every 2 hours

        gestureDetector = new GestureDetector(MainActivity.this, MainActivity.this);

        if (!isNetworkAvailable()) {
            showInternetDisabledAlertToUser();
        }

        //new JSONParse().execute();


        //  TODO tukaj se potem prebere lokacija pa pokliče

        SharedPreferences prefs = getSharedPreferences("isMainFirstOpen", MODE_PRIVATE);
        boolean previouslyStarted = prefs.getBoolean("hasRun", false);

        if (!previouslyStarted) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("hasRun", true);
            editor.commit(); //

            loadData();

            apicall();

        } else {
            Intent i = getIntent();
            cityName = i.getStringExtra("cityName");
            weatherInfo = i.getStringExtra("weatherInfo");
            currentTemp = (int) Math.round(i.getDoubleExtra("currentTemp", 0));
            countryName = i.getStringExtra("countryName");

            loadData();
        }



            if (currentTemp >= 25) {
                txtTemperatureWarning.setText("Stay Hydrated!");
            }


            if (cityName != "" && weatherInfo != "" && countryName != "") {
                currentLocation.setText("Location: " + cityName); //I removed countryName
                currentWeatherInfo.setText("Temperature: " + currentTemp + "°C"); //I removed weatherInfo line
            }

            //requestQueue = Volley.newRequestQueue(getApplicationContext());

            floatingActionButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);
            floatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent addNewWater = new Intent(MainActivity.this, InsertWater.class);
                    startActivity(addNewWater);
                }
            });

            if (cityName == "" && weatherInfo == "" && countryName == "") {
                currentLocation.setText("...");
                currentWeatherInfo.setText("...");
            }
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

    public void apicall() {
        HttpHandler sh = new HttpHandler();

        // Making a request to url and getting response
        String jsonStr="";

        if(location != null) {
            jsonStr = sh.makeServiceCall("http://api.openweathermap.org/data/2.5/weather?lat="+location.getLatitude()+"&lon="+location.getLongitude()+"&appid=37783e3aee7050d7c1e9441f395f41bd&units=metric");
        }


        Log.e("", "Response from url: " + jsonStr);

        if (jsonStr != null) {
            try {
                JSONObject response = new JSONObject(jsonStr);

                for (int i = 0; i < response.length(); i++) {
                    try {

                        weatherInfo = "";


                        JSONArray weather=response.getJSONArray("weather");


                        if (weather.length() > 0) {
                            JSONObject object=weather.getJSONObject(0);

                            String main=object.getString("main");
                            String desc=object.getString("description");


                            weatherInfo += main+"- ";
                            weatherInfo += desc;

                        }
                        JSONObject temperatures=response.getJSONObject("main");
                        if(temperatures.length() > 0) {
                            currentTemp = temperatures.getInt("temp");

                        }

                        JSONObject country=response.getJSONObject("sys");
                        if(country.length() > 0) {
                            countryName = country.getString("country");
                        }

                        cityName = response.getString("name");


                    } catch (JSONException e) {
                        e.printStackTrace();

                    }
                }


            }catch (final JSONException e) {
                Log.e("", "Json parsing error: " + e.getMessage());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Json parsing error: " + e.getMessage(),
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }
        }else {
            Log.e("", "Couldn't get json from server.");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),
                            "Couldn't get weather data from server. Check internet connection!",
                            Toast.LENGTH_LONG)
                            .show();
                }
            });

        }
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



