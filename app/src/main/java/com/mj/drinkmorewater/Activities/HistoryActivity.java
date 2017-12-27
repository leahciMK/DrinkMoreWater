package com.mj.drinkmorewater.Activities;

import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.mj.drinkmorewater.R;
import com.mj.drinkmorewater.db.DatabaseHandler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HistoryActivity extends AppCompatActivity {

    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        spinner = (Spinner)findViewById(R.id.graph_spinner);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar2);

        setSupportActionBar(myToolbar);
        myToolbar.setNavigationIcon(R.drawable.ic_action_back);
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                GraphView graph = (GraphView) findViewById(R.id.graph);
                graph.removeAllSeries();
                onResume();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        final DatabaseHandler databaseHandler = new DatabaseHandler(getApplicationContext());
        databaseHandler.open();
        //only for testing
        //databaseHandler.insertTenDaysTestwater();

        Cursor cursor = null;
        switch(spinner.getSelectedItem().toString()) {
            case "5 days" :  cursor = databaseHandler.getGroupedSumWaterFiveDays();
                            break;
            case "10 days":  cursor = databaseHandler.getGroupedSumWaterTenDays();
                            break;
        }
        cursor.moveToFirst();

        DataPoint[] dt = new DataPoint[cursor.getCount()];
        for (int i=0; i<cursor.getCount(); i++){
            dt[i] = new DataPoint(i, cursor.getInt(1));
            cursor.moveToNext();
        }

        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dt);
        series.setDrawBackground(true);
        series.setBackgroundColor(Color.argb(84, 10, 10, 230));
        series.setDrawDataPoints(true);
        series.setDataPointsRadius(8);

        GraphView graph = (GraphView) findViewById(R.id.graph);
        graph.addSeries(series);
        switch(spinner.getSelectedItem().toString()) {
            case "5 days" :  cursor = databaseHandler.getMaxGroupedSumWaterFiveDays();
                break;
            case "10 days":  cursor = databaseHandler.getMaxGroupedSumWaterTenDays();
                break;
        }
        cursor.moveToFirst();
        int max = cursor.getInt(0);
        if(max+1000 % 2000 ==0){
            max += 1000;
        }else{
         max +=2000; 
        }
        graph.getViewport().setMaxY(max);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    Cursor cursor = null;
                    switch(spinner.getSelectedItem().toString()) {
                        case "5 days" :  cursor = databaseHandler.getGroupedSumWaterFiveDays();
                            break;
                        case "10 days":  cursor = databaseHandler.getGroupedSumWaterTenDays();
                            break;
                    }
                    cursor.moveToPosition(cursor.getCount()- (int)value-1);
                    String newValue = cursor.getString(0);
                    return newValue.substring(5);
                } else {
                    // show L on y values
                    return super.formatLabel(value/1000, isValueX) + " L";
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
