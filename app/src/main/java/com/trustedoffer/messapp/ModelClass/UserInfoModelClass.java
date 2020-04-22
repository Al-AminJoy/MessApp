package com.trustedoffer.messapp.ModelClass;

public class UserInfoModelClass {
    private String user_name,user_email,user_password,user_status,mess_key,mess_name,user_number,gender;
    public UserInfoModelClass(){

    }
    public UserInfoModelClass(String user_name, String user_email, String user_password, String user_status, String mess_key, String mess_name, String user_number,String gender) {
        this.user_name = user_name;
        this.user_email = user_email;
        this.user_password = user_password;
        this.user_status = user_status;
        this.mess_key = mess_key;
        this.mess_name = mess_name;
        this.user_number = user_number;
        this.gender = gender;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getUser_email() {
        return user_email;
    }
    public String getUser_password() {
        return user_password;
    }

    public String getUser_status() {
        return user_status;
    }

    public String getMess_key() {
        return mess_key;
    }

    public String getMess_name() {
        return mess_name;
    }

    public String getUser_number() {
        return user_number;
    }

    public String getGender() {
        return gender;
    }
}
