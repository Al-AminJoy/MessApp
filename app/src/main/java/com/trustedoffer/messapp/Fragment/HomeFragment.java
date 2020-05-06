package com.trustedoffer.messapp.Fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.trustedoffer.messapp.Activity.JoinMessActivity;
import com.trustedoffer.messapp.Adapter.PeriodMealAdapter;
import com.trustedoffer.messapp.Constant.SharedPref;
import com.trustedoffer.messapp.Constant.StoredValues;
import com.trustedoffer.messapp.ModelClass.MemberInfoModelClass;
import com.trustedoffer.messapp.ModelClass.PeriodicalMealModelClass;
import com.trustedoffer.messapp.ModelClass.UserDataModelClass;
import com.trustedoffer.messapp.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment implements View.OnClickListener {
    private String messKey, userEmail;
    private List<UserDataModelClass> listMessThisMonthData = new ArrayList<>();
    private List<UserDataModelClass> listMessLastMonthData = new ArrayList<>();
    private List<UserDataModelClass> listUserData = new ArrayList<>();
    private List<PeriodicalMealModelClass> listBreakFast = new ArrayList<>();
    private List<PeriodicalMealModelClass> listLunch = new ArrayList<>();
    private List<PeriodicalMealModelClass> listDinner = new ArrayList<>();
    private TextView tvCurrentTime, tvTotalMeal, tvMealRate, tvUserBreakfast, tvUserLunch, tvUserDinner, tvTotalBreakfast, tvTotalLunch, tvTotalDinner;
    private int day, month, year;
    private RelativeLayout rlBreakfast, rlLunch, rlDinner;
    private LinearLayout linLayHome;
    private RecyclerView rvBreakfast, rvLunch, rvDinner;
    private ImageView ivBreakfastDropdown, ivBreakfastDropUp, ivLunchDropDown, ivLunchDropUp, ivDinnerDropUp, ivDinnerDropDown;
    private PeriodMealAdapter adapter;
    private NestedScrollView scrollView;
    private ProgressDialog progressDialog;
    private double mealRate;
    private String dayName;
    private String monthName;
    private String prevMonth;
    private SwipeRefreshLayout refreshLayout;
    private DatabaseReference infoRef, dataRef;
    private FirebaseDatabase firebaseDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        findId(view);
        getActivity().setTitle("Home");
        getSharedPrefValues();
        initRecyclerView();
        //Getting date, time, month
        Calendar time = Calendar.getInstance();
        day = time.get(Calendar.DATE);
        month = (time.get(Calendar.MONTH)) + 1;
        year = time.get(Calendar.YEAR);
        monthName = (String) android.text.format.DateFormat.format("MMMM", new Date());
        findDay(time);
        findPreviousMonth(monthName);
        //setting current  date
        tvCurrentTime.setText(dayName + ", " + monthName + " " + day);
        //int database
        infoRef = FirebaseDatabase.getInstance().getReference("userInfo");
        dataRef = FirebaseDatabase.getInstance().getReference("userData");
        firebaseDatabase = FirebaseDatabase.getInstance();

        checkInternet();
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(false);
                checkInternet();
            }
        });
        rlBreakfast.setOnClickListener(this);
        rlLunch.setOnClickListener(this);
        rlDinner.setOnClickListener(this);
        setStaticValue();
        return view;
    }

    private void initRecyclerView() {
        rvBreakfast.setHasFixedSize(true);
        rvBreakfast.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvLunch.setHasFixedSize(true);
        rvLunch.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvDinner.setHasFixedSize(true);
        rvDinner.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void checkInternet() {
        progressOp();
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info == null) {
            progressDialog.dismiss();
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View dialogView = layoutInflater.inflate(R.layout.no_internet_dialog, null);
            MaterialButton btYes = dialogView.findViewById(R.id.btNoInternetDialogOkId);
            final androidx.appcompat.app.AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                    .setView(dialogView).setCancelable(false).create();

            btYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.cancel();
                    checkInternet();
                }
            });
            alertDialog.show();
        } else {
            loadMessData();
        }
    }

    private void progressOp() {
        progressDialog = new ProgressDialog(getActivity(), R.style.ProgressColor);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        //Show Dialog While ProgressDialog Showing
        progressDialog.setOnKeyListener(new Dialog.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dialog.dismiss();
                    LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                    View view = layoutInflater.inflate(R.layout.exit_layout, null);
                    MaterialButton btYes = view.findViewById(R.id.btExitYesDialogId);
                    MaterialButton btNo = view.findViewById(R.id.btExitNoDialogId);
                    final androidx.appcompat.app.AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                            .setView(view).setCancelable(false).create();

                    btYes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getActivity().finish();
                        }
                    });
                    btNo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getContext(), "Cancelled", Toast.LENGTH_SHORT).show();
                            progressDialog.show();
                            alertDialog.cancel();
                        }
                    });
                    alertDialog.show();
                }
                return true;
            }
        });
        progressDialog.show();
    }

    private void setStaticValue() {
        StoredValues.messThisMonthData = listMessThisMonthData;
        StoredValues.messLastMonthData = listMessLastMonthData;
        StoredValues.userData = listUserData;
        StoredValues.day = day;
        StoredValues.month = month;
        StoredValues.year = year;
        StoredValues.monthName = monthName;
        StoredValues.prevMonth = prevMonth;
    }

    private void findId(View view) {
        refreshLayout = view.findViewById(R.id.homeSwipRefId);
        tvCurrentTime = view.findViewById(R.id.tvCurrentTimeId);
        tvTotalMeal = view.findViewById(R.id.tvHomeTotalMealId);
        tvMealRate = view.findViewById(R.id.tvHomeMealRateId);
        tvUserBreakfast = view.findViewById(R.id.tvHomeUserBreFastId);
        tvUserLunch = view.findViewById(R.id.tvHomeUserLunchId);
        tvUserDinner = view.findViewById(R.id.tvHomeUserDinnerId);
        tvTotalBreakfast = view.findViewById(R.id.tvHomeTotalBreFastId);
        tvTotalLunch = view.findViewById(R.id.tvHomeTotalLunchId);
        tvTotalDinner = view.findViewById(R.id.tvHomeTotalDinnerId);
        rlBreakfast = view.findViewById(R.id.rlHomeBreakfastId);
        rlLunch = view.findViewById(R.id.rlHomeLunchId);
        rlDinner = view.findViewById(R.id.rlHomeDinnerId);
        rvBreakfast = view.findViewById(R.id.rvBreakfastID);
        rvLunch = view.findViewById(R.id.rvLunchID);
        rvDinner = view.findViewById(R.id.rvDinnerID);
        ivBreakfastDropdown = view.findViewById(R.id.ivHomeBreakfastDropdownId);
        ivBreakfastDropUp = view.findViewById(R.id.ivHomeBreakfastDropUpId);
        ivLunchDropDown = view.findViewById(R.id.ivHomeLunchDropdownId);
        ivLunchDropUp = view.findViewById(R.id.ivHomeLunchDropUpId);
        ivDinnerDropDown = view.findViewById(R.id.ivHomeDinnerDropdownId);
        ivDinnerDropUp = view.findViewById(R.id.ivHomeDinnerDropUpId);
        scrollView = view.findViewById(R.id.svHome);
        linLayHome = view.findViewById(R.id.linLayHome);
    }

    private void loadMessData() {
        listMessLastMonthData.clear();
        listMessThisMonthData.clear();
        listUserData.clear();
        listBreakFast.clear();
        listLunch.clear();
        listDinner.clear();
        //loading userData and storing in various list
        dataRef.orderByChild("day").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int totalMeal = 0;
                int singleDayMeal = 0;
                double userDebit = 0;
                int todaysBreakfast = 0;
                int todaysLunch = 0;
                int todaysDinner = 0;
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    UserDataModelClass data = dataSnapshot1.getValue(UserDataModelClass.class);
                    int previousMonth = (month - 1);
                    //Getting All members Data
                    if ((data.getMess_key()).equals(messKey) && ((data.getMonth()) == month || (data.getMonth()) == previousMonth) && (data.getYear()) == year) {

                        if ((data.getMonth()) == month) {
                            listMessThisMonthData.add(data);
                            int breakfast = data.getBreakfast();
                            int lunch = data.getLunch();
                            int dinner = data.getDinner();
                            double debit = data.getDebit();
                            userDebit = userDebit + debit;
                            singleDayMeal = (breakfast + lunch + dinner);
                            totalMeal = (totalMeal + singleDayMeal);
                            if (data.getDay() == day) {
                                int usersLunch = data.getLunch();
                                int usersDinner = data.getDinner();
                                int usersBreakfast = data.getBreakfast();
                                todaysBreakfast = todaysBreakfast + usersBreakfast;
                                todaysLunch = todaysLunch + usersLunch;
                                todaysDinner = todaysDinner + usersDinner;

                                String name = data.getUser_name();
                                String time = data.getDate();
                                PeriodicalMealModelClass breakfastData = new PeriodicalMealModelClass(name, usersBreakfast, time);
                                PeriodicalMealModelClass lunchData = new PeriodicalMealModelClass(name, usersLunch, time);
                                PeriodicalMealModelClass dinnerData = new PeriodicalMealModelClass(name, usersDinner, time);
                                listBreakFast.add(breakfastData);
                                listLunch.add(lunchData);
                                listDinner.add(dinnerData);
                            }
                        }
                        if ((data.getMonth()) == previousMonth) {
                            listMessLastMonthData.add(data);
                        }
                        //Getting User Data
                        if ((data.getUser_email()).equals(userEmail) && month == data.getMonth()) {
                            Log.d("Log_User_Tag", "Mess Key: " + data.getMess_key() + " " + " Email : " + userEmail);
                            if (data.getDay() == day) {
                                int lunch = data.getLunch();
                                int dinner = data.getDinner();
                                int breakfast = data.getBreakfast();
                                tvUserBreakfast.setText(Integer.toString(breakfast));
                                tvUserLunch.setText(Integer.toString(lunch));
                                tvUserDinner.setText(Integer.toString(dinner));
                                Log.d("Log_User_Data", "Breakfast : " + data.getBreakfast() + " " + " Lunch : " + data.getLunch());

                            }
                            listUserData.add(data);
                        }
                    }
                }
                if (totalMeal <= 0) {
                    mealRate = 0;
                } else {
                    mealRate = userDebit / totalMeal;
                }

                StoredValues.mealRate = mealRate;
                tvTotalMeal.setText(Integer.toString(totalMeal));
                String showMealRate = String.format(Locale.US, "%.2f", mealRate);
                tvMealRate.setText(showMealRate);
                tvTotalBreakfast.setText(Integer.toString(todaysBreakfast));
                tvTotalLunch.setText(Integer.toString(todaysLunch));
                tvTotalDinner.setText(Integer.toString(todaysDinner));
                adapter = new PeriodMealAdapter(getContext(), listBreakFast);
                rvBreakfast.setAdapter(adapter);
                adapter = new PeriodMealAdapter(getContext(), listLunch);
                rvLunch.setAdapter(adapter);
                adapter = new PeriodMealAdapter(getContext(), listDinner);
                rvDinner.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                loadMemberData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    /**
     * Storing List Of Members of Mess
     */
    private void loadMemberData() {
        final List<MemberInfoModelClass> memberList = new ArrayList<>();
        infoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean isRemoved = true;
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    MemberInfoModelClass memberData = dataSnapshot1.getValue(MemberInfoModelClass.class);
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SharedPref.AppPackage, Context.MODE_PRIVATE);
                    String key = sharedPreferences.getString(SharedPref.SpMessKey, "");
                    String storedEmail = sharedPreferences.getString(SharedPref.SpEmail, "");
                    if (key.equals(memberData.getMess_key())) {
                        if (storedEmail.equals(memberData.getUser_email())) {
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(SharedPref.SpStatus, memberData.getUser_status());
                            editor.putString(SharedPref.SpUserKey, memberData.getKey());
                            editor.apply();
                            isRemoved = false;
                        }
                        memberList.add(memberData);
                    }
                }
                //CHecking Member Removed From Mess Or Not
                if (isRemoved == true) {
                    SharedPreferences preferences = getActivity().getSharedPreferences(SharedPref.AppPackage, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(SharedPref.SpStatus, "");
                    editor.putString(SharedPref.SpMessKey, "");
                    editor.putString(SharedPref.SpMessName, "");
                    startActivity(new Intent(getActivity(), JoinMessActivity.class));
                    editor.apply();
                }
                StoredValues.memberInfo = memberList;
                progressDialog.dismiss();
                //refreshLayout.setRefreshing(false);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getSharedPrefValues() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SharedPref.AppPackage, Context.MODE_PRIVATE);
        messKey = sharedPreferences.getString(SharedPref.SpMessKey, "");
        userEmail = sharedPreferences.getString(SharedPref.SpEmail, "");
    }

    /**
     * Hide overflow menu option for members and visible for admin
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SharedPref.AppPackage, Context.MODE_PRIVATE);
        if (sharedPreferences.getString(SharedPref.SpStatus, "").equals("member")) {
            menu.clear();
        }
    }

    /**
     *
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rlHomeBreakfastId:
                if (rvBreakfast.getVisibility() == View.GONE) {
                    rvBreakfast.setVisibility(View.VISIBLE);
                    ivBreakfastDropdown.setVisibility(View.GONE);
                    ivBreakfastDropUp.setVisibility(View.VISIBLE);
                } else {
                    rvBreakfast.setVisibility(View.GONE);
                    ivBreakfastDropdown.setVisibility(View.VISIBLE);
                    ivBreakfastDropUp.setVisibility(View.GONE);
                }
                break;
            case R.id.rlHomeLunchId:
                if (rvLunch.getVisibility() == View.GONE) {
                    rvLunch.setVisibility(View.VISIBLE);
                    ivLunchDropDown.setVisibility(View.GONE);
                    ivLunchDropUp.setVisibility(View.VISIBLE);
                } else {
                    rvLunch.setVisibility(View.GONE);
                    ivLunchDropDown.setVisibility(View.VISIBLE);
                    ivLunchDropUp.setVisibility(View.GONE);
                }
                break;
            case R.id.rlHomeDinnerId:
                if (rvDinner.getVisibility() == View.GONE) {
                    rvDinner.setVisibility(View.VISIBLE);
                    ivDinnerDropDown.setVisibility(View.GONE);
                    ivDinnerDropUp.setVisibility(View.VISIBLE);
                } else {
                    rvDinner.setVisibility(View.GONE);
                    ivDinnerDropDown.setVisibility(View.VISIBLE);
                    ivDinnerDropUp.setVisibility(View.GONE);
                }
                break;
        }
    }

    private void findPreviousMonth(String monthName) {
        switch (monthName) {
            case "January":
                prevMonth = "December";
                break;
            case "February":
                prevMonth = "January";
                break;
            case "March":
                prevMonth = "February";
                break;
            case "April":
                prevMonth = "March";
                break;
            case "May":
                prevMonth = "April";
                break;
            case "June":
                prevMonth = "May";
                break;
            case "July":
                prevMonth = "June";
                break;
            case "August":
                prevMonth = "July";
                break;
            case "September":
                prevMonth = "August";
                break;
            case "October":
                prevMonth = "September";
                break;
            case "November":
                prevMonth = "October";
                break;
            case "December":
                prevMonth = "November";
                break;
            default:
        }
    }

    private void findDay(Calendar time) {
        int dayofWeek = time.get(Calendar.DAY_OF_WEEK);
        switch (dayofWeek) {

            case 1:
                dayName = "Sunday";

                break;
            case 2:
                dayName = "Monday";

                break;
            case 3:
                dayName = "Tuesday";

                break;
            case 4:
                dayName = "Wednesday";

                break;
            case 5:
                dayName = "Thursday";
                break;
            case 6:
                dayName = "Friday";
                break;
            case 7:
                dayName = "Saturday";
                break;
            default:
        }
    }
}
