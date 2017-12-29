package com.mj.drinkmorewater.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
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
import com.mj.drinkmorewater.api.HttpHandler;
import com.mj.drinkmorewater.db.DatabaseHandler;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {

    FloatingActionButton floatingActionButton;
    private ProgressDialog pDialog;

    TextView txtAlreadyWaterPerDay;
    TextView txtAllWaterPerDay;
    public TextView currentLocation;
    public TextView currentWeatherInfo;

    final public static String getAMountLocation="amountlocation.txt";
    public static Location location;

    public RequestQueue requestQueue;
    static String cityName="";
    static String weatherInfo="";
    static double currentTemp=0;
    static String countryName="";
    static String lastWaterEntry="";

    public AlertDialog alert;


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

        DatabaseHandler databaseHandler=new DatabaseHandler(this);
        Cursor cursor=databaseHandler.getLastWaterEntry();
        cursor.moveToFirst();

        lastWaterEntry=cursor.getString(0);

        //Toast.makeText(getApplicationContext(),lastDate,Toast.LENGTH_LONG).show();


        if (!isNetworkAvailable()){
            showInternetDisabledAlertToUser();
        }

        loadData();

        new JSONParse().execute();


        //requestQueue = Volley.newRequestQueue(getApplicationContext());
        
        floatingActionButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addNewWater = new Intent(MainActivity.this, InsertWater.class);
                startActivity(addNewWater);
            }
        });

        if(cityName == "" && weatherInfo == "" && countryName == "") {
            currentLocation.setText("retrieving data from server");
            currentWeatherInfo.setText("retrieving data from server");
        }
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
    protected void onResume() {
        super.onResume();

        loadData();

        //new JSONParse().execute();



        //http://api.openweathermap.org/data/2.5/weather?lat=46.22&lon=15.16&appid=37783e3aee7050d7c1e9441f395f41bd&units=metric

//        String url ="http://api.openweathermap.org/data/2.5/weather?lat="+location.getLatitude()+"&lon="+location.getLongitude()+"&appid=37783e3aee7050d7c1e9441f395f41bd&units=metric";
//        JsonObjectRequest request=new JsonObjectRequest(Request.Method.GET,url,null,jsonArrayListener,errorListener);
//        requestQueue.add(request);


        DatabaseHandler databaseHandler = new DatabaseHandler(getApplicationContext());
        databaseHandler.open();

        Cursor cursor=databaseHandler.getSumWaterToday();

        cursor.moveToFirst();

        int amount=cursor.getInt(0);


        txtAlreadyWaterPerDay.setText("Total:   "+String.valueOf(amount) +" ml");

//        if(cityName != "" && weatherInfo != "" && countryName != "") {
//            currentLocation.setText("City: "+cityName + "\n"+"Country: "+countryName);
//            currentWeatherInfo.setText(weatherInfo +"\n" + "Temperature: "+currentTemp + " °C");
//        }




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


    class JSONParse extends AsyncTask<Void,Void,Void> {

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
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();


        }

        @Override
        protected Void doInBackground(Void... arg0) {

//            requestQueue = Volley.newRequestQueue(getApplicationContext());
//
//            JsonObjectRequest jsonObject;
//
//            String url ="http://api.openweathermap.org/data/2.5/weather?lat="+location.getLatitude()+"&lon="+location.getLongitude()+"&appid=37783e3aee7050d7c1e9441f395f41bd&units=metric";
//            jsonObject=new JsonObjectRequest(Request.Method.GET,url,null,jsonArrayListener,errorListener);
//            requestQueue.add(jsonObject);


            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall("http://api.openweathermap.org/data/2.5/weather?lat="+location.getLatitude()+"&lon="+location.getLongitude()+"&appid=37783e3aee7050d7c1e9441f395f41bd&units=metric");

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
                                "Couldn't get json from server. Check LogCat for possible errors!",
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

            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();

            if(cityName != "" && weatherInfo != "" && countryName != "") {
                currentLocation.setText("City: "+cityName + "\n"+"Country: "+countryName);
                currentWeatherInfo.setText(weatherInfo +"\n" + "Temperature: "+currentTemp + " °C");
            }
        }
    }
}

