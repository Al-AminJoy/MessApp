package com.trustedoffer.messapp.ModelClass;

public class OthersStatModelClass {
    private String user_name,user_email;
    int meal;
    double debit,used,mealStatus;

    public OthersStatModelClass(String user_name, String user_email, int meal, double debit, double used, double mealStatus) {
        this.user_name = user_name;
        this.user_email = user_email;
        this.meal = meal;
        this.debit = debit;
        this.used = used;
        this.mealStatus = mealStatus;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getUser_email() {
        return user_email;
    }

    public int getMeal() {
        return meal;
    }

    public double getDebit() {
        return debit;
    }

    public double getUsed() {
        return used;
    }

    public double getMealStatus() {
        return mealStatus;
    }
}
