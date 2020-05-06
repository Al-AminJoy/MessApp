package com.trustedoffer.messapp.Fragment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.trustedoffer.messapp.Constant.SharedPref;
import com.trustedoffer.messapp.Constant.StoredValues;
import com.trustedoffer.messapp.ModelClass.DebitReqModelClass;
import com.trustedoffer.messapp.ModelClass.UserDataModelClass;
import com.trustedoffer.messapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddMealFragment extends Fragment implements View.OnClickListener {
    private TextView tvDateShower;
    private CardView cvDatePicker;
    private MaterialButton btAddMeal;
    private Spinner spBreakfast, spLunch, spDinner;
    private String[] breakfast, lunch, dinner;
    private TextInputEditText inTextDebit;
    private int selectedMonth, selectedYear, selectedDay;
    private List<UserDataModelClass> listMeal = new ArrayList<>();
    private int todaysBreakfast;
    private int todaysLunch;
    private int todaysDinner;
    private double inputDebit;
    private ProgressBar pbAddMeal;
    private DatabaseReference mealRef, addDebitRef;
    String URL = "https://fcm.googleapis.com/fcm/send";
    private RequestQueue requestQueue;
    private String topic;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_meal, container, false);
        findId(view);
        requestQueue = Volley.newRequestQueue(getActivity());
        getActivity().setTitle("Add Data");
        //init database
        mealRef = FirebaseDatabase.getInstance().getReference("userData");
        addDebitRef = FirebaseDatabase.getInstance().getReference("debitRequest");

        spinnerOp();

        cvDatePicker.setOnClickListener(this);
        btAddMeal.setOnClickListener(this);

        return view;
    }

    private void spinnerOp() {
        breakfast = getResources().getStringArray(R.array.Breakfast);
        ArrayAdapter<String> breakfastAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_layout, R.id.tvMealSpinnerValueId, breakfast);
        spBreakfast.setAdapter(breakfastAdapter);

        lunch = getResources().getStringArray(R.array.Lunch);
        ArrayAdapter<String> lunchAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_layout, R.id.tvMealSpinnerValueId, lunch);
        spLunch.setAdapter(lunchAdapter);

        dinner = getResources().getStringArray(R.array.Dinner);
        ArrayAdapter<String> dinnerAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_layout, R.id.tvMealSpinnerValueId, dinner);
        spDinner.setAdapter(dinnerAdapter);
    }

    /**
     * sending push notification
     */

    private void sendNotification(String name, String date) {
        JSONObject json = new JSONObject();
        SharedPreferences spSubscriber = getActivity().getSharedPreferences(SharedPref.AppPackage, Context.MODE_PRIVATE);
        String status = spSubscriber.getString(SharedPref.SpStatus, "");
        String messKey = spSubscriber.getString(SharedPref.SpMessKey, "");
        try {
            if (status.equals("admin")) {
                topic = messKey + "All";
            } else {
                topic = messKey + "Admin";
            }
            json.put("to", "/topics/" + topic);
            JSONObject notificationObj = new JSONObject();
            notificationObj.put("title", name + " Sent Expense Request");
            notificationObj.put("body", "Date : " + date);
            json.put("notification", notificationObj);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL,
                    json,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            Log.d("MUR", "onResponse: " + response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("MUR", "onError: " + error.networkResponse);
                }
            }
            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> header = new HashMap<>();
                    header.put("content-type", "application/json");
                    header.put("authorization", "key=AAAAEObyC4E:APA91bEwcQeO2dePuv64QX6bVvFn83dWZKblxRxyFP9VSvJjozx0veuyx-4tEqWYiTIjFPfdq3DxGSJ9UG7HUkTvEDP9EmdWIEj9lLa-fsl1DBTNqQB87OYQF8Lf8qrfs3WQrNtCMP9y");
                    return header;
                }
            };
            requestQueue.add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * hiding option menu
     */
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

    /**
     *
     */
    //Create Date Picker Dialog
    private void dateShower() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), R.style.DialogTheme,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        tvDateShower.setText(day + "/" + (month + 1) + "/" + year);
                        tvDateShower.setTextColor(Color.WHITE);
                        selectedDay = day;
                        selectedMonth = (month + 1);
                        selectedYear = year;

                    }
                }, year, month, dayOfMonth);
        //first day of the month
        calendar.set(Calendar.DAY_OF_MONTH, 0);
        // Set This Month
        calendar.add(Calendar.MONTH, +1);
        // last day of current month
        if (StoredValues.month == 1 || StoredValues.month == 3 || StoredValues.month == 5 || StoredValues.month == 7 || StoredValues.month == 8 || StoredValues.month == 10 || StoredValues.month == 12) {
            calendar.add(Calendar.DAY_OF_MONTH, +1);
        } else {
            calendar.add(Calendar.DAY_OF_MONTH, +0);
        }
        // Max time
        long maxTime = calendar.getTimeInMillis();
        datePickerDialog.getDatePicker().setMaxDate(maxTime);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
        datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#272733"));
        datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#272733"));
        datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setBackgroundColor(Color.WHITE);
        datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setBackgroundColor(Color.WHITE);
    }

    private void findId(View view) {
        cvDatePicker = view.findViewById(R.id.cvAddMealDatePickerId);
        tvDateShower = view.findViewById(R.id.tvAddMealDateShower);
        spBreakfast = view.findViewById(R.id.spBreakfastId);
        spLunch = view.findViewById(R.id.spLunchId);
        spDinner = view.findViewById(R.id.spDinnerId);
        btAddMeal = view.findViewById(R.id.btFragAddMealId);
        inTextDebit = view.findViewById(R.id.etAddDebitId);
        pbAddMeal = view.findViewById(R.id.pbAddDebitProgressBarId);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cvAddMealDatePickerId:
                dateShower();
                break;
            case R.id.btFragAddMealId:
                postData();
                break;
        }
    }

    private void postData() {
        inputDebit = 0;
        SharedPreferences shp = getActivity().getSharedPreferences(SharedPref.AppPackage, Context.MODE_PRIVATE);
        String email = shp.getString(SharedPref.SpEmail, "");
        boolean exist = false;
        if (selectedDay > 0) {
            listMeal = StoredValues.messThisMonthData;
            for (UserDataModelClass data : listMeal) {
                if (selectedDay == data.getDay() && email.equals(data.getUser_email())) {
                    exist = true;
                    break;
                }
            }
            if (exist == true || selectedMonth != StoredValues.month || selectedYear != StoredValues.year) {
                Toast.makeText(getContext(), "This Day's Data Already Exist\n" +
                        "or\n" +
                        " Other Month's Data", Toast.LENGTH_LONG).show();

            } else {
                try {
                    inputDebit = Double.parseDouble(inTextDebit.getText().toString().trim());
                    if (inTextDebit.length() == 0) {
                        inputDebit = 0;
                    }
                } catch (Exception e) {

                }

                if (inputDebit > 50000) {
                    inTextDebit.setError("Max Expense 50000");
                } else {

                    if (inputDebit > 0) {
                        showDebitDialogWarning(inputDebit);
                    } else if (inputDebit == 0) {
                        pbAddMeal.setVisibility(View.VISIBLE);
                        btAddMeal.setVisibility(View.GONE);
                        todaysBreakfast = Integer.parseInt(spBreakfast.getSelectedItem().toString());
                        todaysLunch = Integer.parseInt(spLunch.getSelectedItem().toString());
                        todaysDinner = Integer.parseInt(spDinner.getSelectedItem().toString());
                        SharedPreferences preferences = getActivity().getSharedPreferences(SharedPref.AppPackage, Context.MODE_PRIVATE);
                        String name = preferences.getString(SharedPref.SpName, "");
                        String user_email = preferences.getString(SharedPref.SpEmail, "");
                        String messKey = preferences.getString(SharedPref.SpMessKey, "");
                        String todaysDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                        String updateDate = "";
                        int month = StoredValues.month;
                        int year = StoredValues.year;
                        boolean updated = false;

                        final UserDataModelClass user = new UserDataModelClass(name, user_email, messKey, todaysDate, updateDate, todaysBreakfast, todaysLunch, todaysDinner, selectedDay, month, year, updated, inputDebit);
                        String addDataRefKey = mealRef.push().getKey();
                        mealRef.child(addDataRefKey).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                pbAddMeal.setVisibility(View.GONE);
                                btAddMeal.setVisibility(View.VISIBLE);
                                Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
                                listMeal.add(user);
                                StoredValues.messThisMonthData = listMeal;
                            }
                        })

                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();

                                    }
                                });
                    } else if (inputDebit < 0) {
                        inTextDebit.setError("Invalid Number");
                    }

                }
            }
        } else {
            tvDateShower.setTextColor(Color.RED);
            Toast.makeText(getContext(), "Please Peak Date", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDebitDialogWarning(final double passedDebit) {
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View dialogView = layoutInflater.inflate(R.layout.debit_warning, null);
        ImageButton btClose = dialogView.findViewById(R.id.ibtDebitWarnDialogCloseId);
        MaterialButton btYes = dialogView.findViewById(R.id.btDebitWarnDialogYesId);
        final androidx.appcompat.app.AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setView(dialogView).setCancelable(false).create();

        btYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pbAddMeal.setVisibility(View.VISIBLE);
                btAddMeal.setVisibility(View.GONE);
                todaysBreakfast = Integer.parseInt(spBreakfast.getSelectedItem().toString());
                todaysLunch = Integer.parseInt(spLunch.getSelectedItem().toString());
                todaysDinner = Integer.parseInt(spDinner.getSelectedItem().toString());
                SharedPreferences preferences = getActivity().getSharedPreferences(SharedPref.AppPackage, Context.MODE_PRIVATE);
                final String name = preferences.getString(SharedPref.SpName, "");
                String email = preferences.getString(SharedPref.SpEmail, "");
                String messKey = preferences.getString(SharedPref.SpMessKey, "");
                String todaysDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                String updateDate = "";
                int month = StoredValues.month;
                int year = StoredValues.year;
                boolean updated = false;
                final UserDataModelClass user = new UserDataModelClass(name, email, messKey, todaysDate, updateDate, todaysBreakfast, todaysLunch, todaysDinner, selectedDay, month, year, updated, 0.00);
                String debitKey = addDebitRef.push().getKey();
                DebitReqModelClass debit = new DebitReqModelClass(name, email, messKey, todaysDate, "", "", selectedDay, month, year, passedDebit, false, debitKey);
                final String addDataRefKey = mealRef.push().getKey();
                addDebitRef.child(debitKey).setValue(debit).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mealRef.child(addDataRefKey).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                String date = (Integer.toString(selectedDay) + "-" + Integer.toString(selectedMonth) + "-" + Integer.toString(selectedYear));
                                sendNotification(name, date);
                                pbAddMeal.setVisibility(View.GONE);
                                btAddMeal.setVisibility(View.VISIBLE);
                                Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
                                listMeal.add(user);
                                StoredValues.messThisMonthData = listMeal;

                            }
                        })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        pbAddMeal.setVisibility(View.GONE);
                                        btAddMeal.setVisibility(View.VISIBLE);
                                        Toast.makeText(getActivity(), "Failed", Toast.LENGTH_SHORT).show();
                                    }
                                });
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
