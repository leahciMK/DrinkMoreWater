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

    List<DrinkEntry> entries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_my_water);

        listViewWater = (ListView) findViewById(R.id.listViewWater);
        listViewWater.setOnItemClickListener(viewWaterListener);

        databaseHandler = new DatabaseHandler(getApplicationContext());
        entries = databaseHandler.getAllDrinkEntriesSortedByDate();

        List<DrinkEntry> entries = databaseHandler.getAllDrinkEntriesSortedByDate();
        arrayAdapter = new DrinkEntryAdapter(this, R.layout.view_entry, entries);

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

        DatabaseHandler databaseHandler = new DatabaseHandler(getApplicationContext());
        databaseHandler.open();
        entries = databaseHandler.getAllDrinkEntriesSortedByDate();
        arrayAdapter = new DrinkEntryAdapter(this, R.layout.view_entry, entries);
    }

    AdapterView.OnItemClickListener viewWaterListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
            //Bundle bundle=new Bundle();
            final Intent viewContact = new Intent(ViewMyWater.this, InsertWater.class);
            //bundle.putInt("_id",arg2);
            DrinkEntry entry = entries.get(position);
            viewContact.putExtra("drinkEntry", entry);
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
