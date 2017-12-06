package com.mj.drinkmorewater.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



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
                + "_id integer primary key autoincrement,"
                + "date LONG, amount INTEGER, comment TEXT);";
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
        newEntry.put("date", water.getDate().getTime());
        newEntry.put("amount", water.getAmount());
        newEntry.put("comment", water.getComment());

        open();
        database.insert("water", null, newEntry);
        close();
    }

//    public void updateWater(long id, String name, String email, String phone, String state,
//                              String city,String comment) {
//        ContentValues editContact = new ContentValues();
//        editContact.put("name", name);
//        editContact.put("email", email);
//        editContact.put("phone", phone);
//        editContact.put("street", state);
//        editContact.put("city", city);
//        editContact.put("comment",comment);
//
//        open();
//        database.update("contacts", editContact, "_id=" + id, null);
//        close();
//    }
//    public Cursor getAllContactsSortedByName() {
//        return database.query("contacts", new String[]{"_id", "name", "phone"}, null, null, null, null,
//                "name");
//    }
//
//    public Cursor getAllContacts() {
//        return database.query("contacts", new String[]{"_id", "name", "phone"}, null, null, null, null,
//                null);
//    }
//
//    public Cursor getOneContact(long id) {
//        return database.query("contacts", null, "_id=" + id, null, null, null, null);
//    }
//
//    public void deleteContact(long id) {
//        open();
//        database.delete("contacts", "_id=" + id, null);
//        close();
//    }

}
