package com.trustedoffer.messapp;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Calendar;

public class AddMealFragment extends Fragment {
    private TextView tvDateShower;
    private CardView cvDatePicker;
    private Spinner spBreakfast,spLunch,spDinner;
    private String[] breakfast,lunch,dinner;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_add_meal, container, false);
        findId(view);
        breakfast=getResources().getStringArray(R.array.Breakfast);
        ArrayAdapter<String> breakfastAdapter=new ArrayAdapter<>(getActivity(),R.layout.spinner_layout,R.id.tvMealSpinnerValueId,breakfast);
        spBreakfast.setAdapter(breakfastAdapter);

        lunch=getResources().getStringArray(R.array.Lunch);
        ArrayAdapter<String> lunchAdapter=new ArrayAdapter<>(getActivity(),R.layout.spinner_layout,R.id.tvMealSpinnerValueId,lunch);
        spLunch.setAdapter(lunchAdapter);

        dinner=getResources().getStringArray(R.array.Dinner);
        ArrayAdapter<String> dinnerAdapter=new ArrayAdapter<>(getActivity(),R.layout.spinner_layout,R.id.tvMealSpinnerValueId,dinner);
        spDinner.setAdapter(dinnerAdapter);
        cvDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateShower();
            }
        });
        return view;

    }
    private void dateShower() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        tvDateShower.setText(day + "/" + (month + 1) + "/" + year);
                    }
                }, year, month, dayOfMonth);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void findId(View view) {
        cvDatePicker=view.findViewById(R.id.cvAddMealDatePickerId);
        tvDateShower=view.findViewById(R.id.tvAddMealDateShower);
        spBreakfast=view.findViewById(R.id.spBreakfastId);
        spLunch=view.findViewById(R.id.spLunchId);
        spDinner=view.findViewById(R.id.spDinnerId);
    }

}
