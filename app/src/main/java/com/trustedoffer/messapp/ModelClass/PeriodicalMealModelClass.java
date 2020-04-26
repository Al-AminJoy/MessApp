package com.trustedoffer.messapp.ModelClass;

public class PeriodicalMealModelClass {
    private String name;
    private int meal;
    String time;

    public PeriodicalMealModelClass(String name, int meal,String time) {
        this.name = name;
        this.meal = meal;
        this.time=time;
    }

    public String getName() {
        return name;
    }

    public int getMeal() {
        return meal;
    }

    public String getTime() {
        return time;
    }
}
