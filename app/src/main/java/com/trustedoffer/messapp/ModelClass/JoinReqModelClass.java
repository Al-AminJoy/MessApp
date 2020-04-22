package com.trustedoffer.messapp.ModelClass;

public class JoinReqModelClass {
    private String user_name,user_email,user_gender,send_time,mess_key;
    private boolean approved;
    private String key;

    public JoinReqModelClass() {
    }

    public JoinReqModelClass(String user_name, String user_email, String user_gender, String send_time, String mess_key, boolean approved) {
        this.user_name = user_name;
        this.user_email = user_email;
        this.user_gender = user_gender;
        this.send_time = send_time;
        this.mess_key = mess_key;
        this.approved = approved;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getUser_email() {
        return user_email;
    }

    public String getUser_gender() {
        return user_gender;
    }

    public String getSend_time() {
        return send_time;
    }

    public String getMess_key() {
        return mess_key;
    }

    public boolean isApproved() {
        return approved;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
