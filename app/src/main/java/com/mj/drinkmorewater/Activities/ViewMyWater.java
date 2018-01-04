package com.mj.drinkmorewater.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.mj.drinkmorewater.R;
import com.mj.drinkmorewater.db.DatabaseHandler;

public class ViewMyWater extends AppCompatActivity {

    ListView listViewWater;
    CursorAdapter waterAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_my_water);

        listViewWater = (ListView) findViewById(R.id.listViewWater);
        listViewWater.setOnItemClickListener(viewWaterListener);

        String[] from = new String[]{"date","amount","comment"};
        int[] to = new int[]{R.id.dateTextView,R.id.amountTextView,R.id.commentTextView};

        waterAdapter = new SimpleCursorAdapter(ViewMyWater.this,
                R.layout.view_water, null, from, to, 0);
        listViewWater.setAdapter(waterAdapter);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar3);

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

        DatabaseHandler databaseHandler = new DatabaseHandler(getApplicationContext());
        databaseHandler.open();
        waterAdapter.changeCursor(databaseHandler.getAllWatersSortedByDate());
    }

    AdapterView.OnItemClickListener viewWaterListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            //Bundle bundle=new Bundle();
            final Intent viewContact = new Intent(ViewMyWater.this, InsertWater.class);
            //bundle.putInt("_id",arg2);
            viewContact.putExtra("_id",arg3);
            startActivity(viewContact);
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
