package com.mj.drinkmorewater.Activities;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.mj.drinkmorewater.R;
import com.mj.drinkmorewater.db.DatabaseHandler;
import com.mj.drinkmorewater.db.Water;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton floatingActionButton;

    TextView txtWaterPerDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtWaterPerDay=(TextView) findViewById(R.id.txtDataWaterPerDay);

        Water water=new Water();
        Date current=water.getCurrentDate();
        String str=current.toString();
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, str, duration);
        toast.show();

        floatingActionButton=(FloatingActionButton) findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addNewWater=new Intent(MainActivity.this,InsertWater.class);
                startActivity(addNewWater);
            }
        });

        DatabaseHandler databaseHandler=new DatabaseHandler(getApplicationContext());
        //databaseHandler.getAllWater();



    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
