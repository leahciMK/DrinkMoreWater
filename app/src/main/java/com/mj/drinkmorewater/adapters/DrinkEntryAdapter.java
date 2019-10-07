package com.mj.drinkmorewater.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mj.drinkmorewater.R;
import com.mj.drinkmorewater.db.DrinkEntry;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class DrinkEntryAdapter extends ArrayAdapter<DrinkEntry> {

    Context context;
    int layoutResourceID;
    ArrayList<DrinkEntry> data;

    public DrinkEntryAdapter(Context context, int resource, List<DrinkEntry> objects) {
        super(context, resource, objects);
        this.layoutResourceID = resource;
        this.context = context;
        this.data = (ArrayList<DrinkEntry>) objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        DrinkEntryHolder entry = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceID, parent, false);

            entry = new DrinkEntryHolder();
            entry.amount = (TextView) row.findViewById(R.id.amountTextView);
            entry.date = (TextView)row.findViewById(R.id.dateTextView);
            entry.drinkType = (TextView)row.findViewById(R.id.drinkTypeTextView);

            row.setTag(entry);

        }
        else
        {
            entry = (DrinkEntryHolder) row.getTag();
            System.out.println("Entry: " + entry.toString());
        }

        DrinkEntry item = data.get(position);
        entry.amount.setText(String.format("%s",item.getAmount()));
        entry.date.setText(item.getDate());
        entry.drinkType.setText(item.getDrinkType().name());



        return row;
    }

    private class DrinkEntryHolder {
        public TextView amount;
        public TextView date;
        public TextView drinkType;

    }
}
