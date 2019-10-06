package com.mj.drinkmorewater.db;

import com.mj.drinkmorewater.Utils.DateUtils;
import com.mj.drinkmorewater.components.DrinkType;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

import androidx.annotation.NonNull;

/**
 * Created by mihaa on 6. 12. 2017.
 */

public class DrinkEntry {
    private String date;
    private int amount; //in ml
    private DrinkType drinkType = DrinkType.Water;

    public DrinkEntry() {

    }
    public DrinkEntry(int amount, DrinkType drinkType) {
        this.amount=amount;
        this.date = DateUtils.getFormattedCurentDateAndTime();
        this.drinkType = drinkType;
    }

    public DrinkEntry(int amount, DrinkType drinkType,String date) {
        this.amount=amount;
        this.date= date;
        this.drinkType=drinkType;
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

    public DrinkType getDrinkType() {
        return drinkType;
    }

    public void setDrinkType(DrinkType drinkType) {
        this.drinkType = drinkType;
    }

    @NonNull
    @Override
    public String toString() {
        return String.format("%s %s %s", this.date, this.amount, this.drinkType.name());
    }
}
