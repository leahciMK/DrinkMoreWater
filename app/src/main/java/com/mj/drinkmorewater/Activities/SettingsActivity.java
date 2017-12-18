package com.mj.drinkmorewater.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.Spinner;

import com.mj.drinkmorewater.R;
import com.mj.drinkmorewater.db.DatabaseHandler;
import com.mj.drinkmorewater.db.Water;

public class SettingsActivity extends AppCompatActivity {

    SeekBar seekBarAge;
    SeekBar seekBarWeight;
    Spinner spinnerGender;
    Button  btnSave;
    CheckBox checkbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        seekBarAge = (SeekBar)findViewById(R.id.age_seekBar);
        seekBarWeight = (SeekBar)findViewById(R.id.weight_seekBar);
        spinnerGender = (Spinner)findViewById(R.id.gender_spinner);
        btnSave  = (Button)findViewById(R.id.save_btn);
        checkbox = (CheckBox)findViewById(R.id.checkBox);

    }
}
