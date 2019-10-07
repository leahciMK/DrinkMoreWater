package com.mj.drinkmorewater.Activities;

import android.database.Cursor;
import android.graphics.Color;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.mj.drinkmorewater.R;
import com.mj.drinkmorewater.db.DatabaseHandler;
import com.mj.drinkmorewater.db.DrinkEntry;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
        setupTitle();
        setupGraph();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        GraphView graph = (GraphView) findViewById(R.id.graph);
        graph.removeAllSeries();

        switch (item.getItemId()) {
            case R.id.itemFivedays:
                selected="5 days";
                setupGraph();
                return super.onOptionsItemSelected(item);

            case R.id.itemTendays:
                selected="10 days";
                setupGraph();
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

    public void setupTitle(){
        Calendar cal = Calendar.getInstance();
        String mesec = new SimpleDateFormat("MMMM").format(cal.getTime());
        SpannableString s = new SpannableString(mesec);
        s.setSpan(new ForegroundColorSpan(Color.WHITE), 0, mesec.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setTitle(s);
    }

    public void setupGraph() {
        final DatabaseHandler databaseHandler = new DatabaseHandler(getApplicationContext());
        databaseHandler.open();

        Cursor cursor = null;
        Map<String, Integer> entryList = databaseHandler.getGroupedEntriesForFiveDays();
        for(Map.Entry entry : entryList.entrySet()) {
            System.out.println("Ln: " + entry.getKey());
        }
        /*switch(selected) {
            case "5 days" :  cursor = databaseHandler.getGroupedSumWaterFiveDays();
                break;
            case "10 days":  cursor = databaseHandler.getGroupedSumWaterTenDays();
                break;
        }
        cursor.moveToFirst();

        //date formater
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();

        //nardimo DataPoint z točkami ki se bojo prikazovale na grafu (podatke dobimo iz baze)
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

        //Graph design
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dt);
        series.setDrawBackground(true);
        series.setBackgroundColor(Color.argb(80, 25, 118, 210));
        series.setColor(Color.rgb(25, 118, 210));
        series.setDrawDataPoints(true);
        series.setDataPointsRadius(8);

        GraphView graph = (GraphView) findViewById(R.id.graph);
        graph.addSeries(series);    //dodamo točke na graf
        //for geting max amount of water
        switch(selected) {
            case "5 days" :  cursor = databaseHandler.getMaxGroupedSumWaterFiveDays();
                break;
            case "10 days":  cursor = databaseHandler.getMaxGroupedSumWaterTenDays();
                break;
        }
        cursor.moveToFirst();
        int max = cursor.getInt(0); //vemo koliko je maximalna količina vode in lahko nastavimo višino y-osi
        graph.getViewport().setMaxY(max+1000);
        graph.getViewport().setMinY(0);
        graph.getViewport().setYAxisBoundsManual(true);
        //formatiramo oznake na y in x oseh
        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    String myDateStr = new SimpleDateFormat("dd").format(new Date((new Double(value)).longValue()));
                    if(myDateStr.substring(0,1).equals("0")){
                        myDateStr=myDateStr.substring(1);
                    }
                    return myDateStr;
                } else {
                    // show ml on y values
                    return super.formatLabel(value, isValueX) + " ml";

                }
            }
        });

        graph.getGridLabelRenderer().setNumHorizontalLabels(5); // only 5  labels on x because of the space
        graph.getGridLabelRenderer().setHumanRounding(false);
        Date maxX = new Date();
        maxX.setHours(0);       //odrežemo ure in minute, pustimo samo datum da je graf lepši
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
        //nastavim omejitve od kje do kje je X-os (odvisno al je izbran 5 ali 10 dni)
        graph.getViewport().setMinX(minX.getTime());
        graph.getViewport().setMaxX(maxX.getTime());
        graph.getViewport().setXAxisBoundsManual(true);*/
    }
}
