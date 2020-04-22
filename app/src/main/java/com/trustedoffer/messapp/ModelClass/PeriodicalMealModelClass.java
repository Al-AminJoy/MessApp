package com.trustedoffer.messapp.ModelClass;

public class PeriodicalMealModelClass {
    private String name;
    private int meal;

    public PeriodicalMealModelClass(String name, int meal) {
        this.name = name;
        this.meal = meal;
    }

    public String getName() {
        return name;
    }

    public int getMeal() {
        return meal;
    }
}
