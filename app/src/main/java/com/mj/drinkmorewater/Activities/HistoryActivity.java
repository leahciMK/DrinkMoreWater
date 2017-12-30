package com.mj.drinkmorewater.Activities;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

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

import static com.mj.drinkmorewater.db.DatabaseHandler.subtractDays;

public class HistoryActivity extends AppCompatActivity {

    String selected = "5 days";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
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
    protected void onResume() {
        super.onResume();

        final DatabaseHandler databaseHandler = new DatabaseHandler(getApplicationContext());
        databaseHandler.open();
        //only for testing
        //databaseHandler.insertTenDaysTestwater();

        Cursor cursor = null;
        switch(selected) {
            case "5 days" :  cursor = databaseHandler.getGroupedSumWaterFiveDays();
                            break;
            case "10 days":  cursor = databaseHandler.getGroupedSumWaterTenDays();
                            break;
        }
        cursor.moveToFirst();

        //date formater
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();

        DataPoint[] dt = new DataPoint[cursor.getCount()];
        for (int i=0; i<cursor.getCount(); i++){
            try {
                date = format.parse(cursor.getString(0));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Log.d("test", Integer.toString(i) + Integer.toString(cursor.getInt(1)) + " "+ cursor.getString(0));
            dt[i] = new DataPoint(date, cursor.getInt(1));
            cursor.moveToNext();
        }

        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dt);
        series.setDrawBackground(true);
        series.setBackgroundColor(Color.argb(84, 10, 10, 230));
        series.setDrawDataPoints(true);
        series.setDataPointsRadius(8);

        GraphView graph = (GraphView) findViewById(R.id.graph);
        graph.addSeries(series);
        //for geting max amount of water
        switch(selected) {
            case "5 days" :  cursor = databaseHandler.getMaxGroupedSumWaterFiveDays();
                break;
            case "10 days":  cursor = databaseHandler.getMaxGroupedSumWaterTenDays();
                break;
        }
        cursor.moveToFirst();
        int max = cursor.getInt(0);
        graph.getViewport().setMaxY(max+1000);
        graph.getViewport().setMinY(0);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    String myDateStr = new SimpleDateFormat("dd/MM").format(new Date((new Double(value)).longValue()));
                    return myDateStr;
                    //return super.formatLabel(value, isValueX);
                } else {
                    // show ml on y values
                    return super.formatLabel(value, isValueX) + " ml";

                }
            }
        });
        //added for date formating
//        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this));
        graph.getGridLabelRenderer().setNumHorizontalLabels(5); // only 4 because of the space
        graph.getGridLabelRenderer().setHumanRounding(false);
        Date maxX = new Date();
        maxX.setHours(0);
        maxX.setMinutes(0);
        Date minX = new Date();
        minX.setHours(0);
        minX.setMinutes(0);
        switch(selected) {
            case "5 days" :  minX=subtractDays(maxX, 4);

                break;
            case "10 days":  minX=subtractDays(maxX, 9);
                break;
        }
        // set manual x bounds to have nice steps
        graph.getViewport().setMinX(minX.getTime());
        graph.getViewport().setMaxX(maxX.getTime());
        graph.getViewport().setXAxisBoundsManual(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        GraphView graph = (GraphView) findViewById(R.id.graph);
        graph.removeAllSeries();

        switch (item.getItemId()) {
            case R.id.itemFivedays:
                selected="5 days";
                onResume();
                return super.onOptionsItemSelected(item);

            case R.id.itemTendays:
                selected="10 days";
                onResume();
                return super.onOptionsItemSelected(item);

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { //load toolbar
        getMenuInflater().inflate(R.menu.menu_history, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
