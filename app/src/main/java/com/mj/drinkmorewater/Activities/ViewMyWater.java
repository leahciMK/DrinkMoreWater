package com.mj.drinkmorewater.Activities;

import android.content.Intent;
import android.graphics.Color;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.mj.drinkmorewater.R;
import com.mj.drinkmorewater.adapters.DrinkEntryAdapter;
import com.mj.drinkmorewater.db.DatabaseHandler;
import com.mj.drinkmorewater.db.DrinkEntry;

import java.util.List;

public class ViewMyWater extends AppCompatActivity {

    ListView listViewWater;
    CursorAdapter waterAdapter;
    ArrayAdapter arrayAdapter;
    DatabaseHandler databaseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_my_water);

        listViewWater = (ListView) findViewById(R.id.listViewWater);
        listViewWater.setOnItemClickListener(viewWaterListener);

        databaseHandler = new DatabaseHandler(getApplicationContext());

        String[] from = new String[]{"date","amount","comment"};
        int[] to = new int[]{R.id.dateTextView,R.id.amountTextView,R.id.commentTextView};

        waterAdapter = new SimpleCursorAdapter(ViewMyWater.this,
                R.layout.view_water, null, from, to, 0);

        List<DrinkEntry> entries = databaseHandler.getAllDrinkEntriesSortedByDate();
        for(DrinkEntry entry : entries) {
            System.out.println(entry.toString());
        }

        //arrayAdapter = new ArrayAdapter<DrinkEntry>(ViewMyWater.this, R.layout.view_entry,R.id.textview, databaseHandler.getAllDrinkEntriesSortedByDate());
        arrayAdapter = new DrinkEntryAdapter(this, R.layout.view_entry, databaseHandler.getAllDrinkEntriesSortedByDate());

        //listViewWater.setAdapter(waterAdapter);
        listViewWater.setAdapter(arrayAdapter);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar3);

        setSupportActionBar(myToolbar);
        myToolbar.setNavigationIcon(R.drawable.ic_action_back);
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();

            }
        });

        //setup nice white title
        setupTitle();
    }

    @Override
    protected void onResume() {
        super.onResume();

       /* DatabaseHandler databaseHandler = new DatabaseHandler(getApplicationContext());
        databaseHandler.open();
        waterAdapter.changeCursor(databaseHandler.getAllWatersSortedByDate());
        waterAdapter.changeCursor(databaseHandler.getAllDrinkEntriesSortedByDate());*/

        DatabaseHandler databaseHandler = new DatabaseHandler(getApplicationContext());
        databaseHandler.open();
        arrayAdapter = new DrinkEntryAdapter(this, R.layout.view_entry, databaseHandler.getAllDrinkEntriesSortedByDate());

    }

    AdapterView.OnItemClickListener viewWaterListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            //Bundle bundle=new Bundle();
            final Intent viewContact = new Intent(ViewMyWater.this, InsertWater.class);
            //bundle.putInt("_id",arg2);
            viewContact.putExtra("_id",arg3);
            startActivity(viewContact);
            finish();
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void setupTitle(){
        String title = getResources().getString(R.string.view_my_waters);
        SpannableString s = new SpannableString(title);
        s.setSpan(new ForegroundColorSpan(Color.WHITE), 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setTitle(s);
    }
}
