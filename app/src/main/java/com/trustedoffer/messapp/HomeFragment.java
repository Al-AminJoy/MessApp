package com.trustedoffer.messapp;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.type.DayOfWeek;
import com.trustedoffer.messapp.Adapter.MemberAdapter;
import com.trustedoffer.messapp.Adapter.PeriodMealAdapter;
import com.trustedoffer.messapp.ConstantClasses.SharedPref;
import com.trustedoffer.messapp.ConstantClasses.StoredValues;
import com.trustedoffer.messapp.ModelClass.MemberInfoModelClass;
import com.trustedoffer.messapp.ModelClass.MemberRetriveModelClass;
import com.trustedoffer.messapp.ModelClass.PeriodicalMealModelClass;
import com.trustedoffer.messapp.ModelClass.UserDataModelClass;
import com.trustedoffer.messapp.ModelClass.UserInfoModelClass;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {
    private String messKey, userEmail;
    private FirebaseFirestore db;
    private DocumentReference ref,memberRef;
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
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        findId(view);
        getActivity().setTitle("Home");
        getSharedPrefValues();
        rvBreakfast.setHasFixedSize(true);
        rvBreakfast.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvLunch.setHasFixedSize(true);
        rvLunch.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvDinner.setHasFixedSize(true);
        rvDinner.setLayoutManager(new LinearLayoutManager(getActivity()));
        Calendar time = Calendar.getInstance();
        day = time.get(Calendar.DATE);
        month = (time.get(Calendar.MONTH)) + 1;
        year = time.get(Calendar.YEAR);
        monthName=(String)android.text.format.DateFormat.format("MMMM", new Date());
        findDay(time);
        findPreviousMonth(monthName);
        tvCurrentTime.setText(dayName + "," + monthName + " " + day);
        db = FirebaseFirestore.getInstance();
        ref = db.document("messDatabase/userData");
        memberRef=db.document("messDatabase/userInfo");
        progressOp();
        checkInternet();
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(true);
                checkInternet();
            }
        });
        rlBreakfast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rvBreakfast.getVisibility() == View.GONE) {
                    rvBreakfast.setVisibility(View.VISIBLE);
                    ivBreakfastDropdown.setVisibility(View.GONE);
                    ivBreakfastDropUp.setVisibility(View.VISIBLE);
                } else {
                    rvBreakfast.setVisibility(View.GONE);
                    ivBreakfastDropdown.setVisibility(View.VISIBLE);
                    ivBreakfastDropUp.setVisibility(View.GONE);
                }

            }
        });
        rlLunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rvLunch.getVisibility() == View.GONE) {
                    rvLunch.setVisibility(View.VISIBLE);
                    ivLunchDropDown.setVisibility(View.GONE);
                    ivLunchDropUp.setVisibility(View.VISIBLE);
                } else {
                    rvLunch.setVisibility(View.GONE);
                    ivLunchDropDown.setVisibility(View.VISIBLE);
                    ivLunchDropUp.setVisibility(View.GONE);
                }
            }
        });
        rlDinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rvDinner.getVisibility() == View.GONE) {
                    rvDinner.setVisibility(View.VISIBLE);
                    ivDinnerDropDown.setVisibility(View.GONE);
                    ivDinnerDropUp.setVisibility(View.VISIBLE);
                } else {
                    rvDinner.setVisibility(View.GONE);
                    ivDinnerDropDown.setVisibility(View.VISIBLE);
                    ivDinnerDropUp.setVisibility(View.GONE);
                }

            }
        });
        setStaticValue();
        return view;
    }

    private void findPreviousMonth(String monthName) {
        switch (monthName) {
            case "January":
                prevMonth="December";
                break;
            case "February":
                prevMonth="January";
                break;
            case "March":
                prevMonth="February";
                break;
            case "April":
                prevMonth="March";
                break;
            case "May":
                prevMonth="April";
                break;
            case "June":
                prevMonth="May";
                break;
            case "July":
                prevMonth="June";
                break;
            case "August":
                prevMonth="July";
                break;
            case "September":
                prevMonth="August";
                break;
            case "October":
                prevMonth="September";
                break;
            case "November":
                prevMonth="October";
                break;
            case "December":
                prevMonth="November";
                break;
            default:
        }
    }

    private void findDay(Calendar time) {
        int dayofWeek=time.get(Calendar.DAY_OF_WEEK);
        switch (dayofWeek) {

            case 1:
                dayName="Sunday";

                break;
            case 2:
                dayName="Monday";

                break;
            case 3:
                dayName="Tuesday";

                break;
            case 4:
                dayName="Wednesday";

                break;
            case 5:
                dayName="Thursday";
                break;
            case 6:
                dayName="Friday";
                break;
            case 7:
                dayName="Saturday";
                break;
            default:
        }
    }

    private void progressOp() {
        progressDialog = new ProgressDialog(getActivity(), R.style.ProgressColor);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

    }
    private void checkInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info == null) {
            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
            alertDialogBuilder.setMessage("Please Turn On Internet Connection")
                    .setTitle("No Internet !")
                    .setCancelable(false)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            checkInternet();
                            refreshLayout.setRefreshing(false);
                        }
                    });
            alertDialogBuilder.show();
            //  Toast.makeText(getContext(),"No Internet",Toast.LENGTH_LONG).show();
        } else {
            loadMessData();
        }
    }

    private void setStaticValue() {
        StoredValues.messThisMonthData = listMessThisMonthData;
        StoredValues.messLastMonthData = listMessLastMonthData;
        StoredValues.userData = listUserData;
        StoredValues.day = day;
        StoredValues.month = month;
        StoredValues.year = year;
        StoredValues.monthName=monthName;
        StoredValues.prevMonth=prevMonth;
    }
    private void findId(View view) {
        refreshLayout=view.findViewById(R.id.homeSwipRefId);
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
        final List<MemberInfoModelClass> memberList=new ArrayList<>();
        Task memberTask=memberRef.collection("userInfoCollection").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            boolean isRemoved=true;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                MemberRetriveModelClass info= document.toObject(MemberRetriveModelClass.class);
                                String memberName=info.getUser_name();
                                String memberEmail=info.getUser_email();
                                String memberStatus=info.getUser_status();
                                String memberMessKey=info.getMess_key();
                                String memberNumber=info.getUser_number();
                                String member_Gender=info.getGender();
                                String memberDocumentKey=document.getId();
                                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SharedPref.AppPackage, Context.MODE_PRIVATE);
                                String key=sharedPreferences.getString(SharedPref.SpMessKey,"");
                                String storedEmail=sharedPreferences.getString(SharedPref.SpEmail,"");
                                if (key.equals(memberMessKey)){
                                    MemberInfoModelClass memberData=new MemberInfoModelClass(memberName,memberEmail,memberStatus,memberMessKey,memberNumber,member_Gender,memberDocumentKey);
                                    if (storedEmail.equals(memberEmail)){
                                        SharedPreferences preferences = getActivity().getSharedPreferences(SharedPref.AppPackage, Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor=preferences.edit();
                                        editor.putString(SharedPref.SpStatus,info.getUser_status());
                                        editor.putString(SharedPref.SpUserKey,memberDocumentKey);
                                        editor.apply();
                                        isRemoved=false;
                                    }
                                    memberList.add(memberData);
                                }


                            }
                            if (isRemoved==true){
                                SharedPreferences preferences = getActivity().getSharedPreferences(SharedPref.AppPackage, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor=preferences.edit();
                                editor.putString(SharedPref.SpStatus,"");
                                editor.putString(SharedPref.SpMessKey,"");
                                editor.putString(SharedPref.SpMessName,"");
                               // editor.putString(SharedPref.SpEmail,"");
                                //editor.putString(SharedPref.SpNumber,"");
                                startActivity(new Intent(getActivity(),JoinMessActivity.class));
                                editor.apply();
                            }
                            StoredValues.memberInfo=memberList;

                        } else {
                        }
                    }
                });
        listMessLastMonthData.clear();
        listMessThisMonthData.clear();
        listUserData.clear();
        listBreakFast.clear();
        listLunch.clear();
        listDinner.clear();
        Task loadDataTask=ref.collection("userDataCollection")
                .orderBy("day", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int totalMeal = 0;
                            int singleDayMeal = 0;
                            double userDebit = 0;
                            int todaysBreakfast = 0;
                            int todaysLunch = 0;
                            int todaysDinner = 0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                UserDataModelClass data = document.toObject(UserDataModelClass.class);
                                int previousMonth = (month - 1);
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
                                        Log.d("Log_User_Date", "Breakfast : " + data.getDate()+ " " + " Lunch : " + data.getLunch());

                                        if (data.getDay() == day) {
                                            int usersLunch = data.getLunch();
                                            int usersDinner = data.getDinner();
                                            int usersBreakfast = data.getBreakfast();
                                            todaysBreakfast = todaysBreakfast + usersBreakfast;
                                            todaysLunch = todaysLunch + usersLunch;
                                            todaysDinner = todaysDinner + usersDinner;

                                            String name = data.getUser_name();
                                            String time=data.getDate();
                                            Log.d("Log_User_Data_Loop", "Date : " + " " + " Lunch : " + data.getLunch());

                                            PeriodicalMealModelClass breakfastData = new PeriodicalMealModelClass(name, usersBreakfast,time);
                                            PeriodicalMealModelClass lunchData = new PeriodicalMealModelClass(name, usersLunch,time);
                                            PeriodicalMealModelClass dinnerData = new PeriodicalMealModelClass(name, usersDinner,time);
                                            listBreakFast.add(breakfastData);
                                            listLunch.add(lunchData);
                                            listDinner.add(dinnerData);
                                        }

                                    }
                                    if ((data.getMonth()) == previousMonth){
                                        listMessLastMonthData.add(data);
                                    }
                                    Log.d("Log_Mess_Tag", "Mess Key: " + data.getMess_key() + " Email :" + data.getUser_email());
                                    if ((data.getUser_email()).equals(userEmail) && month == data.getMonth()) {
                                        Log.d("Log_User_Tag", "Mess Key: " + data.getMess_key() + " " + " Email : " + userEmail);
                                        if (data.getDay() == day) {
                                            int lunch = data.getLunch();
                                            int dinner = data.getDinner();
                                            int breakfast = data.getBreakfast();
                                            String bre = Integer.toString(breakfast);
                                            tvUserBreakfast.setText(Integer.toString(breakfast));
                                            tvUserLunch.setText(Integer.toString(lunch));
                                            tvUserDinner.setText(Integer.toString(dinner));
                                            Log.d("Log_User_Data", "Breakfast : " + data.getBreakfast() + " " + " Lunch : " + data.getLunch());

                                        }
                                        listUserData.add(data);
                                    }
                                }
                            }
                            if (totalMeal<=0){
                                mealRate =0;
                            }
                            else {
                                mealRate = userDebit / totalMeal;
                            }

                            StoredValues.mealRate = mealRate;
                            tvTotalMeal.setText(Integer.toString(totalMeal));
                            String showMealRate = String.format(Locale.US, "%.2f", mealRate);
                            tvMealRate.setText(showMealRate);
                            if (todaysBreakfast==0){
                                tvTotalBreakfast.setText("--");
                                tvTotalLunch.setText("--");
                                tvTotalDinner.setText("--");
                            }
                            else {
                                tvTotalBreakfast.setText(Integer.toString(todaysBreakfast));
                                tvTotalLunch.setText(Integer.toString(todaysLunch));
                                tvTotalDinner.setText(Integer.toString(todaysDinner));
                            }

                      //      Log.d("Log_All_User_Data", "Breakfast : " + todaysBreakfast + " " + " Lunch : " + todaysLunch+ " Dinner : " + todaysDinner);

                            adapter = new PeriodMealAdapter(getContext(), listBreakFast);
                            rvBreakfast.setAdapter(adapter);
                            adapter = new PeriodMealAdapter(getContext(), listLunch);
                            rvLunch.setAdapter(adapter);
                            adapter = new PeriodMealAdapter(getContext(), listDinner);
                            rvDinner.setAdapter(adapter);

                        } else {
                            Toast.makeText(getContext(), "Failed To Load Data", Toast.LENGTH_LONG).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Failed To Load Data", Toast.LENGTH_LONG).show();
            }
        });
        Tasks.whenAllSuccess(memberTask,loadDataTask).addOnSuccessListener(new OnSuccessListener<List<Object>>() {
            @Override
            public void onSuccess(List<Object> objects) {
                progressDialog.dismiss();
                refreshLayout.setRefreshing(false);
            }
        });
    }

    private void getSharedPrefValues() {
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences(SharedPref.AppPackage, Context.MODE_PRIVATE);
        messKey = sharedPreferences.getString(SharedPref.SpMessKey, "");
        userEmail = sharedPreferences.getString(SharedPref.SpEmail, "");
        // Toast.makeText(getActivity(),"Email : "+userEmail,Toast.LENGTH_LONG).show();
    }


}
