package com.trustedoffer.messapp.ModelClass;

public class JoinReqModelClass {
    private String user_name,user_email,user_gender,send_time,mess_key,mess_name,user_key,user_number,user_image;
    private boolean approved;
    private String key;

    public JoinReqModelClass() {
    }

    public JoinReqModelClass(String user_name, String user_email, String user_gender, String send_time, String mess_key,String mess_name,String user_key,String user_number,String user_image, boolean approved,String key) {
        this.user_name = user_name;
        this.user_email = user_email;
        this.user_gender = user_gender;
        this.send_time = send_time;
        this.mess_key = mess_key;
        this.mess_name=mess_name;
        this.user_key=user_key;
        this.user_number=user_number;
        this.user_image=user_image;
        this.approved = approved;
        this.key=key;
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

    public String getMess_name() {
        return mess_name;
    }

    public String getUser_key() {
        return user_key;
    }

    public String getUser_number() {
        return user_number;
    }

    public String getUser_image() {
        return user_image;
    }

    public boolean isApproved() {
        return approved;
    }

    public String getKey() {
        return key;
    }
}
