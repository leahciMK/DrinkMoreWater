package com.mj.drinkmorewater.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;


/**
 * Created by mihaa on 6. 12. 2017.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Water";
    private static final int DATABASE_VERSION = 1;
    private SQLiteDatabase database;

    /*
        @TODO
        rewrite Date to new java8 -> localdate etc...
        and replace variables names to appropriate one! -> old "rookie" mistakes
     */

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

    public void updateWater(long id, String date, int amount, String comment) {
        ContentValues editWater = new ContentValues();
        editWater.put("date", date);
        editWater.put("amount", amount);
        editWater.put("comment",comment);

        open();
        database.update("water", editWater, "_id=" + id, null);
        close();
    }
    public Cursor getAllWatersSortedByDate() {
        return database.query("water", new String[]{"_id", "date", "amount","comment"}, null, null, null, null,
                "date desc");
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
        return database.query("water", new String[]{"_id", "date", "amount","comment"}, "_id=" + id, null, null, null, null);
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

    public Cursor getSumWaterFiveDays() {
        open();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        Date daysAgo=subtractDays(date, 5);

        String dateToString=dateFormat.format(date);
        String dateToString2=dateFormat.format(daysAgo);

        String countQuery="SELECT date, sum(amount) as 'amount' FROM water WHERE date >= '"+dateToString2+"'"+" and "+"date <= '"+dateToString+"'";

        return database.rawQuery(countQuery,null);
    }

    public Cursor getSumWaterTenDays() {
        open();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        Date daysAgo=subtractDays(date, 10);

        String dateToString=dateFormat.format(date);
        String dateToString2=dateFormat.format(daysAgo);

        String countQuery="SELECT sum(amount) as 'amount' FROM water WHERE date >= '"+dateToString2+"'"+" and "+"date <= '"+dateToString+"'";

        return database.rawQuery(countQuery,null);
    }

    public static Date subtractDays(Date date, int days) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.add(Calendar.DATE, -days);

        return cal.getTime();
    }

    public Cursor getGroupedSumWaterFiveDays() {
        open();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        Date daysAgo=subtractDays(date, 5);

        String dateToString=dateFormat.format(date);
        String dateToString2=dateFormat.format(daysAgo);

        String countQuery="SELECT date(date) as 'date', sum(amount) as 'amount' FROM water WHERE date >= '"+dateToString2+"'"+" and "+"date <= '"+dateToString+"' GROUP BY date(date)";

        return database.rawQuery(countQuery,null);
    }
    public Cursor getGroupedSumWaterTenDays() {
        open();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        Date daysAgo=subtractDays(date, 10);

        String dateToString=dateFormat.format(date);
        String dateToString2=dateFormat.format(daysAgo);

        String countQuery="SELECT date(date) as 'date', sum(amount) as 'amount' FROM water WHERE date >= '"+dateToString2+"'"+" and "+"date <= '"+dateToString+"' GROUP BY date(date)";

        return database.rawQuery(countQuery,null);
    }
    public Cursor getMaxGroupedSumWaterFiveDays(){
        open();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        Date daysAgo=subtractDays(date, 5);

        String dateToString=dateFormat.format(date);
        String dateToString2=dateFormat.format(daysAgo);

        String countQuery="SELECT max(amount) FROM (SELECT sum(amount) as 'amount' FROM water WHERE date >= '"+dateToString2+"'"+" and "+"date <= '"+dateToString+"' GROUP BY date(date) ORDER BY date(date) DESC)";

        return database.rawQuery(countQuery,null);
    }
    public Cursor getMaxGroupedSumWaterTenDays(){
        open();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        Date daysAgo=subtractDays(date, 10);

        String dateToString=dateFormat.format(date);
        String dateToString2=dateFormat.format(daysAgo);

        String countQuery="SELECT max(amount) FROM (SELECT sum(amount) as 'amount' FROM water WHERE date >= '"+dateToString2+"'"+" and "+"date <= '"+dateToString+"' GROUP BY date(date) ORDER BY date(date) DESC)";

        return database.rawQuery(countQuery,null);
    }

    public Cursor getLastWaterEntry() {
        open();

        String lastEntryQuery="SELECT date FROM water ORDER BY date DESC LIMIT 1";
        return database.rawQuery(lastEntryQuery,null);
    }

    //method for testing
    public void insertTenDaysTestwater(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date datum = new Date();
        Random rnd = new Random();
        int st;

        for (int i=0; i<11; i++){
            st = rnd.nextInt((20 - 10) + 1) + 10;
            Water w = new Water(st*100, "voda", dateFormat.format(datum));
            insertWater(w);
            Log.d("testJulijan", datum.toString()+ "---"+ Integer.toString(st*100));
            datum = subtractDays(datum,1);
        }
    }
}
