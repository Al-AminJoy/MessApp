package com.trustedoffer.messapp.ModelClass;

public class UpdateReqModelClass {
    private String user_name,user_email,mess_key,request_time,approved_by,approve_time;
    private int breakfast,lunch,dinner,day,month,year;
    private int preBreakfast,preLunch,preDinner;
    private Double debit;
    private Double preDebit;
    private boolean approved;
    private String key;

    public UpdateReqModelClass() {
    }

    public UpdateReqModelClass(String user_name, String user_email, String mess_key, String request_time, String approved_by, String approve_time, int breakfast, int lunch, int dinner, int day, int month, int year, int preBreakfast, int preLunch, int preDinner, Double debit, Double preDebit, boolean approved, String key) {
        this.user_name = user_name;
        this.user_email = user_email;
        this.mess_key = mess_key;
        this.request_time = request_time;
        this.approved_by = approved_by;
        this.approve_time = approve_time;
        this.breakfast = breakfast;
        this.lunch = lunch;
        this.dinner = dinner;
        this.day = day;
        this.month = month;
        this.year = year;
        this.preBreakfast = preBreakfast;
        this.preLunch = preLunch;
        this.preDinner = preDinner;
        this.debit = debit;
        this.preDebit = preDebit;
        this.approved = approved;
        this.key = key;
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

    public String getRequest_time() {
        return request_time;
    }

    public String getApproved_by() {
        return approved_by;
    }

    public String getApprove_time() {
        return approve_time;
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

    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public int getPreBreakfast() {
        return preBreakfast;
    }

    public int getPreLunch() {
        return preLunch;
    }

    public int getPreDinner() {
        return preDinner;
    }

    public Double getDebit() {
        return debit;
    }

    public Double getPreDebit() {
        return preDebit;
    }

    public boolean isApproved() {
        return approved;
    }

    public String getKey() {
        return key;
    }
}
