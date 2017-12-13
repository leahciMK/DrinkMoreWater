package com.mj.drinkmorewater.Activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mj.drinkmorewater.R;
import com.mj.drinkmorewater.db.DatabaseHandler;
import com.mj.drinkmorewater.db.Water;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton floatingActionButton;

    TextView txtWaterPerDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtWaterPerDay=(TextView) findViewById(R.id.txtDataWaterPerDay);

        Water water=new Water();
        String str=water.getCurrentDate();
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


    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        DatabaseHandler databaseHandler=new DatabaseHandler(getApplicationContext());
        databaseHandler.open();
        Cursor cursor=databaseHandler.getAllWatersSortedByDate();


        List<Water> waterList=new ArrayList<>();

        cursor.moveToNext();
        if (cursor.moveToFirst()) {
            do {
                Water water1= new Water();
                water1.setDate(cursor.getString(1));
                water1.setAmount(Integer.parseInt(cursor.getString(2)));
                water1.setComment(cursor.getString(3));

                waterList.add(water1);

            } while (cursor.moveToNext());
        }
        String s="";

        for(Water w : waterList) {
            s+=String.valueOf(w.getDate())+" "+String.valueOf(w.getAmount())+ " "+w.getComment()+"\n";
            txtWaterPerDay.setText(s);
        }


        cursor=databaseHandler.getSumWaterToday();

        cursor.moveToFirst();

        int amount=cursor.getInt(0);
        s+="Total:   "+String.valueOf(amount) +" ml";

        txtWaterPerDay.setText(s);

    }
}
