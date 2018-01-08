package com.mj.drinkmorewater;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.mj.drinkmorewater.Activities.MainActivity;
import com.mj.drinkmorewater.Activities.SettingsActivity;
import com.mj.drinkmorewater.api.HttpHandler;
import com.mj.drinkmorewater.db.DatabaseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;

/**
 * Created by godzilla on 6.1.2018.
 */

public class SplashScreen extends Activity {
    // Splash screen timer
    private static int SPLASH_TIME_OUT = 2000; // 2 sec
    static String cityName="";
    static String weatherInfo="";
    static double currentTemp=0;
    static String countryName="";
    public static Location location;
    final public static String getAMountLocation="amountlocation.txt";

    /*


        TODO
        there would be async task...
        https://www.androidhive.info/2013/07/how-to-implement-android-splash-screen-2/
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //code if the app HAS run before
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean previouslyStarted = prefs.getBoolean("hasRun", false);
        if (!previouslyStarted) {
            Log.d("hasrun", "this has not run before");
            SharedPreferences.Editor edit = prefs.edit();
            edit.putBoolean("hasRun", Boolean.TRUE);
            edit.commit();

            new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

                @Override
                public void run() {
                    // This method will be executed once the timer is over
                    // Start your app main activity
//                    Intent j = new Intent(SplashScreen.this, MainActivity.class);
//                    startActivity(j);
                    Intent i = new Intent(SplashScreen.this, SettingsActivity.class);
                    startActivity(i);

                    // close this activity
                    finish();
                }
            }, SPLASH_TIME_OUT);

            //Only to insert some DUMMY water inputs REMOVE THIS IN FINAL VERSION TODO
            final DatabaseHandler databaseHandler = new DatabaseHandler(getApplicationContext());
            databaseHandler.open();
            databaseHandler.insertTenDaysTestwater();
            databaseHandler.close();

        } else {
            new JSONParse().execute();
        }



        //new JSONParse().execute();
    }

    public void loadData() {
        try {
            FileInputStream stream = openFileInput(getAMountLocation);
            Scanner scanner = new Scanner(stream);
            scanner.nextLine();

            String[] separete=scanner.nextLine().split(" ");
            double longitude=Double.parseDouble(separete[0]);
            double latitude=Double.parseDouble(separete[1]);

            location=new Location("provider");
            location.setLongitude(longitude);
            location.setLatitude(latitude);
            scanner.close();

        }
        catch (IOException e) {

        }
    }

    class JSONParse extends AsyncTask<Void,Void,Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            loadData();

        }

        @Override
        protected Void doInBackground(Void... arg0) {


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
                                currentTemp = temperatures.getDouble("temp");
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




            return null;


        }
        @Override
        protected void onPostExecute(Void result) {

            super.onPostExecute(result);

//            Intent i = new Intent(MainActivity.this, SplashScreen.class);
//            startActivity(i);

//            static String cityName="";
//            static String weatherInfo="";
//            static double currentTemp=0;
//            static String countryName="";
//            public static Location location;

            new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

                @Override
                public void run() {
                    // This method will be executed once the timer is over
                    // Start your app main activity
                    Intent i = new Intent(SplashScreen.this, MainActivity.class);
                    i.putExtra("cityName", cityName);
                    i.putExtra("weatherInfo",weatherInfo);
                    i.putExtra("currentTemp",currentTemp);
                    i.putExtra("countryName",countryName);
                    startActivity(i);

                    // close this activity
                    finish();
                }
            }, SPLASH_TIME_OUT);



        }
    }
}
