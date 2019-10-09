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
import com.mj.drinkmorewater.Utils.DateUtils;
import com.mj.drinkmorewater.db.DatabaseHandler;
import com.mj.drinkmorewater.db.DrinkEntry;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

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
        //graph.removeAllSeries();

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
        Map<String, Integer> entryList = new TreeMap<>();

        switch (selected) {
            case "5 days":
                entryList = databaseHandler.getGroupedEntriesForFiveDays();
                break;
            case "10 days":
                entryList = databaseHandler.getGroupedEntriesForTenDays();
                break;
        }

        DataPoint[] dt = new DataPoint[entryList.size()];
        int i = 0;
        for(Map.Entry entry : entryList.entrySet()) {
            DateFormat format = new SimpleDateFormat(DateUtils.DATE, Locale.ENGLISH);
            Date date = null;
            try {
                date = format.parse(entry.getKey().toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            dt[i] = new DataPoint(date, (double) (int) entry.getValue());
            i++;
        }

        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dt);
        series.setDrawBackground(true);
        series.setBackgroundColor(Color.argb(80, 25, 118, 210));
        series.setColor(Color.rgb(25, 118, 210));
        series.setDrawDataPoints(true);
        series.setDataPointsRadius(8);

        GraphView graph = (GraphView) findViewById(R.id.graph);
        graph.addSeries(series);    //dodamo točke na graf
        //for geting max amount of water
        int maxAmount = 0;
        switch(selected) {
            case "5 days" :  maxAmount = databaseHandler.getMaxGroupedSumWaterFiveDays();
                break;
            case "10 days":  maxAmount = databaseHandler.getMaxGroupedSumWaterTenDays();
                break;
        }

        graph.getViewport().setMaxY(maxAmount+500);
        graph.getViewport().setMinY(0);
        graph.getViewport().setYAxisBoundsManual(true);
        //formatiramo oznake na y in x oseh
        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    String myDateStr = new SimpleDateFormat("d.MM").format(new Date((new Double(value)).longValue()));
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
            case "5 days" :  minX=subtractDays(maxX, 5);
                break;
            case "10 days":  minX=subtractDays(maxX, 10);
                break;
        }
        //nastavim omejitve od kje do kje je X-os (odvisno al je izbran 5 ali 10 dni)
        graph.getViewport().setMinX(minX.getTime());
        graph.getViewport().setMaxX(maxX.getTime());
        graph.getViewport().setXAxisBoundsManual(true);
    }
}
