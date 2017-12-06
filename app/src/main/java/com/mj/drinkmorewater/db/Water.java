package com.mj.drinkmorewater.db;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by mihaa on 6. 12. 2017.
 */

public class Water implements WaterEntry {
    private Date date;
    private int amount; //in ml
    private String comment;

    public Water() {
        this.date=null;
        this.amount=0;
        this.comment=null;
    }
    public Water(Date date, int amount, String comment) {
        this.date=date;
        this.amount=amount;
        this.comment=comment;
    }

    @Override
    public Date getCurrentDate() {
        Date currentDate = new Date(System.currentTimeMillis());
        return currentDate;
    }

    @Override
    public String getCurrentDateToString() {
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date currentDate=new Date(System.currentTimeMillis());
        
        return df.format(currentDate);

    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
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
