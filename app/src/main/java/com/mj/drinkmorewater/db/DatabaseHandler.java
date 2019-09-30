package com.mj.drinkmorewater.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.mj.drinkmorewater.Utils.DateUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;


/**
 * Created by mihaa on 6. 12. 2017.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "DrinkEntry";
    private static final String DATABASE_TABLE_NAME = "drinkEntry";
    private static final int DATABASE_VERSION = 2;
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
        /*String createQuery = "CREATE TABLE water ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE NOT NULL,"
                + "date TEXT NOT NULL, amount INTEGER NOT NULL, drinkType TEXT);";*/

        String queryString = String.format("CREATE TABLE %s (_id INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE NOT NULL," +
                "%s TEXT NOT NULL, %s INTEGER NOT NULL, %s TEXT);", DATABASE_TABLE_NAME, DatabaseColumns.date.name(),
                DatabaseColumns.amount.name(), DatabaseColumns.drinkType);
        db.execSQL(queryString);
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

    public void insertEntry(DrinkEntry entry) {
        ContentValues newEntry = new ContentValues();
        newEntry.put(DatabaseColumns.date.name(), entry.getDate());
        newEntry.put(DatabaseColumns.amount.name(), entry.getAmount());
        newEntry.put(DatabaseColumns.drinkType.name(), entry.getDrinkType().name());


        open();
        database.insert(DATABASE_TABLE_NAME, null, newEntry);
        close();
    }

    public void updateWater(long id, String date, int amount, String drinkTyoe) {
        ContentValues editWater = new ContentValues();
        editWater.put(DatabaseColumns.date.name(), date);
        editWater.put(DatabaseColumns.amount.name(), amount);
        editWater.put(DatabaseColumns.drinkType.name(), drinkTyoe);

        open();
        database.update(DATABASE_TABLE_NAME, editWater, "_id=" + id, null);
        close();
    }
    public Cursor getAllDrinkEntriesSortedByDate() {
        return database.query(DATABASE_TABLE_NAME, new String[]{"_id", DatabaseColumns.date.name(), DatabaseColumns.amount.name(),DatabaseColumns.drinkType.name()}, null, null, null, null,
                String.format("%s desc", DatabaseColumns.date.name()));
    }
    public Cursor getTodayDrinkEntries() {
        String todayDate = DateUtils.getFormattedCurrentDate();
        return database.query(DATABASE_TABLE_NAME,new String[]{"amount"},"date like "+todayDate,null,null,null,null);
    }

    public Cursor getAllEntries() {
        return database.query(DATABASE_TABLE_NAME, new String[]{"_id", DatabaseColumns.date.name(), DatabaseColumns.amount.name(),DatabaseColumns.drinkType.name()}, null, null, null, null,
                null);
    }

    public Cursor getOneWater(long id) {
        return database.query(DATABASE_TABLE_NAME, new String[]{"_id", "date", "amount","comment"}, "_id=" + id, null, null, null, null);
    }

    public void deleteEntry(long id) {
        open();
        database.delete(DATABASE_TABLE_NAME, "_id=" + id, null);
        close();
    }

    public int getTodaySum() {
        open();
        String todayDate = DateUtils.getFormattedCurrentDate();

        String countQuery="SELECT sum(amount) as 'amount' FROM water WHERE date >= '"+todayDate+"'";

        Cursor cursor = database.rawQuery(countQuery,null);
        int sum = 0;
        while(cursor.moveToNext()) {
            sum = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseColumns.amount.name()));
        }
        return sum;
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
    /*public void insertTenDaysTestwater(){
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
    }*/
}
