package com.mj.drinkmorewater.db;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by mihaa on 6. 12. 2017.
 */

public class Water implements WaterEntry {
    private String date;
    private int amount; //in ml
    private String comment;

    public Water() {
        this.date=null;
        this.amount=0;
        this.comment=null;
    }
    public Water(int amount, String comment) {
        this.amount=amount;
        this.comment=comment;
        this.date=getCurrentDate();
    }

    public Water(int amount, String comment,String date) {
        this.amount=amount;
        this.comment=comment;
        this.date= date;
    }

    @Override
    public String getCurrentDate() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date currentDate=new Date(System.currentTimeMillis());
        return df.format(currentDate);
    }

    @Override
    public String getCurrentDateToString() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date currentDate=new Date(System.currentTimeMillis());

        return df.format(currentDate);

    }

    @Override
    public String getDateToString() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date currentDate=new Date(System.currentTimeMillis());

        return df.format(currentDate);
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
