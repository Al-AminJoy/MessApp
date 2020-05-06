package com.trustedoffer.messapp.Fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.trustedoffer.messapp.Constant.StoredValues;
import com.trustedoffer.messapp.ModelClass.UserDataModelClass;
import com.trustedoffer.messapp.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MyStatTotalOverViewFragment extends Fragment {
    private TextView tvDebit, tvTotalMeal, tvMealRate, tvUsed, tvGetBack, tvMonth;
    private List<UserDataModelClass> dataList = new ArrayList<>();
    private int totalMeal = 0;
    private double userDebit = 0;
    private double mealRate = 0;
    private double getBack;
    private double used;
    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_stat_total_over_view, container, false);
        findId(view);
        loadData();
        setView();
        return view;
    }

    private void setView() {
        tvMonth.setText(StoredValues.monthName);
        tvDebit.setText(String.format(Locale.US, "%.2f", userDebit));
        tvTotalMeal.setText(Integer.toString(totalMeal));
        tvUsed.setText(String.format(Locale.US, "%.2f", used));
        tvMealRate.setText(String.format(Locale.US, "%.2f", mealRate));
        if (getBack >= 0) {
            tvGetBack.setText("You Will Get Back " + String.format(Locale.US, "%.2f", getBack));
        } else {
            double posGetBack = (getBack * -1);
            tvGetBack.setText("You Have To Return " + String.format(Locale.US, "%.2f", posGetBack));

        }

    }

    private void progressOp() {
        progressDialog = new ProgressDialog(getActivity(), R.style.ProgressColor);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void loadData() {
        progressOp();
        dataList = StoredValues.userData;
        for (UserDataModelClass data : dataList) {
            {
                int breakfast = data.getBreakfast();
                int lunch = data.getLunch();
                int dinner = data.getDinner();
                double debit = data.getDebit();
                userDebit = (userDebit + debit);
                int singleDayMeal = (breakfast + lunch + dinner);
                totalMeal = (totalMeal + singleDayMeal);
            }
            mealRate = StoredValues.mealRate;
            used = (mealRate * totalMeal);

            getBack = (userDebit - used);

        }
        progressDialog.dismiss();
    }

    private void findId(View view) {
        tvDebit = view.findViewById(R.id.tvMyTotalStatDebitId);
        tvTotalMeal = view.findViewById(R.id.tvMyTotalStatTotalMealId);
        tvMealRate = view.findViewById(R.id.tvMyTotalStatMealRateId);
        tvUsed = view.findViewById(R.id.tvMyTotalStatUsedId);
        tvGetBack = view.findViewById(R.id.tvMyTotalStatBackId);
        tvMonth = view.findViewById(R.id.tvMyTotalStatMonthId);
    }


}
