package com.trustedoffer.messapp.ModelClass;

public class DebitReqModelClass {

    private String user_name,user_email,mess_key,request_time,approved_by,approve_time;
    private int day,month,year;
    private Double debit;
    private boolean approved;
    private String key;
    public DebitReqModelClass() {

    }

    public DebitReqModelClass(String user_name,String user_email, String mess_key, String request_time, String approved_by, String approve_time, int day, int month, int year, Double debit, boolean approved) {
        this.user_name=user_name;
        this.user_email = user_email;
        this.mess_key = mess_key;
        this.request_time = request_time;
        this.approved_by = approved_by;
        this.approve_time = approve_time;
        this.day = day;
        this.month = month;
        this.year = year;
        this.debit = debit;
        this.approved = approved;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
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

    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public Double getDebit() {
        return debit;
    }

    public boolean isApproved() {
        return approved;
    }
}
