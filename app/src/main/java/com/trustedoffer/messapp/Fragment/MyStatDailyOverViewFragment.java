package com.trustedoffer.messapp.Fragment;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.trustedoffer.messapp.Adapter.DailyStatAdapter;
import com.trustedoffer.messapp.Constant.SharedPref;
import com.trustedoffer.messapp.Constant.StoredValues;
import com.trustedoffer.messapp.ModelClass.UpdateReqModelClass;
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


public class MyStatDailyOverViewFragment extends Fragment implements View.OnClickListener {
    private MaterialButton btUpdateData;
    private RecyclerView recyclerView;
    private List<UserDataModelClass> dataList = new ArrayList<>();
    private List<UserDataModelClass> list = new ArrayList<>();
    private DailyStatAdapter adapter;
    private TextView tvMonth, tvNoMessage;
    private double currDebit;
    private int selectedMonth;
    private int selectedyear;
    private int selectedDay;
    private int preBre, preLunch, preDinner;
    private double preDebit;
    private Boolean isThisMonth = false;
    private ProgressDialog progressDialog;
    private DatabaseReference updateRef;
    String URL = "https://fcm.googleapis.com/fcm/send";
    private RequestQueue requestQueue;
    private String topic;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_stat_daily_over_view, container, false);
        findId(view);
        requestQueue = Volley.newRequestQueue(getActivity());
        //init database
        updateRef = FirebaseDatabase.getInstance().getReference("updateRequest");
        //show month name
        tvMonth.setText(StoredValues.monthName);

        initRecyclerView();

        loadData();
        btUpdateData.setOnClickListener(this);
        return view;
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
        if (dataList.size() == 0) {
            tvNoMessage.setVisibility(View.VISIBLE);
            tvNoMessage.setText("No Record Found");
            progressDialog.dismiss();
        } else {
            tvNoMessage.setVisibility(View.GONE);
            adapter = new DailyStatAdapter(getContext(), dataList);
            recyclerView.setAdapter(adapter);
            progressDialog.dismiss();
        }

    }

    private void initRecyclerView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void findId(View view) {
        btUpdateData = view.findViewById(R.id.btUpdateData);
        recyclerView = view.findViewById(R.id.rvMyDailyStat);
        tvMonth = view.findViewById(R.id.tvMyDailyStatMonthId);
        tvNoMessage = view.findViewById(R.id.tvMyDailyStatNoDataFoundId);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btUpdateData:
                showUpdateDialog();
        }
    }

    private void showUpdateDialog() {
        selectedDay = 0;
        selectedMonth = 0;
        selectedyear = 0;
        preDebit = 0;
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View dialogView = layoutInflater.inflate(R.layout.update_data_dialog, null);
        String[] breakfast, lunch, dinner;
        final Spinner spBreakfast = dialogView.findViewById(R.id.spUpdateDialogBreakfastId);
        final Spinner spLunch = dialogView.findViewById(R.id.spUpdateDialogLunchId);
        final Spinner spDinner = dialogView.findViewById(R.id.spUpdateDialogDinnerId);
        ImageButton ibClose = dialogView.findViewById(R.id.ibtUpdateDebitDialogCloseId);
        final TextInputEditText inDebit = dialogView.findViewById(R.id.inTextUpdateDebitId);
        final MaterialButton btUpdate = dialogView.findViewById(R.id.btUpdateMealDialog);
        final ProgressBar pbUpdate = dialogView.findViewById(R.id.pbUpdateProgressBar);
        final CardView cvDatePicker = dialogView.findViewById(R.id.cvUpdateMealDatePickerDialogId);
        final TextView tvDate = dialogView.findViewById(R.id.tvUpdateMealDateShowerDialogId);
        final androidx.appcompat.app.AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setView(dialogView).setCancelable(false).create();
        breakfast = getResources().getStringArray(R.array.Breakfast);
        final ArrayAdapter<String> breakfastAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_layout, R.id.tvMealSpinnerValueId, breakfast);
        spBreakfast.setAdapter(breakfastAdapter);

        lunch = getResources().getStringArray(R.array.Lunch);
        final ArrayAdapter<String> lunchAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_layout, R.id.tvMealSpinnerValueId, lunch);
        spLunch.setAdapter(lunchAdapter);

        dinner = getResources().getStringArray(R.array.Dinner);
        final ArrayAdapter<String> dinnerAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_layout, R.id.tvMealSpinnerValueId, dinner);
        spDinner.setAdapter(dinnerAdapter);
        cvDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDate(tvDate, inDebit, spBreakfast, spLunch, spDinner, breakfastAdapter, lunchAdapter, dinnerAdapter);

            }
        });
        ibClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
            }
        });

        btUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedDay > 0) {
                    if (isThisMonth == true) {
                        try {
                            currDebit = Double.parseDouble(inDebit.getText().toString().trim());
                            if (inDebit.length() == 0) {
                                currDebit = 0;
                            }
                        } catch (Exception e) {
                        }
                        if (currDebit > 50000 || currDebit < 0) {
                            inDebit.setError("Max Expense 50000 and Min 0");
                        } else {
                            final String currBre = spBreakfast.getSelectedItem().toString();
                            final String currLunch = spLunch.getSelectedItem().toString();
                            final String currDinner = spDinner.getSelectedItem().toString();
                            showWarning(currBre, currLunch, currDinner, currDebit, alertDialog, pbUpdate, btUpdate);
                        }
                    } else {
                        Toast.makeText(getContext(), "You Can Update Only\n" +
                                " This Month Data\n" +
                                "And Exist Data", Toast.LENGTH_LONG).show();
                    }
                } else {
                    tvDate.setTextColor(Color.RED);
                    Toast.makeText(getContext(), "Please Peak Date", Toast.LENGTH_SHORT).show();
                }


            }
        });
        alertDialog.show();
    }

    private void showWarning(final String currBre, final String currLunch, final String currDinner, final double currDebit, final AlertDialog updateDialog, final ProgressBar pbUpdate, final MaterialButton btUpdate) {
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View dialogView = layoutInflater.inflate(R.layout.update_warning, null);
        ImageButton btClose = dialogView.findViewById(R.id.ibtUpdateWarnDialogCloseId);
        MaterialButton btYes = dialogView.findViewById(R.id.btUpdateWarnDialogYesId);
        final androidx.appcompat.app.AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setView(dialogView).setCancelable(false).create();

        btYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btUpdate.setVisibility(View.GONE);
                pbUpdate.setVisibility(View.VISIBLE);
                int updateBreakfast = Integer.parseInt(currBre);
                int updateLunch = Integer.parseInt(currLunch);
                int updateDinner = Integer.parseInt(currDinner);
                SharedPreferences preferences = getActivity().getSharedPreferences(SharedPref.AppPackage, Context.MODE_PRIVATE);
                final String name = preferences.getString(SharedPref.SpName, "");
                String email = preferences.getString(SharedPref.SpEmail, "");
                String messKey = preferences.getString(SharedPref.SpMessKey, "");
                String todaysDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                String updateRefKey = updateRef.push().getKey();
                UpdateReqModelClass update = new UpdateReqModelClass(name, email, messKey, todaysDate, "", "", updateBreakfast, updateLunch, updateDinner, selectedDay, selectedMonth, selectedyear, preBre, preLunch, preDinner, currDebit, preDebit, false, updateRefKey);

                updateRef.child(updateRefKey).setValue(update)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                String date = (Integer.toString(selectedDay) + "-" + Integer.toString(selectedMonth) + "-" + Integer.toString(selectedyear));
                                sendNotification(name, date);
                                updateDialog.cancel();
                                Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
                                pbUpdate.setVisibility(View.GONE);
                                btUpdate.setVisibility(View.VISIBLE);

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                                pbUpdate.setVisibility(View.GONE);
                                btUpdate.setVisibility(View.VISIBLE);
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

    private void showDate(final TextView tvDate, final TextInputEditText inDebit, final Spinner spBreakfast, final Spinner spLunch, final Spinner spDinner, final ArrayAdapter<String> breakfastAdapter, final ArrayAdapter<String> lunchAdapter, final ArrayAdapter<String> dinnerAdapter) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), R.style.DialogTheme,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        tvDate.setTextColor(Color.WHITE);
                        selectedDay = day;
                        selectedMonth = (month + 1);
                        selectedyear = year;
                        list = StoredValues.userData;

                        for (UserDataModelClass data : list) {

                            if (selectedDay == data.getDay() && selectedMonth == StoredValues.month && selectedyear == StoredValues.year) {
                                preBre = data.getBreakfast();
                                preLunch = data.getLunch();
                                preDinner = data.getDinner();
                                preDebit = data.getDebit();
                                isThisMonth = true;
                                //setting prev debit
                                inDebit.setText(Double.toString(preDebit));
                                //setting prev breakfast
                                int breSpPos = breakfastAdapter.getPosition(Integer.toString(preBre));
                                spBreakfast.setSelection(breSpPos);
                                //setting prev lunch
                                int lunSpPos = lunchAdapter.getPosition(Integer.toString(preLunch));
                                spLunch.setSelection(lunSpPos);
                                //setting prev dinner
                                int dinnSpPos = dinnerAdapter.getPosition(Integer.toString(preDinner));
                                spDinner.setSelection(dinnSpPos);
                                break;
                            } else {
                                isThisMonth = false;
                                inDebit.setText("0");
                            }
                        }
                        tvDate.setText(day + "/" + (month + 1) + "/" + year);


                    }
                }, year, month, dayOfMonth);
        datePickerDialog.getDatePicker();
        datePickerDialog.show();
        datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#272733"));
        datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#272733"));
        datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setBackgroundColor(Color.WHITE);
        datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setBackgroundColor(Color.WHITE);
    }

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
            notificationObj.put("title", name + " Sent Update Request");
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
}
