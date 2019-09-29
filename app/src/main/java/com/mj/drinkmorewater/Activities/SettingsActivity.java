package com.mj.drinkmorewater.Activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Looper;

import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.mj.drinkmorewater.R;
import com.mj.drinkmorewater.components.SettingsAnswerType;
import com.mj.drinkmorewater.components.resources.CoreResourceBundle;

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

    private CoreResourceBundle coreResourceBundle;


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
                    this,
                    new String[]{
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.INTERNET
                    },
                    100
            );
        } else {

            // Create the location request to start receiving updates
            mLocationRequest = new LocationRequest();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
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

    public void onLocationChanged(Location location) {
        currentLocation = location;
    }


    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String permissions[],
            int[] grantResults) {
        if (requestCode == 100) {
            if (grantResults.length > 0) {

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                }
                if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                }
                if (grantResults[2] == PackageManager.PERMISSION_GRANTED) {

                }

            }
        }
    }

    private void showGPSDisabledAlertToUser(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(coreResourceBundle.getMessage("disabled_gps"))
                .setCancelable(false)
                .setPositiveButton(coreResourceBundle.getMessage("disabled_gps_instructions"),
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
                    ageText.setText(String.format("%s + y", seekBar.getProgress()));
                }else{
                    ageText.setText(String.format("%s y", seekBar.getProgress()));

                }
                break;

            case R.id.weight_seekBar:
                if(seekBarWeight.getProgress() == 150){
                    weightText.setText(String.format("%s +kg", seekBarWeight.getProgress()));
                }else {
                    weightText.setText(String.format("%s kg", seekBarWeight.getProgress()));
                }
                break;
        }
    }

    public void calculateWaterPerDay() {
        DecimalFormat df = new DecimalFormat("#.##");
        amountWaterPerDay= (seekBarWeight.getProgress() / 30) * 1000;
        if(checkbox.isChecked()) {
            if(seekBarAge.getProgress() > 50) {
                if(spinnerGender.getSelectedItem().toString().equals("Male")) {
                    amountWaterPerDay += (seekBarWeight.getProgress() / 10) * 205;
                } else {
                    amountWaterPerDay += (seekBarWeight.getProgress() / 10) * 185;
                }

            } else {
                if(spinnerGender.getSelectedItem().toString().equals("Male")) {
                    amountWaterPerDay += (seekBarWeight.getProgress() / 10) * seekBarAge.getProgress() + 350;
                } else {
                    amountWaterPerDay += (seekBarWeight.getProgress() / 10) * seekBarAge.getProgress();
                }

            }

        }

        dailyAmountValue.setText(String.format("%s ml", df.format(amountWaterPerDay)));
    }

    View.OnClickListener saveSettings = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(seekBarAge.getProgress() != 0 && seekBarWeight.getProgress() != 0 && currentLocation != null) {
                calculateWaterPerDay();
                saveData();

                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                startActivity(intent);
                finish();


            }else{
                if(currentLocation == null) {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            coreResourceBundle.getMessage("disabled_gps_instructions"), Toast.LENGTH_SHORT);
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
        finish();
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
            writer.write(checkbox.isChecked() ? SettingsAnswerType.YES.name() : SettingsAnswerType.NO.name());
            writer.write(System.lineSeparator());
            writer.write(amountWaterPerDay + System.lineSeparator());
            writer.write(currentLocation.getLongitude() + " "+currentLocation.getLatitude()+System.lineSeparator());

            writer.close();

            stream=openFileOutput(getAMountLocation,MODE_PRIVATE);
            writer=new OutputStreamWriter(stream);
            writer.write(amountWaterPerDay + System.lineSeparator());
            writer.write(currentLocation.getLongitude() + " "+currentLocation.getLatitude()+System.lineSeparator());
            writer.close();


            Toast toast = Toast.makeText(getApplicationContext(),
                    coreResourceBundle.getMessage("saving_data_success"), Toast.LENGTH_SHORT);
            toast.show();

        }
        catch (IOException e) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    coreResourceBundle.getMessage("saving_failed_failed"), Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void loadData() {
        try {
            FileInputStream stream = openFileInput(filename);
            Scanner scanner = new Scanner(stream);
            if (scanner.nextLine().equals("Male")) {
                spinnerGender.setSelection(0);
            }

            else {
                spinnerGender.setSelection(1);
            }

            int age = Integer.parseInt(scanner.nextLine());
            seekBarAge.setProgress(age);

            int weight = Integer.parseInt(scanner.nextLine());
            seekBarWeight.setProgress(weight);

            checkbox.setChecked(scanner.nextLine().equals("YES"));
            dailyAmountValue.setText(String.format("%s ml", scanner.nextLine()));
            locationText.setText(String.format("Location: %s", scanner.nextLine()));
            scanner.close();
        } catch (IOException e) {
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
