package com.mj.drinkmorewater.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.mj.drinkmorewater.R;
import com.mj.drinkmorewater.db.DatabaseHandler;
import com.mj.drinkmorewater.db.Water;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton floatingActionButton;

    TextView txtAlreadyWaterPerDay;
    TextView txtAllWaterPerDay;
    TextView currentLocation;
    TextView currentWeatherInfo;

    final private static String getAMountLocation="amountlocation.txt";
    static Location location;

    private RequestQueue requestQueue;
    static String cityName="";
    static String weatherInfo="";
    static double currentTemp=0;
    static String countryName="";

    private Response.Listener<JSONObject> jsonArrayListener = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
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
                    return;
                }
            }

        }
    };

    private Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.d("REST error", error.getMessage());
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //code if the app HAS run before
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean previouslyStarted = prefs.getBoolean("hasRun", false);
        if(!previouslyStarted) {
            SharedPreferences.Editor edit = prefs.edit();
            edit.putBoolean("hasRun", Boolean.TRUE);
            edit.commit();
            Intent intent=new Intent(MainActivity.this,SettingsActivity.class);
            startActivity(intent);
            finish();
        }

        setContentView(R.layout.activity_main);
        txtAlreadyWaterPerDay = (TextView) findViewById(R.id.txtDataWaterToday);
        txtAllWaterPerDay = (TextView) findViewById(R.id.txtDataWaterTotalToday);
        currentLocation = (TextView) findViewById(R.id.txtCurrentLocation);
        currentWeatherInfo =(TextView) findViewById(R.id.txtWeatherInfo);




        if (!isNetworkAvailable()){
            showInternetDisabledAlertToUser();
        }

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        
        floatingActionButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addNewWater = new Intent(MainActivity.this, InsertWater.class);
                startActivity(addNewWater);
            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    private void showInternetDisabledAlertToUser(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Internet is disabled in your device. Would you like to enable it?")
                .setCancelable(false)
                .setPositiveButton("Goto Settings Page To Enable Internet",
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
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadData();

        //http://api.openweathermap.org/data/2.5/weather?lat=46.22&lon=15.16&appid=37783e3aee7050d7c1e9441f395f41bd&units=metric

        String url ="http://api.openweathermap.org/data/2.5/weather?lat="+location.getLatitude()+"&lon="+location.getLongitude()+"&appid=37783e3aee7050d7c1e9441f395f41bd&units=metric";
        JsonObjectRequest request=new JsonObjectRequest(Request.Method.GET,url,null,jsonArrayListener,errorListener);
        requestQueue.add(request);


        DatabaseHandler databaseHandler = new DatabaseHandler(getApplicationContext());
        databaseHandler.open();

        Cursor cursor=databaseHandler.getWaterPerDay();
        cursor=databaseHandler.getSumWaterToday();

        cursor.moveToFirst();

        int amount=cursor.getInt(0);


        txtAlreadyWaterPerDay.setText("Total:   "+String.valueOf(amount) +" ml");
        if(cityName != "" && weatherInfo != "" && countryName != "") {
            currentLocation.setText("City: "+cityName + "\n"+"Country: "+countryName);
            currentWeatherInfo.setText(weatherInfo +"\n" + "Temperature: "+currentTemp + " °C");
        }




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

    private void loadData() {
        try {
            FileInputStream stream = openFileInput(getAMountLocation);
            Scanner scanner = new Scanner(stream);
            txtAllWaterPerDay.setText(scanner.nextLine());

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
