package com.mj.drinkmorewater.Activities;

import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.mj.drinkmorewater.R;
import com.mj.drinkmorewater.db.DatabaseHandler;
import com.mj.drinkmorewater.db.Water;

import java.text.DecimalFormat;

public class SettingsActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    SeekBar seekBarAge;
    SeekBar seekBarWeight;
    Spinner spinnerGender;
    Button  btnSave;
    CheckBox checkbox;
    TextView ageText;
    TextView weightText;
    TextView dailyAmountValue;

    int  amountWaterPerDay=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        seekBarAge = (SeekBar)findViewById(R.id.age_seekBar);
        seekBarWeight = (SeekBar)findViewById(R.id.weight_seekBar);
        spinnerGender = (Spinner)findViewById(R.id.gender_spinner);
        btnSave  = (Button)findViewById(R.id.save_btn);
        checkbox = (CheckBox)findViewById(R.id.checkBox);
        ageText = (TextView)findViewById(R.id.age_textView);
        weightText = (TextView)findViewById(R.id.weight_textView);
        dailyAmountValue=(TextView) findViewById(R.id.txtDailyAmountValue);

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
        amountWaterPerDay= (seekBarWeight.getProgress() / 30) * 1000;
        if(checkbox.isChecked()) {
            if(seekBarAge.getProgress() > 50) {
                amountWaterPerDay += (seekBarWeight.getProgress() / 10) * 200;
            } else {
                amountWaterPerDay += (seekBarWeight.getProgress() / 10) * seekBarAge.getProgress();
            }

        }

        dailyAmountValue.setText(String.valueOf(df.format(amountWaterPerDay)) +" ml");
    }

    View.OnClickListener saveSettings = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(seekBarAge.getProgress() != 0 && seekBarWeight.getProgress() != 0) {
                calculateWaterPerDay();
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
}
