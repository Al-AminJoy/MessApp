package com.trustedoffer.messapp;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.trustedoffer.messapp.Adapter.PeriodMealAdapter;
import com.trustedoffer.messapp.ConstantClasses.SharedPref;
import com.trustedoffer.messapp.ConstantClasses.StoredValues;
import com.trustedoffer.messapp.ModelClass.DebitReqModelClass;
import com.trustedoffer.messapp.ModelClass.MemberInfoModelClass;
import com.trustedoffer.messapp.ModelClass.PeriodicalMealModelClass;
import com.trustedoffer.messapp.ModelClass.UserDataModelClass;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddMealFragment extends Fragment implements View.OnClickListener {
    private TextView tvDateShower;
    private CardView cvDatePicker;
    private MaterialButton btAddMeal;
    private Spinner spBreakfast,spLunch,spDinner;
    private String[] breakfast,lunch,dinner;
    private TextInputEditText inTextDebit;
    private int selectedMonth,selectedYear,selectedDay;
    private FirebaseFirestore db;
    private DocumentReference ref,debitRef;
    private List<UserDataModelClass> listMeal=new ArrayList<>();
    private int todaysBreakfast;
    private int todaysLunch;
    private int todaysDinner;
    private double inputDebit;
    private ProgressBar pbAddMeal;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_meal, container, false);
        findId(view);
        getActivity().setTitle("Add Data");
        db = FirebaseFirestore.getInstance();
        ref = db.document("messDatabase/userData");
        debitRef = db.document("messDatabase/debitRequest");
        breakfast = getResources().getStringArray(R.array.Breakfast);
        ArrayAdapter<String> breakfastAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_layout, R.id.tvMealSpinnerValueId, breakfast);
        spBreakfast.setAdapter(breakfastAdapter);

        lunch = getResources().getStringArray(R.array.Lunch);
        ArrayAdapter<String> lunchAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_layout, R.id.tvMealSpinnerValueId, lunch);
        spLunch.setAdapter(lunchAdapter);

        dinner = getResources().getStringArray(R.array.Dinner);
        ArrayAdapter<String> dinnerAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_layout, R.id.tvMealSpinnerValueId, dinner);
        spDinner.setAdapter(dinnerAdapter);
        cvDatePicker.setOnClickListener(this);
        btAddMeal.setOnClickListener(this);

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
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
                        tvDateShower.setTextColor(Color.WHITE);
                        selectedDay=day;
                        selectedMonth=(month+1);
                        selectedYear=year;

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
        btAddMeal=view.findViewById(R.id.btFragAddMealId);
        inTextDebit=view.findViewById(R.id.etAddDebitId);
        pbAddMeal=view.findViewById(R.id.pbAddDebitProgressBarId);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.cvAddMealDatePickerId:
                dateShower();
                break;
            case R.id.btFragAddMealId:
                postData();
                break;
        }

    }

    private void postData() {
        inputDebit=0;
        selectedDay=0;
        selectedMonth=0;
        selectedYear=0;
        SharedPreferences shp=getActivity().getSharedPreferences(SharedPref.AppPackage, Context.MODE_PRIVATE);
        String email=shp.getString(SharedPref.SpEmail,"");
        boolean exist=false;
        if (selectedDay>0 ){
            listMeal=StoredValues.messThisMonthData;
            for (UserDataModelClass data:listMeal){
                if (selectedDay==data.getDay() && email.equals(data.getUser_email())){
                    exist=true;
                    break;
                }

            }
            if (exist==true || selectedMonth!=StoredValues.month ||selectedYear!=StoredValues.year){
                Toast.makeText(getContext(),"This Day's Data Already Exist\n" +
                        "or\n" +
                        " Other Month's Data",Toast.LENGTH_LONG).show();

            }
            else{
                try {
                    inputDebit=Double.parseDouble(inTextDebit.getText().toString().trim());
                    if (inTextDebit.length()==0){
                        inputDebit=0;
                    }
                }
                catch (Exception e){

                }

                if (inputDebit>50000){
                    inTextDebit.setError("Max Expense 50000");
                }
                else {

                    if (inputDebit>0){
                        showDebitDialogWarning(inputDebit);
                    }
                    else if (inputDebit==0){
                        pbAddMeal.setVisibility(View.VISIBLE);
                        btAddMeal.setVisibility(View.GONE);
                        todaysBreakfast=Integer.parseInt(spBreakfast.getSelectedItem().toString());
                        todaysLunch=Integer.parseInt(spLunch.getSelectedItem().toString());
                        todaysDinner=Integer.parseInt(spDinner.getSelectedItem().toString());
                        SharedPreferences preferences=getActivity().getSharedPreferences(SharedPref.AppPackage, Context.MODE_PRIVATE);
                        String name=preferences.getString(SharedPref.SpName,"");
                        String user_email=preferences.getString(SharedPref.SpEmail,"");
                        String messKey=preferences.getString(SharedPref.SpMessKey,"");
                        String todaysDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                        String updateDate = "";
                        int month=StoredValues.month;
                        int year=StoredValues.year;
                        boolean updated=false;

                        final UserDataModelClass user = new UserDataModelClass(name,user_email,messKey,todaysDate,updateDate,todaysBreakfast,todaysLunch,todaysDinner,selectedDay,month,year,updated,inputDebit);
                        ref.collection("userDataCollection").add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                pbAddMeal.setVisibility(View.GONE);
                                btAddMeal.setVisibility(View.VISIBLE);
                                Toast.makeText(getContext(),"Success",Toast.LENGTH_SHORT).show();
                                listMeal.add(user);
                                StoredValues.messThisMonthData=listMeal;
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(),"Failed",Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                    else if (inputDebit<0){
                        inTextDebit.setError("Invalid Number");
                    }

                }
            }
        }
        else {
            tvDateShower.setTextColor(Color.RED);
            Toast.makeText(getContext(),"Please Peak Date",Toast.LENGTH_SHORT).show();
        }
    }

    private void showDebitDialogWarning(final double passedDebit) {
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View dialogView = layoutInflater.inflate(R.layout.debit_warning, null);
        ImageButton btClose=dialogView.findViewById(R.id.ibtDebitWarnDialogCloseId);
        MaterialButton btYes = dialogView.findViewById(R.id.btDebitWarnDialogYesId);
        final androidx.appcompat.app.AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setView(dialogView).setCancelable(false).create();

        btYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pbAddMeal.setVisibility(View.VISIBLE);
                btAddMeal.setVisibility(View.GONE);
                todaysBreakfast=Integer.parseInt(spBreakfast.getSelectedItem().toString());
                todaysLunch=Integer.parseInt(spLunch.getSelectedItem().toString());
                todaysDinner=Integer.parseInt(spDinner.getSelectedItem().toString());
                SharedPreferences preferences=getActivity().getSharedPreferences(SharedPref.AppPackage, Context.MODE_PRIVATE);
                String name=preferences.getString(SharedPref.SpName,"");
                String email=preferences.getString(SharedPref.SpEmail,"");
                String messKey=preferences.getString(SharedPref.SpMessKey,"");
                String todaysDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                String updateDate = "";
                int month=StoredValues.month;
                int year=StoredValues.year;
                boolean updated=false;

                final UserDataModelClass user = new UserDataModelClass(name,email,messKey,todaysDate,updateDate,todaysBreakfast,todaysLunch,todaysDinner,selectedDay,month,year,updated,0.00);
                DebitReqModelClass debit=new DebitReqModelClass(name,email,messKey,todaysDate,"","",selectedDay,month,year,passedDebit,false);
                Task taskDebitReq=debitRef.collection("debitReqCollection").add(debit);
                Task taskUserData=ref.collection("userDataCollection").add(user);
                Tasks.whenAllSuccess(taskUserData,taskDebitReq).addOnSuccessListener(new OnSuccessListener<List<Object>>() {
                    @Override
                    public void onSuccess(List<Object> objects) {
                        pbAddMeal.setVisibility(View.GONE);
                        btAddMeal.setVisibility(View.VISIBLE);
                        Toast.makeText(getContext(),"Success",Toast.LENGTH_SHORT).show();
                        listMeal.add(user);
                        StoredValues.messThisMonthData=listMeal;

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pbAddMeal.setVisibility(View.GONE);
                        btAddMeal.setVisibility(View.VISIBLE);
                        Toast.makeText(getActivity(),"Failed",Toast.LENGTH_SHORT).show();

                    }
                });
                alertDialog.cancel();
            }
        });
        btClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });
        alertDialog.show();
    }
}
