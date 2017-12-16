package com.mj.drinkmorewater.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by mihaa on 6. 12. 2017.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Water";
    private static final int DATABASE_VERSION = 1;
    private SQLiteDatabase database;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createQuery = "CREATE TABLE water ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE NOT NULL,"
                + "date TEXT NOT NULL, amount INTEGER NOT NULL, comment TEXT);";
        db.execSQL(createQuery);
    }

    /*CREATE TABLE water (
            _id     INTEGER PRIMARY KEY AUTOINCREMENT
            UNIQUE
            NOT NULL,
            date    LONG    NOT NULL,
            amount  INTEGER NOT NULL,
            comment TEXT
    ); */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void open() throws SQLException {
        database = this.getWritableDatabase();
    }

    public void close() {
        if (database != null)
            database.close();
    }

    public void insertWater(Water water) {
        ContentValues newEntry = new ContentValues();
        newEntry.put("date", water.getDate());
        newEntry.put("amount", water.getAmount());
        newEntry.put("comment", water.getComment());


        open();
        database.insert("water", null, newEntry);
        close();
    }

    public void updateWater(long id, Date date, int amount, String comment) {
        ContentValues editWater = new ContentValues();
        editWater.put("date", date.getTime());
        editWater.put("amount", amount);
        editWater.put("comment",comment);

        open();
        database.update("water", editWater, "_id=" + id, null);
        close();
    }
    public Cursor getAllWatersSortedByDate() {
        return database.query("water", new String[]{"_id", "date", "amount","comment"}, null, null, null, null,
                "date");
    }
    public Cursor getWaterPerDay() {
        Water a=new Water();
        String str=a.getDateToString();
        return database.query("water",new String[]{"amount"},"date like "+str,null,null,null,null);
    }

    public Cursor getAllWater() {
        return database.query("water", new String[]{"_id", "date", "amount","comment"}, null, null, null, null,
                null);
    }

    public Cursor getOneWater(long id) {
        return database.query("water", null, "_id=" + id, null, null, null, null);
    }

    public void deleteWater(long id) {
        open();
        database.delete("water", "_id=" + id, null);
        close();
    }

    public Cursor getSumWaterToday() {
        open();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String dateToString=dateFormat.format(date);

        String countQuery="SELECT sum(amount) as 'amount' FROM water WHERE date >= '"+dateToString+"'";

        return database.rawQuery(countQuery,null);
    }

}
