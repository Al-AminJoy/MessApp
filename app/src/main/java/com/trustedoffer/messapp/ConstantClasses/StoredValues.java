package com.trustedoffer.messapp.ConstantClasses;

import com.trustedoffer.messapp.ModelClass.MemberInfoModelClass;
import com.trustedoffer.messapp.ModelClass.UserDataModelClass;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StoredValues {
    public static  List<UserDataModelClass> messThisMonthData=new ArrayList<>();
    public static  List<UserDataModelClass> messLastMonthData=new ArrayList<>();
    public static  List<UserDataModelClass> userData=new ArrayList<>();
    public static  List<MemberInfoModelClass> memberInfo=new ArrayList<>();

    public static int day;
    public static int month;
    public static  int year;
    public static String monthName="";
    public static String prevMonth="";
    public static  double mealRate;

    public static double addDebit=0.00;
}
