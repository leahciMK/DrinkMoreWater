package com.mj.drinkmorewater.Utils;

import android.database.Cursor;

import com.mj.drinkmorewater.db.DatabaseColumns;

public class DatabaseUtils {

    public static String[] ALL_COLUMNS = new String[] {DatabaseColumns.date.name(), DatabaseColumns.amount.name(), DatabaseColumns.drinkType.name()};
    public static String[] DATE_AMOUNT_COLUMNS = new String[] {DatabaseColumns.date.name(), DatabaseColumns.amount.name()};

    public static int getDateColumnIndex(Cursor cursor) {
        return cursor.getColumnIndex(DatabaseColumns.date.name());
    }

    public static int getAmountColumnIndex(Cursor cursor) {
        return cursor.getColumnIndex(DatabaseColumns.amount.name());
    }

    public static int getDrinkTypeColumnIndex(Cursor cursor) {
        return cursor.getColumnIndex(DatabaseColumns.drinkType.name());
    }
}
