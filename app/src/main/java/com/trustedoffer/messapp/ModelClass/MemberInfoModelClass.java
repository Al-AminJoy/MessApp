package com.trustedoffer.messapp.ModelClass;

public class MemberInfoModelClass {
    private String user_name,user_email,user_status,mess_key,user_number,gender;
    private String key;
    public MemberInfoModelClass (){

    }
    public MemberInfoModelClass (String user_name, String user_email,String user_status, String mess_key,  String user_number,String gender,String key) {
        this.user_name = user_name;
        this.user_email = user_email;
        this.user_status = user_status;
        this.mess_key = mess_key;
        this.user_number = user_number;
        this.gender = gender;
        this.key=key;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getUser_email() {
        return user_email;
    }

    public String getUser_status() {
        return user_status;
    }

    public String getMess_key() {
        return mess_key;
    }

    public String getUser_number() {
        return user_number;
    }

    public String getGender() {
        return gender;
    }

    public String getKey() {
        return key;
    }
}
