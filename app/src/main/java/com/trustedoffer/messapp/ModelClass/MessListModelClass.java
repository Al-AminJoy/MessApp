package com.trustedoffer.messapp.ModelClass;

public class MessListModelClass {
    private String mess_name,mess_key,create_time;

    public MessListModelClass() {
    }

    public MessListModelClass(String mess_name, String mess_key, String create_time) {
        this.mess_name = mess_name;
        this.mess_key = mess_key;
        this.create_time = create_time;
    }

    public String getMess_name() {
        return mess_name;
    }

    public String getMess_key() {
        return mess_key;
    }

    public String getCreate_time() {
        return create_time;
    }
}
