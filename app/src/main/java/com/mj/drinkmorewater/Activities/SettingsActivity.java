package com.mj.drinkmorewater.Activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.mj.drinkmorewater.R;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.util.Scanner;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class SettingsActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    final private static String filename = "data.txt";
    final private static String getAMountLocation="amountlocation.txt";
    SeekBar seekBarAge;
    SeekBar seekBarWeight;
    Spinner spinnerGender;
    Button  btnSave;
    CheckBox checkbox;
    TextView ageText;
    TextView weightText;
    TextView dailyAmountValue;
    TextView locationText;

    int  amountWaterPerDay=0;
    static Location currentLocation;

    private LocationRequest mLocationRequest;

    private long UPDATE_INTERVAL = 1000 * 1000;  /* 1000 secs */
    private long FASTEST_INTERVAL = 200000; /* 200 sec */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            showGPSDisabledAlertToUser();
        }

        seekBarAge = (SeekBar)findViewById(R.id.age_seekBar);
        seekBarWeight = (SeekBar)findViewById(R.id.weight_seekBar);
        spinnerGender = (Spinner)findViewById(R.id.gender_spinner);
        btnSave  = (Button)findViewById(R.id.save_btn);
        checkbox = (CheckBox)findViewById(R.id.checkBox); //physical activity
        ageText = (TextView)findViewById(R.id.age_textView);
        weightText = (TextView)findViewById(R.id.weight_textView);
        dailyAmountValue=(TextView) findViewById(R.id.txtDailyAmountValue);
        locationText=(TextView) findViewById(R.id.txtLocation);

        seekBarAge.setOnSeekBarChangeListener(this);
        seekBarWeight.setOnSeekBarChangeListener(this);
        btnSave.setOnClickListener(saveSettings);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar2);

        setSupportActionBar(myToolbar);
        myToolbar.setNavigationIcon(R.drawable.ic_action_back);
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        //setup of nice white title
        setupTitle();
    }

    // Trigger new location updates at interval
    protected void startLocationUpdates() {

        int permLocationCoarse=checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
        int permLocationFine=checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
        int permInternet=checkSelfPermission(Manifest.permission.INTERNET);

        if (permLocationCoarse != PackageManager.PERMISSION_GRANTED
                || permLocationFine != PackageManager.PERMISSION_GRANTED || permInternet != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this, // Aktivnost, ki zahteva pravice.
                    new String[]{ // Tabela zahtevanih pravic.
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.INTERNET
                    },
                    100 // Poljubna koda zahtevka, tipa int.
            );
        } else {

            // Create the location request to start receiving updates
            mLocationRequest = new LocationRequest();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setInterval(UPDATE_INTERVAL);
            mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

            // Create LocationSettingsRequest object using location request
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
            builder.addLocationRequest(mLocationRequest);
            LocationSettingsRequest locationSettingsRequest = builder.build();

            // Check whether location settings are satisfied
            // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
            SettingsClient settingsClient = LocationServices.getSettingsClient(this);
            settingsClient.checkLocationSettings(locationSettingsRequest);

            // new Google API SDK v11 uses getFusedLocationProviderClient(this)
            getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                        @Override
                        public void onLocationResult(LocationResult locationResult) {
                            // do work here
                            onLocationChanged(locationResult.getLastLocation());
                        }
                    },
                    Looper.myLooper());


        }


    }




    public void getLastLocation() {
        // Get last known recent location using new Google Play Services SDK (v11+)
        FusedLocationProviderClient locationClient = getFusedLocationProviderClient(this);

        locationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // GPS location can be null if GPS is switched off
                        if (location != null) {
                            onLocationChanged(location);

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("MapDemoActivity", "Error trying to get last GPS location");
                        e.printStackTrace();
                    }
                });
    }

    public void onLocationChanged(Location location) {
        // New location has now been determined
        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
        //Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        // You can now create a LatLng Object for use with maps
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        currentLocation=location;

        //locationText.setText(latLng.toString());
    }


    @Override
    public void onRequestPermissionsResult(
            int requestCode, // koda zahtevka
            String permissions[], // tabela zahtevanih pravic
            int[] grantResults) // tabela odobritev
    {
        if (requestCode == 100) { // Če je št. zahtevka enaka 100.
            if (grantResults.length > 0) {

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Prva pravica je bila odobrena.

                }
                if(grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    //druga bila odobrena
                }
                if(grantResults[2] == PackageManager.PERMISSION_GRANTED) {

                }

            }
        }
    }

    private void showGPSDisabledAlertToUser(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
                .setCancelable(false)
                .setPositiveButton("Goto Settings Page To Enable GPS",
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
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
    protected void onResume() {
        super.onResume();

        startLocationUpdates();
        loadData();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        switch (seekBar.getId()) {
            case R.id.age_seekBar:
                if(seekBarAge.getProgress() == 100) {
                    ageText.setText(Integer.toString((int) seekBarAge.getProgress()) + "+ y");
                }else{
                    ageText.setText(Integer.toString((int) seekBarAge.getProgress()) + " y");

                }
                break;

            case R.id.weight_seekBar:
                if(seekBarWeight.getProgress() == 150){
                    weightText.setText(Integer.toString((int) seekBarWeight.getProgress()) + "+ kg");
                }else {
                    weightText.setText(Integer.toString((int) seekBarWeight.getProgress()) + " kg");
                }
                break;
        }
    }

    public void calculateWaterPerDay() {
        DecimalFormat df = new DecimalFormat("#.##");
        amountWaterPerDay = 2000;
        int weight = seekBarWeight.getProgress();
        if(weight > 60){
            amountWaterPerDay+=200;
        }
        if(weight >80){
            amountWaterPerDay+=300;
        }
        if(weight >=100){
            amountWaterPerDay+=300;
        }
        if(weight >=120){
            amountWaterPerDay+=200;
        }
        if(spinnerGender.getSelectedItem().equals("Male")){
            amountWaterPerDay+=300;
        }
        int age = seekBarAge.getProgress();
        if(age > 20 ){
            amountWaterPerDay+=200;
        }
        if(age > 30 ){
            amountWaterPerDay+=200;
        }
        if(age > 50){
            amountWaterPerDay+=200;
        }
        if(age >=70){
            amountWaterPerDay+=100;
        }
        if(checkbox.isChecked()) {
            if(weight >= 80){
                amountWaterPerDay+=700;
            }else if(weight >=100){
                amountWaterPerDay+=800;
            }else{
                amountWaterPerDay+=500;
            }
        }

        dailyAmountValue.setText(String.valueOf(df.format(amountWaterPerDay)) +" ml");
    }

    View.OnClickListener saveSettings = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(seekBarAge.getProgress() != 0 && seekBarWeight.getProgress() != 0 && currentLocation != null) {
                calculateWaterPerDay();
                saveData();

            }else{
                if(currentLocation == null) {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Please enable GPS.", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Invalid input.", Toast.LENGTH_SHORT);
                    toast.show();
                }

            }
        }
    };

    @Override
    public void onStartTrackingTouch(SeekBar seekBarAge) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBarAge) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //code if the app HAS run before

        SharedPreferences prefs = getSharedPreferences("isSettingsFirstOpen", MODE_PRIVATE);
        boolean previouslyStarted = prefs.getBoolean("hasRun", false);

        if (!previouslyStarted) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("hasRun", true);
            editor.commit(); //

            loadData();
            Intent i = new Intent(SettingsActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        }

    }

    private void saveData() {
        try {
            FileOutputStream stream = openFileOutput(filename, MODE_PRIVATE);
            OutputStreamWriter writer = new OutputStreamWriter(stream);
            writer.write(spinnerGender.getSelectedItem().toString() + System.lineSeparator());
            int age = (int)seekBarAge.getProgress();
            int weight = (int)seekBarWeight.getProgress();
            writer.write(Integer.toString(age) + System.lineSeparator());
            writer.write(Integer.toString(weight) + System.lineSeparator());
            writer.write(checkbox.isChecked() ? "YES" : "NO");
            writer.write(System.lineSeparator());
            writer.write(amountWaterPerDay + System.lineSeparator());
            writer.write(currentLocation.getLongitude() + " "+currentLocation.getLatitude()+System.lineSeparator());

            writer.close();

            stream=openFileOutput(getAMountLocation,MODE_PRIVATE);
            writer=new OutputStreamWriter(stream);
            writer.write(amountWaterPerDay + System.lineSeparator());
            writer.write( currentLocation.getLongitude() + " "+currentLocation.getLatitude()+System.lineSeparator());
            writer.close();


            Toast toast = Toast.makeText(getApplicationContext(),
                    "Data saved", Toast.LENGTH_SHORT);
            toast.show();

        }
        catch (IOException e) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Saving data failed", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void loadData() {
        try {
            FileInputStream stream = openFileInput(filename);
            Scanner scanner = new Scanner(stream);
            if (scanner.nextLine().equals("Male"))
                spinnerGender.setSelection(0);
            else
                spinnerGender.setSelection(1);
            int age = (int)(Integer.parseInt(scanner.nextLine()));
            seekBarAge.setProgress(age);
            int weight = (int)(Integer.parseInt(scanner.nextLine()));
            seekBarWeight.setProgress(weight);
            checkbox.setChecked(scanner.nextLine().equals("YES"));
            dailyAmountValue.setText(scanner.nextLine() + " ml");
            locationText.setText("Location: " + scanner.nextLine());
            scanner.close();
        }
        catch (IOException e) {
            spinnerGender.setSelection(0);
            seekBarAge.setProgress(0);
            seekBarWeight.setProgress(0);
            checkbox.setChecked(false);
            dailyAmountValue.setText("");
        }
    }

    public void setupTitle(){
        String title = getResources().getString(R.string.settings);
        SpannableString s = new SpannableString(title);
        s.setSpan(new ForegroundColorSpan(Color.WHITE), 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setTitle(s);
    }
}
