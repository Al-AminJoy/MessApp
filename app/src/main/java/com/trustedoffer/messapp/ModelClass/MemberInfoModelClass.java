package com.trustedoffer.messapp.ModelClass;

public class MemberInfoModelClass {
    private String user_name,user_email,user_status,mess_name,mess_key,user_number,gender,user_image_url;
    private String key;
    public MemberInfoModelClass (){

    }
    public MemberInfoModelClass (String user_name, String user_email,String user_status,String mess_name, String mess_key, String user_number,String gender,String user_image_url,String key) {
        this.user_name = user_name;
        this.user_email = user_email;
        this.user_status = user_status;
        this.mess_name=mess_name;
        this.mess_key = mess_key;
        this.user_number = user_number;
        this.gender = gender;
        this.user_image_url=user_image_url;
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

    public String getMess_name() {
        return mess_name;
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

    public String getUser_image_url() {
        return user_image_url;
    }

    public String getKey() {
        return key;
    }
}
