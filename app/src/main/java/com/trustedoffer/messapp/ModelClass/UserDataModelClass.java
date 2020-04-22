package com.trustedoffer.messapp.ModelClass;

import android.text.BoringLayout;

import com.google.firebase.Timestamp;

public class UserDataModelClass {
    private String user_name,user_email,mess_key;
    private String date,update_time;
    private int breakfast,lunch,dinner,day,month,year;
    private Boolean updated;
    double debit;
    public UserDataModelClass() {
    }

    public UserDataModelClass(String user_name, String user_email, String mess_key, String date, String update_time, int breakfast, int lunch, int dinner,  int day, int month, int year, Boolean updated,double debit) {
        this.user_name = user_name;
        this.user_email = user_email;
        this.mess_key = mess_key;
        this.date = date;
        this.update_time = update_time;
        this.breakfast = breakfast;
        this.lunch = lunch;
        this.dinner = dinner;
        this.day = day;
        this.month = month;
        this.year = year;
        this.updated = updated;
        this.debit = debit;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getUser_email() {
        return user_email;
    }

    public String getMess_key() {
        return mess_key;
    }

    public String getDate() {
        return date;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public int getBreakfast() {
        return breakfast;
    }

    public int getLunch() {
        return lunch;
    }

    public int getDinner() {
        return dinner;
    }

    public double getDebit() {
        return debit;
    }

    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public Boolean getUpdated() {
        return updated;
    }
}
