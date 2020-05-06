package com.trustedoffer.messapp.Constant;

import com.trustedoffer.messapp.ModelClass.MemberInfoModelClass;
import com.trustedoffer.messapp.ModelClass.UserDataModelClass;

import java.util.ArrayList;
import java.util.List;

public class StoredValues {
    public static List<UserDataModelClass> messThisMonthData = new ArrayList<>();
    public static List<UserDataModelClass> messLastMonthData = new ArrayList<>();
    public static List<UserDataModelClass> userData = new ArrayList<>();
    public static List<MemberInfoModelClass> memberInfo = new ArrayList<>();

    public static int day;
    public static int month;
    public static int year;
    public static String monthName = "";
    public static String prevMonth = "";
    public static double mealRate;

}
