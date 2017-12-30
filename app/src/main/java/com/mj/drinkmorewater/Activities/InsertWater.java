package com.mj.drinkmorewater.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.mj.drinkmorewater.R;
import com.mj.drinkmorewater.db.DatabaseHandler;
import com.mj.drinkmorewater.db.Water;

public class InsertWater extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener, Spinner.OnItemSelectedListener{

    SeekBar seekBar;
    EditText editTextComment;
    TextView txtMl;
    Button btnInsert;
    Spinner drinksSpinner;
    TextView textComment;
    TextView textDrinks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_water);

        seekBar=(SeekBar)findViewById(R.id.seekBarWaterAmount);
        editTextComment=(EditText)findViewById(R.id.editTextComment);
        textComment=(TextView) findViewById(R.id.txtComment);
        txtMl=(TextView)findViewById(R.id.txtMl);
        btnInsert=(Button)findViewById(R.id.btnInsert);
        drinksSpinner=(Spinner) findViewById(R.id.drinks_spinner);
        textDrinks=(TextView) findViewById(R.id.drinks_text);

        btnInsert.setOnClickListener(saveWaterButtonClicked);
        seekBar.setOnSeekBarChangeListener(this);
        drinksSpinner.setOnItemSelectedListener(this);


        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);

        setSupportActionBar(myToolbar);
        myToolbar.setNavigationIcon(R.drawable.ic_action_back);
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        seekBar.setMax(40);
    }

    View.OnClickListener saveWaterButtonClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            saveWater();
        }
    };



    private void saveWater() {
        DatabaseHandler databaseHandler= new DatabaseHandler(getApplicationContext());

        if (getIntent().getExtras() == null) {
            int amount= seekBar.getProgress()*50;
            if(amount!=0) {
                //String comment = editTextComment.getText().toString();

                String drink=drinksSpinner.getSelectedItem().toString();

                Water water=null;

                if(!drink.equals("Custom")) {
                    water = new Water(amount, drink);
                    databaseHandler.insertWater(water);
                } else {
                    String comment = editTextComment.getText().toString();
                    water = new Water(amount, comment);

                    databaseHandler.insertWater(water);
                }

                Toast toast = Toast.makeText(getApplicationContext(), water.getCurrentDateToString() + " " + Integer.toString(water.getAmount()) + " " + water.getComment(), Toast.LENGTH_SHORT);
                toast.show();

                seekBar.setProgress(5);
                editTextComment.setText("");
                onBackPressed();
            }else{
                Toast.makeText(this, this.getString(R.string.no_water_error)
                        ,
                        Toast.LENGTH_LONG).show();
            }
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if(adapterView.getSelectedItem().toString().equals("Custom")) {
            editTextComment.setVisibility(View.VISIBLE);
            textComment.setVisibility(View.VISIBLE);
            drinksSpinner.setVisibility(View.INVISIBLE);
            textDrinks.setVisibility(View.INVISIBLE);

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        txtMl.setText(Integer.toString((int) seekBar.getProgress()*50) + " ml");
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.viewWater:
                Intent intent = new Intent(InsertWater.this,ViewMyWater.class);
                startActivity(intent);
                return super.onOptionsItemSelected(item);


            default:
                return super.onOptionsItemSelected(item);


        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { //load toolbar
        getMenuInflater().inflate(R.menu.menu_insert_water, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
