package com.mj.drinkmorewater.Activities;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mj.drinkmorewater.R;
import com.mj.drinkmorewater.components.DrinkType;
import com.mj.drinkmorewater.db.DatabaseHandler;
import com.mj.drinkmorewater.db.DrinkEntry;

public class InsertWater extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener, Spinner.OnItemSelectedListener {

    SeekBar seekBar;
    EditText editTextComment;
    TextView txtMl;
    Button btnInsert;
    Spinner drinksSpinner;
    TextView textComment;
    TextView textDrinks;

    long idOfWater = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_water);

        seekBar = (SeekBar) findViewById(R.id.seekBarWaterAmount);
        editTextComment = (EditText) findViewById(R.id.editTextComment);
        textComment = (TextView) findViewById(R.id.txtComment);
        txtMl = (TextView) findViewById(R.id.txtMl);
        btnInsert = (Button) findViewById(R.id.btnInsert);
        drinksSpinner = (Spinner) findViewById(R.id.drinks_spinner);
        textDrinks = (TextView) findViewById(R.id.drinks_text);

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

        //setup nice white title
        setupTitle();
    }

    View.OnClickListener saveWaterButtonClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            saveWater();
        }
    };

    private int getIndex(Spinner spinner, String myString) {
        int index = 0;

        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)) {
                index = i;
                break;
            }
        }
        return index;
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                idOfWater = bundle.getLong("_id");
                DatabaseHandler databaseHandler=new DatabaseHandler(this);
                databaseHandler.open();
                //Cursor cursor=databaseHandler.getOneWater(idOfWater);
                DrinkEntry entry = databaseHandler.getEntry(idOfWater);
                System.out.println("Which entry: " + entry.toString());

                seekBar.setProgress(entry.getAmount() / 50);
                drinksSpinner.setSelection(getIndex(drinksSpinner, entry.getDrinkType().toString()));


                /*if(cursor.moveToFirst()) {

                    String date=cursor.getString(1);
                    int amount=cursor.getInt(2);
                    String comment=cursor.getString(3);

                    seekBar.setProgress(amount/50);
                    int index=getIndex(drinksSpinner,comment);


                    drinksSpinner.setSelection(index);
                }*/


                if (idOfWater == 0) {
                    idOfWater=0;
                }
            }
        }
    }

    private void saveWater() {
        DatabaseHandler databaseHandler = new DatabaseHandler(getApplicationContext());

        if (getIntent().getExtras() == null) {
            int amount = seekBar.getProgress() * 50;
            if (amount != 0) {
                String drink = drinksSpinner.getSelectedItem().toString();

                DrinkEntry entry = null;

                if (!drink.equals("Custom")) {
                    entry = new DrinkEntry(amount, DrinkType.valueOf(drink));
                    databaseHandler.insertEntry(entry);
                } else {
                    String drinkType = editTextComment.getText().toString();
                    entry = new DrinkEntry(amount, DrinkType.valueOf(drinkType));

                    databaseHandler.insertEntry(entry);
                }

                Toast toast = Toast.makeText(getApplicationContext(), String.format(
                        "%s %s %s", entry.getDate(), entry.getAmount(), entry.getDrinkType().name()
                ), Toast.LENGTH_SHORT);
                toast.show();

                seekBar.setProgress(5);
                editTextComment.setText("");
                onBackPressed();
            } else {

                Toast.makeText(this, this.getString(R.string.no_water_error)
                        ,
                        Toast.LENGTH_LONG).show();
            }
        } else {
            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                idOfWater = bundle.getLong("_id");
                databaseHandler.open();
                /*Cursor cursor = databaseHandler.getOneWater(idOfWater);

                DrinkEntry entry = databaseHandler.getEntry(idOfWater);

                //databaseHandler.updateEntry(idOfWater, );

                if (cursor.moveToFirst()) {
                    int amount1 = seekBar.getProgress() * 50;
                    String drink = drinksSpinner.getSelectedItem().toString();

                    if (!drink.equals("Custom")) {
                        databaseHandler.updateWater(cursor.getLong(0), cursor.getString(1), amount1, drink);

                    } else {
                        String comment = editTextComment.getText().toString();

                        databaseHandler.updateWater(cursor.getLong(0), cursor.getString(1), amount1, comment);
                    }

                    Toast.makeText(this, "Succesfully updated"
                            ,
                            Toast.LENGTH_LONG).show();

                    Intent intent1=new Intent(this,ViewMyWater.class);
                    startActivity(intent1);

                }*/


                if (idOfWater == 0) {
                    idOfWater = 0;
                }
            }

        }
    }



        @Override
        public void onItemSelected (AdapterView < ? > adapterView, View view,int i, long l){
            if (adapterView.getSelectedItem().toString().equals("Custom")) {
                editTextComment.setVisibility(View.VISIBLE);
                textComment.setVisibility(View.VISIBLE);
                drinksSpinner.setVisibility(View.INVISIBLE);
                textDrinks.setVisibility(View.INVISIBLE);

            }
        }

        @Override
        public void onNothingSelected (AdapterView < ? > adapterView){

        }

        @Override
        public void onProgressChanged (SeekBar seekBar,int i, boolean b){
            Intent intent = getIntent();
            if (intent == null) {
                txtMl.setText(Integer.toString((int) seekBar.getProgress() * 50) + " ml");
            } else {
                txtMl.setText(Integer.toString(seekBar.getProgress() * 50) + " ml");
            }
        }

        @Override
        public void onStartTrackingTouch (SeekBar seekBar){
        }

        @Override
        public void onStopTrackingTouch (SeekBar seekBar){
        }

        @Override
        public boolean onOptionsItemSelected (MenuItem item){
            switch (item.getItemId()) {
                case R.id.viewWater:
                    Intent intent = new Intent(InsertWater.this, ViewMyWater.class);
                    startActivity(intent);
                    return super.onOptionsItemSelected(item);


                default:
                    return super.onOptionsItemSelected(item);


            }

        }

        @Override
        public boolean onCreateOptionsMenu (Menu menu){ //load toolbar
            getMenuInflater().inflate(R.menu.menu_insert_water, menu);
            return true;
        }

        @Override
        public void onBackPressed () {
            super.onBackPressed();
            finish();
        }

    public void setupTitle(){
        String title = getResources().getString(R.string.insert_water);
        SpannableString s = new SpannableString(title);
        s.setSpan(new ForegroundColorSpan(Color.WHITE), 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setTitle(s);
    }
    }
