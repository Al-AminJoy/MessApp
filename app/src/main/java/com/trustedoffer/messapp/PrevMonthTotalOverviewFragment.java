package com.trustedoffer.messapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.trustedoffer.messapp.ConstantClasses.StoredValues;
import com.trustedoffer.messapp.ModelClass.UserDataModelClass;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PrevMonthTotalOverviewFragment extends Fragment {
    private TextView tvDebit,tvUsed,tvMealRate,tvTotalMeal,tvMonthName;
    private List<UserDataModelClass> list=new ArrayList<>();
    int totalMeal = 0;
    double userDebit = 0;
    private double mealRate;
    private double expense;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_prev_month_total_overview, container, false);
        findId(view);
        loadData();
        setView();
        return view;
    }

    private void setView() {
        tvMonthName.setText(StoredValues.prevMonth);
        tvDebit.setText(String.format(Locale.US, "%.2f", userDebit));
        tvTotalMeal.setText(Integer.toString(totalMeal));
        tvMealRate.setText(String.format(Locale.US, "%.2f", mealRate));
        if (userDebit==expense){
            tvUsed.setText("Balanced");
        }
        else {
            tvUsed.setText("Not Balanced");

        }
    }

    private void loadData() {
        list= StoredValues.messLastMonthData;
        int singleDayMeal;
        for (UserDataModelClass data:list){
            int breakfast = data.getBreakfast();
            int lunch = data.getLunch();
            int dinner = data.getDinner();
            double debit = data.getDebit();
            userDebit = userDebit + debit;
            singleDayMeal = (breakfast + lunch + dinner);
            totalMeal = (totalMeal + singleDayMeal);
        }
        if (totalMeal<=0){
            mealRate=0;
        }
        else {
            mealRate = userDebit / totalMeal;
        }

        expense=(mealRate*totalMeal);

    }

    private void findId(View view) {
    tvDebit=view.findViewById(R.id.tvPrevMonthTotalDebitId);
    tvMealRate=view.findViewById(R.id.tvPrevMonthMealRateId);
    tvTotalMeal=view.findViewById(R.id.tvPrevMonthTotalMealId);
    tvUsed=view.findViewById(R.id.tvPrevMonthTotalExpenseId);
    tvMonthName=view.findViewById(R.id.tvPrevMonthTotalStatMonthId);
    }
}
