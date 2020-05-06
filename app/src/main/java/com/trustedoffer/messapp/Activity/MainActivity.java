package com.trustedoffer.messapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.onesignal.OneSignal;
import com.squareup.picasso.Picasso;
import com.trustedoffer.messapp.Fragment.AddMealFragment;
import com.trustedoffer.messapp.Constant.SharedPref;
import com.trustedoffer.messapp.Constant.StoredValues;
import com.trustedoffer.messapp.Fragment.DebitRequestFragment;
import com.trustedoffer.messapp.Fragment.HomeFragment;
import com.trustedoffer.messapp.Fragment.JoinReqFragment;
import com.trustedoffer.messapp.Fragment.MemberFragment;
import com.trustedoffer.messapp.ModelClass.DebitReqModelClass;
import com.trustedoffer.messapp.ModelClass.MemberInfoModelClass;
import com.trustedoffer.messapp.ModelClass.UpdateReqModelClass;
import com.trustedoffer.messapp.ModelClass.UserDataModelClass;
import com.trustedoffer.messapp.Fragment.MyRequestFragment;
import com.trustedoffer.messapp.Fragment.MyStatFragment;
import com.trustedoffer.messapp.Fragment.OtherStatDetailsDailyOverviewFragment;
import com.trustedoffer.messapp.Fragment.OtherStatFragment;
import com.trustedoffer.messapp.Fragment.PreviousMonthFragment;
import com.trustedoffer.messapp.Fragment.ProfileFragment;
import com.trustedoffer.messapp.R;
import com.trustedoffer.messapp.Fragment.UpdateReqFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private ActionBarDrawerToggle mToggle;
    private FloatingActionButton fabAdd, fabAddMeal, fabUpdate;
    private Animation animOpen, animClose, animClockwise, animAnticlockwise;
    private Boolean isOpen = false;
    private Fragment fragment;

    private double currDebit;
    private int selectedMonth;
    private int selectedyear;
    private int selectedDay;
    private int preBre, preLunch, preDinner;
    private double preDebit;
    private DocumentReference updateRef, ref, debitRef;
    private Boolean isThisMonth = false;
    private FirebaseFirestore db;
    private List<MemberInfoModelClass> listItem = new ArrayList<>();
    private List<UserDataModelClass> list = new ArrayList<>();
    List<MemberInfoModelClass> memberList = new ArrayList<>();
    private List<UserDataModelClass> listMeal = new ArrayList<>();
    private int todaysBreakfast;
    private int todaysLunch;
    private int todaysDinner;
    private double inputDebit;
    private DatabaseReference addDebitRef, addUpdateRef, addMealRef, adminLeftRef;
    private FirebaseDatabase firebaseDatabase;
    private GoogleSignInClient mGoogleSignInClient;
    private ProgressDialog progressDialog;
    private boolean isSuccess = false;
    private FirebaseAuth mAuth;
    private String pushEmail;
    private String topic;
    String URL = "https://fcm.googleapis.com/fcm/send";
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findId();
        requestQueue = Volley.newRequestQueue(this);
        //For Authentication Logout
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        //Init Firebase Database
        addDebitRef = FirebaseDatabase.getInstance().getReference("debitRequest");
        addMealRef = FirebaseDatabase.getInstance().getReference("userData");
        addUpdateRef = FirebaseDatabase.getInstance().getReference("updateRequest");
        adminLeftRef = FirebaseDatabase.getInstance().getReference("userInfo");
        firebaseDatabase = FirebaseDatabase.getInstance();
        //Init One Signal
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();
        //Send Users of This App to One Signal
        mAuth = FirebaseAuth.getInstance();
        pushEmail = mAuth.getCurrentUser().getEmail();
        OneSignal.sendTag("logged_user_email", pushEmail);
        //For Push Notification Greater Than Orio Version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("PushNotification", "PushNotification", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("adminChannelDescription");
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.enableVibration(true);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        //Setting Notification Subscription
        SharedPreferences spSubscriber = getSharedPreferences(SharedPref.AppPackage, Context.MODE_PRIVATE);
        String spMessKey = spSubscriber.getString(SharedPref.SpMessKey, "");
        if (spSubscriber.getString(SharedPref.SpStatus, "").equals("member")) {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(spMessKey + "Admin");
            FirebaseMessaging.getInstance().subscribeToTopic(spMessKey + "Member");
        } else if (spSubscriber.getString(SharedPref.SpStatus, "").equals("admin")) {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(spMessKey + "Member");
            FirebaseMessaging.getInstance().subscribeToTopic(spMessKey + "Admin");
        }
        FirebaseMessaging.getInstance().subscribeToTopic("all");
        FirebaseMessaging.getInstance().subscribeToTopic(spMessKey + "All");
        //Toolbar
        toolbar();
        //Setting Home Fragment As Default Fragment
        setDefaultFragment();

        /**
         * Floating Action Button Work
         */
        animOpen = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        animClose = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        animClockwise = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.animation_clockwise);
        animAnticlockwise = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.animation_anticlockwise);
        fabAdd.setOnClickListener(this);
        fabAddMeal.setOnClickListener(this);
        fabUpdate.setOnClickListener(this);
        /**
         */
    }

    /**
     * Sending Push Notification To Admin
     */
    private void sendNotification(String requestType, String name, String date) {
        JSONObject json = new JSONObject();
        SharedPreferences spSubscriber = getSharedPreferences(SharedPref.AppPackage, Context.MODE_PRIVATE);
        String status = spSubscriber.getString(SharedPref.SpStatus, "");
        String spMessKey = spSubscriber.getString(SharedPref.SpMessKey, "");
        try {
            if (status.equals("admin")) {
                topic = spMessKey + "All";
            } else {
                topic = spMessKey + "Admin";
            }
            json.put("to", "/topics/" + topic);
            JSONObject notificationObj = new JSONObject();
            if (requestType.equals("add")) {
                notificationObj.put("title", name + " Sent Expense Request");
            } else {
                notificationObj.put("title", name + " Sent Update Request");
            }

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

    private void setDefaultFragment() {
        checkInternet();
        fragment = new HomeFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameMainActivityId, fragment).commit();
    }

    private void toolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.whiteColor));
        mToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        mToggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.whiteColor));
        drawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        SharedPreferences spHeader = getSharedPreferences(SharedPref.AppPackage, Context.MODE_PRIVATE);
        TextView title = headerView.findViewById(R.id.tvDrawerHeaderNameId);
        CircleImageView ivUserImage = headerView.findViewById(R.id.ivDrawerImageId);
        title.setText(spHeader.getString(SharedPref.SpName, ""));
        Picasso.get().load(spHeader.getString(SharedPref.SpUserImage, "")).into(ivUserImage);
    }

    /**
     * Overflow  Option Menu Item Slection
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (isOpen == true) {
            fabAnimationStop();
        }
        if (mToggle.onOptionsItemSelected(item)) {

            return true;
        }
        switch (item.getItemId()) {
            case R.id.option_debit_req_id:
                checkInternet();
                getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.frameMainActivityId,
                        new DebitRequestFragment()).commit();
                return true;
            case R.id.option_update_req_id:
                checkInternet();
                getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.frameMainActivityId,
                        new UpdateReqFragment()).commit();
                return true;
            case R.id.option_join_req_id:
                getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.frameMainActivityId,
                        new JoinReqFragment()).commit();
                return true;
            case R.id.option_left_id:
                leftOp();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void progressOp() {
        progressDialog = new ProgressDialog(this, R.style.ProgressColor);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    private void leftOp() {
        int countAdmin = 0;
        listItem = StoredValues.memberInfo;
        for (MemberInfoModelClass memberInfo : listItem) {
            if (memberInfo.getUser_status().equals("admin")) {
                countAdmin = (countAdmin + 1);
            }
        }
        leftDialog(countAdmin);
    }

    /**
     * If Admin One Person Then Make Everyone Admin Otherwise Only He Will Left
     */
    private void leftDialog(final int countAdmin) {
        final SharedPreferences leftSp = getSharedPreferences(SharedPref.AppPackage, Context.MODE_PRIVATE);
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View view = layoutInflater.inflate(R.layout.left_dialog, null);
        MaterialButton btYes = view.findViewById(R.id.btLeftYesDialogId);
        MaterialButton btNo = view.findViewById(R.id.btLeftNoDialogId);
        TextView tvNote = view.findViewById(R.id.tvAdminLeftDialogNoteId);
        if (countAdmin > 1) {
            tvNote.setVisibility(View.GONE);
        } else {
            tvNote.setVisibility(View.VISIBLE);
        }
        final androidx.appcompat.app.AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(view).setCancelable(false).create();

        btYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressOp();
                String userKey = leftSp.getString(SharedPref.SpUserKey, "");
                final String userName = leftSp.getString(SharedPref.SpName, "");
                final String userEmail = leftSp.getString(SharedPref.SpEmail, "");
                String userNumber = leftSp.getString(SharedPref.SpNumber, "");
                String userGender = leftSp.getString(SharedPref.SpGender, "");
                String userImage = leftSp.getString(SharedPref.SpUserImage, "");
                if (countAdmin > 1) {
                    // Updating Admins Profile
                    adminLeftRef.child(userKey)
                            .setValue(new MemberInfoModelClass(userName, userEmail, "member", "", "", userNumber, userGender, userImage, userKey))
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                                    SharedPreferences.Editor editorSp = leftSp.edit();
                                    editorSp.putString(SharedPref.SpMessKey, "");
                                    editorSp.putString(SharedPref.SpStatus, "member");
                                    editorSp.putString(SharedPref.SpMessName, "");
                                    editorSp.apply();
                                    startActivity(new Intent(MainActivity.this, JoinMessActivity.class));
                                    finish();
                                    progressDialog.dismiss();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {
                    //Updation Admins Profile
                    memberList = StoredValues.memberInfo;
                    adminLeftRef.child(userKey)
                            .setValue(new MemberInfoModelClass(userName, userEmail, "member", "", "", userNumber, userGender, userImage, userKey))
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    for (MemberInfoModelClass memberInfo : memberList) {
                                        isSuccess = true;
                                        String memberUserKey = memberInfo.getKey();
                                        String memberUserName = memberInfo.getUser_name();
                                        String memberUserEmail = memberInfo.getUser_email();
                                        String memberUserMessName = memberInfo.getMess_name();
                                        String memberUserMessKey = memberInfo.getMess_key();
                                        String memberUserNumber = memberInfo.getUser_number();
                                        String memberUserGender = memberInfo.getGender();
                                        String memberUserImage = memberInfo.getUser_image_url();
                                        if (!memberUserEmail.equals(userEmail)) {
                                            //Updating Members Profile
                                            adminLeftRef.child(memberUserKey)
                                                    .setValue(new MemberInfoModelClass(memberUserName, memberUserEmail, "admin", memberUserMessName, memberUserMessKey, memberUserNumber, memberUserGender, memberUserImage, memberUserKey));
                                        }

                                    }
                                    if (isSuccess == true) {
                                        Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                                        SharedPreferences.Editor editorSp = leftSp.edit();
                                        editorSp.putString(SharedPref.SpMessKey, "");
                                        editorSp.putString(SharedPref.SpStatus, "member");
                                        editorSp.putString(SharedPref.SpMessName, "");
                                        editorSp.apply();
                                        startActivity(new Intent(MainActivity.this, JoinMessActivity.class));
                                        finish();
                                        progressDialog.dismiss();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }

                                }
                            });
                }


                alertDialog.cancel();
            }
        });
        btNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Cancelled", Toast.LENGTH_SHORT).show();
                alertDialog.cancel();
            }
        });
        alertDialog.show();
    }

    private void showUpdateDialog() {
        selectedDay = 0;
        selectedMonth = 0;
        selectedyear = 0;
        preDebit = 0;
        currDebit = 0;
        LayoutInflater layoutInflater = LayoutInflater.from(this);
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
        final androidx.appcompat.app.AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(dialogView).setCancelable(false).create();
        breakfast = getResources().getStringArray(R.array.Breakfast);
        final ArrayAdapter<String> breakfastAdapter = new ArrayAdapter<>(this, R.layout.spinner_layout, R.id.tvMealSpinnerValueId, breakfast);
        spBreakfast.setAdapter(breakfastAdapter);

        lunch = getResources().getStringArray(R.array.Lunch);
        final ArrayAdapter<String> lunchAdapter = new ArrayAdapter<>(this, R.layout.spinner_layout, R.id.tvMealSpinnerValueId, lunch);
        spLunch.setAdapter(lunchAdapter);

        dinner = getResources().getStringArray(R.array.Dinner);
        final ArrayAdapter<String> dinnerAdapter = new ArrayAdapter<>(this, R.layout.spinner_layout, R.id.tvMealSpinnerValueId, dinner);
        spDinner.setAdapter(dinnerAdapter);
        cvDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUpdateDate(tvDate, inDebit, spBreakfast, spLunch, spDinner, breakfastAdapter, lunchAdapter, dinnerAdapter);

            }
        });
        ibClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
            }
        });
        //Data Will Update If It Is For This Month Or Exist Data
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

                        if ((currDebit > 50000)) {
                            inDebit.setError("Max Expense 50000");
                        } else if (currDebit < 0) {
                            inDebit.setError("Min Expense 0");
                        } else {
                            final String currBre = spBreakfast.getSelectedItem().toString();
                            final String currLunch = spLunch.getSelectedItem().toString();
                            final String currDinner = spDinner.getSelectedItem().toString();
                            showWarning(currBre, currLunch, currDinner, currDebit, alertDialog, pbUpdate, btUpdate);
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "You Can Update Only\n" +
                                " This Month Data\n" +
                                "And Exist Data", Toast.LENGTH_LONG).show();
                    }
                } else {
                    tvDate.setTextColor(Color.RED);
                    Toast.makeText(getApplicationContext(), "Please Peak Date", Toast.LENGTH_SHORT).show();
                }
            }
        });
        alertDialog.show();
    }

    private void showWarning(final String currBre, final String currLunch, final String currDinner, final double currDebit, final AlertDialog updateDialog, final ProgressBar pbUpdate, final MaterialButton btUpdate) {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View dialogView = layoutInflater.inflate(R.layout.update_warning, null);
        ImageButton btClose = dialogView.findViewById(R.id.ibtUpdateWarnDialogCloseId);
        MaterialButton btYes = dialogView.findViewById(R.id.btUpdateWarnDialogYesId);
        final androidx.appcompat.app.AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(dialogView).setCancelable(false).create();

        btYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btUpdate.setVisibility(View.GONE);
                pbUpdate.setVisibility(View.VISIBLE);
                int updateBreakfast = Integer.parseInt(currBre);
                int updateLunch = Integer.parseInt(currLunch);
                int updateDinner = Integer.parseInt(currDinner);
                SharedPreferences preferences = getSharedPreferences(SharedPref.AppPackage, Context.MODE_PRIVATE);
                final String name = preferences.getString(SharedPref.SpName, "");
                String email = preferences.getString(SharedPref.SpEmail, "");
                String messKey = preferences.getString(SharedPref.SpMessKey, "");
                String todaysDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                final String date = (Integer.toString(selectedDay) + "-" + Integer.toString(selectedMonth) + "-" + Integer.toString(selectedyear));
                //  oneSignalNotification("update",name,date);
                String updateKey = addUpdateRef.push().getKey();
                UpdateReqModelClass update = new UpdateReqModelClass(name, email, messKey, todaysDate, "", "", updateBreakfast, updateLunch, updateDinner, selectedDay, selectedMonth, selectedyear, preBre, preLunch, preDinner, currDebit, preDebit, false, updateKey);
                addUpdateRef.child(updateKey).setValue(update)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                //Sending Push Notification
                                sendNotification("update", name, date);
                                updateDialog.cancel();
                                Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                                pbUpdate.setVisibility(View.GONE);
                                btUpdate.setVisibility(View.VISIBLE);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
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

    private void showUpdateDate(final TextView tvDate, final TextInputEditText inDebit, final Spinner spBreakfast, final Spinner spLunch, final Spinner spDinner, final ArrayAdapter<String> breakfastAdapter, final ArrayAdapter<String> lunchAdapter, final ArrayAdapter<String> dinnerAdapter) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, R.style.DialogTheme,
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

    private void addMealDialog() {
        selectedDay = 0;
        selectedMonth = 0;
        selectedyear = 0;
        inputDebit = 0;
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View view = layoutInflater.inflate(R.layout.add_meal_dialog, null);
        String[] breakfast, lunch, dinner;
        final Spinner spBreakfast = view.findViewById(R.id.spDialogBreakfastId);
        final Spinner spLunch = view.findViewById(R.id.spDialogLunchId);
        final Spinner spDinner = view.findViewById(R.id.spDialogDinnerId);
        ImageButton ibClose = view.findViewById(R.id.ibtAddMealDialogCloseId);
        final CardView cvDatePicker = view.findViewById(R.id.cvAddMealDatePickerDialogId);
        final TextView tvDateShower = view.findViewById(R.id.tvAddMealDateShowerDialogId);
        final MaterialButton btAdd = view.findViewById(R.id.btSubmitMealDialog);
        final TextInputEditText inTextDebit = view.findViewById(R.id.etAddDebitDialogId);
        final ProgressBar pbAddData = view.findViewById(R.id.pbAddDataDialogId);
        final androidx.appcompat.app.AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(view).setCancelable(false).create();
        breakfast = getResources().getStringArray(R.array.Breakfast);
        ArrayAdapter<String> breakfastAdapter = new ArrayAdapter<>(this, R.layout.spinner_layout, R.id.tvMealSpinnerValueId, breakfast);
        spBreakfast.setAdapter(breakfastAdapter);

        lunch = getResources().getStringArray(R.array.Lunch);
        ArrayAdapter<String> lunchAdapter = new ArrayAdapter<>(this, R.layout.spinner_layout, R.id.tvMealSpinnerValueId, lunch);
        spLunch.setAdapter(lunchAdapter);

        dinner = getResources().getStringArray(R.array.Dinner);
        ArrayAdapter<String> dinnerAdapter = new ArrayAdapter<>(this, R.layout.spinner_layout, R.id.tvMealSpinnerValueId, dinner);
        spDinner.setAdapter(dinnerAdapter);
        cvDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateShower(tvDateShower);
            }
        });
        btAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postData(alertDialog, spBreakfast, spLunch, spDinner, tvDateShower, inTextDebit, pbAddData, btAdd);
            }
        });
        ibClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
            }
        });
        alertDialog.show();
    }

    private void postData(final AlertDialog alertDialog, Spinner spBreakfast, Spinner spLunch, Spinner spDinner, TextView tvDateShower, TextInputEditText inTextDebit, final ProgressBar pbAddData, final MaterialButton btAdd) {
        inputDebit = 0;
        SharedPreferences shp = getSharedPreferences(SharedPref.AppPackage, Context.MODE_PRIVATE);
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
            if (exist == true || selectedMonth != StoredValues.month || selectedyear != StoredValues.year) {
                Toast.makeText(getApplicationContext(), "This Day's Data Already Exist\n" +
                        "or\n" +
                        " Other Month's Data" + selectedDay, Toast.LENGTH_LONG).show();

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
                        showDebitDialogWarning(inputDebit, spBreakfast, spLunch, spDinner, pbAddData, btAdd, alertDialog);
                    } else if (inputDebit == 0) {
                        pbAddData.setVisibility(View.VISIBLE);
                        btAdd.setVisibility(View.GONE);
                        todaysBreakfast = Integer.parseInt(spBreakfast.getSelectedItem().toString());
                        todaysLunch = Integer.parseInt(spLunch.getSelectedItem().toString());
                        todaysDinner = Integer.parseInt(spDinner.getSelectedItem().toString());
                        SharedPreferences preferences = getSharedPreferences(SharedPref.AppPackage, Context.MODE_PRIVATE);
                        String name = preferences.getString(SharedPref.SpName, "");
                        String user_email = preferences.getString(SharedPref.SpEmail, "");
                        String messKey = preferences.getString(SharedPref.SpMessKey, "");
                        String todaysDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                        String updateDate = "";
                        int month = StoredValues.month;
                        int year = StoredValues.year;
                        boolean updated = false;
                        final UserDataModelClass user = new UserDataModelClass(name, user_email, messKey, todaysDate, updateDate, todaysBreakfast, todaysLunch, todaysDinner, selectedDay, month, year, updated, inputDebit);
                        String addMealKey = addMealRef.push().getKey();
                        addMealRef.child(addMealKey).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                                listMeal.add(user);
                                StoredValues.messThisMonthData = listMeal;
                                pbAddData.setVisibility(View.GONE);
                                btAdd.setVisibility(View.VISIBLE);
                                alertDialog.cancel();
                            }
                        })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
                                        pbAddData.setVisibility(View.GONE);
                                        btAdd.setVisibility(View.VISIBLE);
                                    }
                                });
                    } else if (inputDebit < 0) {
                        inTextDebit.setError("Invalid Number");
                    }

                }
            }
        } else {
            tvDateShower.setTextColor(Color.RED);
            Toast.makeText(getApplicationContext(), "Please Peak Date", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDebitDialogWarning(final double passedDebit, final Spinner spBreakfast, final Spinner spLunch, final Spinner spDinner, final ProgressBar pbAddData, final MaterialButton btAdd, final AlertDialog dialog) {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View dialogView = layoutInflater.inflate(R.layout.debit_warning, null);
        ImageButton btClose = dialogView.findViewById(R.id.ibtDebitWarnDialogCloseId);
        MaterialButton btYes = dialogView.findViewById(R.id.btDebitWarnDialogYesId);
        final androidx.appcompat.app.AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(dialogView).setCancelable(false).create();

        btYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pbAddData.setVisibility(View.VISIBLE);
                btAdd.setVisibility(View.GONE);
                todaysBreakfast = Integer.parseInt(spBreakfast.getSelectedItem().toString());
                todaysLunch = Integer.parseInt(spLunch.getSelectedItem().toString());
                todaysDinner = Integer.parseInt(spDinner.getSelectedItem().toString());
                SharedPreferences preferences = getSharedPreferences(SharedPref.AppPackage, Context.MODE_PRIVATE);
                final String name = preferences.getString(SharedPref.SpName, "");
                String email = preferences.getString(SharedPref.SpEmail, "");
                String messKey = preferences.getString(SharedPref.SpMessKey, "");
                String todaysDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                String updateDate = "";
                int month = StoredValues.month;
                int year = StoredValues.year;
                boolean updated = false;
                final String date = (Integer.toString(selectedDay) + "-" + Integer.toString(selectedMonth) + "-" + Integer.toString(selectedyear));
                //oneSignalNotification("add",name,date);
                final UserDataModelClass user = new UserDataModelClass(name, email, messKey, todaysDate, updateDate, todaysBreakfast, todaysLunch, todaysDinner, selectedDay, month, year, updated, 0.00);
                final String debitKey = addDebitRef.push().getKey();
                final String userDataKey = addDebitRef.push().getKey();
                DebitReqModelClass debit = new DebitReqModelClass(name, email, messKey, todaysDate, "", "", selectedDay, month, year, passedDebit, false, debitKey);
                addDebitRef.child(debitKey).setValue(debit).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        addMealRef.child(userDataKey).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                                //Sending Push Notification
                                sendNotification("add", name, date);
                                listMeal.add(user);
                                StoredValues.messThisMonthData = listMeal;
                                pbAddData.setVisibility(View.GONE);
                                btAdd.setVisibility(View.VISIBLE);
                                dialog.cancel();
                            }
                        })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        pbAddData.setVisibility(View.GONE);
                                        btAdd.setVisibility(View.VISIBLE);
                                        Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
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

    private void dateShower(final TextView tvDate) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, R.style.DialogTheme,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        tvDate.setText(day + "/" + (month + 1) + "/" + year);
                        tvDate.setTextColor(Color.WHITE);
                        selectedDay = day;
                        selectedMonth = (month + 1);
                        selectedyear = year;

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
// Max = current
        long maxTime = calendar.getTimeInMillis();
        datePickerDialog.getDatePicker().setMaxDate(maxTime);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
        datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#272733"));
        datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#272733"));
        datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setBackgroundColor(Color.WHITE);
        datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setBackgroundColor(Color.WHITE);
    }

    private void findId() {
        drawerLayout = findViewById(R.id.HomeDrawerId);
        toolbar = findViewById(R.id.toolbarId);
        fabAdd = findViewById(R.id.fabHomePlusId);
        fabAddMeal = findViewById(R.id.fabHomeAddMealId);
        fabUpdate = findViewById(R.id.fabHomeUpdateId);
        navigationView = findViewById(R.id.homeNav);
    }

    public void fabAnimationStart() {
        fabAddMeal.startAnimation(animOpen);
        fabUpdate.startAnimation(animOpen);
        fabAdd.setAnimation(animClockwise);
        fabAddMeal.setClickable(true);
        fabUpdate.setClickable(true);
        isOpen = true;
    }

    public void fabAnimationStop() {
        fabAddMeal.startAnimation(animClose);
        fabUpdate.startAnimation(animClose);
        fabAdd.setAnimation(animAnticlockwise);
        fabAddMeal.setClickable(false);
        fabUpdate.setClickable(false);
        isOpen = false;
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_home_id: {
                getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.frameMainActivityId,
                        new HomeFragment()).commit();
                break;
            }
            case R.id.menu_profile_id: {

                checkInternet();
                getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.frameMainActivityId,
                        new ProfileFragment()).commit();
                break;
            }
            case R.id.menu_member_id: {
                checkInternet();
                getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.frameMainActivityId,
                        new MemberFragment()).commit();
                break;
            }
            case R.id.menu_add_meal_id: {
                checkInternet();
                getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.frameMainActivityId,
                        new AddMealFragment()).commit();
                break;
            }
            case R.id.menu_req_id: {
                checkInternet();
                getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.frameMainActivityId,
                        new MyRequestFragment()).commit();

                break;
            }

            case R.id.menu_my_status_id: {
                checkInternet();
                getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.frameMainActivityId,
                        new MyStatFragment()).commit();
                break;
            }
            case R.id.menu_others_status_id: {
                checkInternet();
                getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.frameMainActivityId,
                        new OtherStatFragment()).commit();
                break;
            }
            case R.id.menu_previous_month_id: {
                checkInternet();
                getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.frameMainActivityId,
                        new PreviousMonthFragment()).commit();
                break;
            }
            case R.id.menu_feedback: {
                feedbackDialog();
                break;
            }
            case R.id.menu_logout: {
                logoutDialog();
                break;
            }
        }
        //close navigation drawer
        drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }

    private void logoutDialog() {

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View view = layoutInflater.inflate(R.layout.logout_dialog, null);
        MaterialButton btYes = view.findViewById(R.id.btLogOutYesDialogId);
        MaterialButton btNo = view.findViewById(R.id.btLogOutNoDialogId);
        final androidx.appcompat.app.AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(view).setCancelable(false).create();

        btYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutOp();
            }
        });
        btNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
                Toast.makeText(getApplicationContext(), "Cancelled", Toast.LENGTH_SHORT).show();
            }
        });
        alertDialog.show();
    }

    private void logoutOp() {
        FirebaseAuth.getInstance().signOut();
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent intent = new Intent(MainActivity.this, StartActivity.class);
                        drawerLayout.closeDrawer(GravityCompat.START);
                        SharedPreferences sharedPreferences = getSharedPreferences(SharedPref.AppPackage, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(SharedPref.SpEmail, "");
                        editor.putString(SharedPref.SpMessKey, "");
                        editor.putString(SharedPref.SpStatus, "");
                        editor.putString(SharedPref.SpMessName, "");
                        editor.putString(SharedPref.SpGender, "");
                        editor.putString(SharedPref.SpNumber, "");
                        editor.putString(SharedPref.SpUserKey, "");
                        editor.putString(SharedPref.SpName, "");
                        editor.apply();
                        startActivity(intent);
                        finish();
                    }
                });

    }

    private void feedbackDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View view = layoutInflater.inflate(R.layout.feedback_content, null);
        ImageButton ibClose = view.findViewById(R.id.ibtFeedDialogCloseId);
        final EditText etFeedback = view.findViewById(R.id.feedback_edtxt_id);
        MaterialButton btSend = view.findViewById(R.id.send_feedback_bn_id);
        final androidx.appcompat.app.AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(view).setCancelable(false).create();

        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send(etFeedback);
            }
        });
        ibClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
            }
        });
        alertDialog.show();
    }

    /**
     * Feed Method : Send Using Email
     */
    private void send(EditText etFeedback) {
        String receiver = "alaminislam3555@gmail.com";
        String[] recevername = receiver.split(",");
        String subject = "Mess App";
        String message = etFeedback.getText().toString();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, recevername);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, message);
        intent.setType("message/rfc822");
        startActivity(Intent.createChooser(intent, "Choose App For Send"));
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (isOpen == true) {
                fabAnimationStop();
            }
            Fragment frag = getSupportFragmentManager().findFragmentById(R.id.frameMainActivityId);
            if (frag instanceof ProfileFragment) {

                checkInternet();
                getSupportFragmentManager().beginTransaction().replace(R.id.frameMainActivityId,
                        new HomeFragment(), "Home").commit();
            } else if (frag instanceof DebitRequestFragment) {
                checkInternet();
                getSupportFragmentManager().beginTransaction().replace(R.id.frameMainActivityId,
                        new HomeFragment(), "Home").commit();
            } else if (frag instanceof UpdateReqFragment) {
                checkInternet();
                getSupportFragmentManager().beginTransaction().replace(R.id.frameMainActivityId,
                        new HomeFragment(), "Home").commit();
            } else if (frag instanceof JoinReqFragment) {
                checkInternet();
                getSupportFragmentManager().beginTransaction().replace(R.id.frameMainActivityId,
                        new HomeFragment(), "Home").commit();
            } else if (frag instanceof AddMealFragment) {
                checkInternet();
                getSupportFragmentManager().beginTransaction().replace(R.id.frameMainActivityId,
                        new HomeFragment(), "Home").commit();
            } else if (frag instanceof MyRequestFragment) {
                checkInternet();
                getSupportFragmentManager().beginTransaction().replace(R.id.frameMainActivityId,
                        new HomeFragment(), "Home").commit();
            } else if (frag instanceof MemberFragment) {
                checkInternet();
                getSupportFragmentManager().beginTransaction().replace(R.id.frameMainActivityId,
                        new HomeFragment(), "Home").commit();
            } else if (frag instanceof MyStatFragment) {
                checkInternet();
                getSupportFragmentManager().beginTransaction().replace(R.id.frameMainActivityId,
                        new HomeFragment(), "Home").commit();
            } else if (frag instanceof OtherStatFragment) {
                checkInternet();
                getSupportFragmentManager().beginTransaction().replace(R.id.frameMainActivityId,
                        new HomeFragment(), "Home").commit();
            } else if (frag instanceof OtherStatDetailsDailyOverviewFragment) {
                checkInternet();
                getSupportFragmentManager().beginTransaction().replace(R.id.frameMainActivityId,
                        new OtherStatFragment(), "Other Stat").commit();
            } else if (frag instanceof PreviousMonthFragment) {
                checkInternet();
                getSupportFragmentManager().beginTransaction().replace(R.id.frameMainActivityId,
                        new HomeFragment(), "Home").commit();
            } else {
                LayoutInflater layoutInflater = LayoutInflater.from(this);
                View view = layoutInflater.inflate(R.layout.exit_layout, null);
                MaterialButton btYes = view.findViewById(R.id.btExitYesDialogId);
                MaterialButton btNo = view.findViewById(R.id.btExitNoDialogId);
                final androidx.appcompat.app.AlertDialog alertDialog = new AlertDialog.Builder(this)
                        .setView(view).setCancelable(false).create();

                btYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
                btNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.cancel();
                        Toast.makeText(getApplicationContext(), "Cancelled", Toast.LENGTH_SHORT).show();
                    }
                });
                alertDialog.show();

           /* alertDialogBuilder.setNeutralButton("Rate us", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    final String appPackageNameRating = getPackageName(); // getPackageName() from Context or Activity object
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageNameRating)));
                    } catch (ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageNameRating)));
                    }
                }
            });*/
            }
        }

    }

    private void checkInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(this);
            View dialogView = layoutInflater.inflate(R.layout.no_internet_dialog, null);
            MaterialButton btYes = dialogView.findViewById(R.id.btNoInternetDialogOkId);
            final androidx.appcompat.app.AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setView(dialogView).setCancelable(false).create();

            btYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.cancel();
                }
            });
            alertDialog.show();
        }
    }

    /**
     * For Overflow Menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        SharedPreferences preferences = getSharedPreferences(SharedPref.AppPackage, Context.MODE_PRIVATE);
        if ((preferences.getString(SharedPref.SpStatus, "")).equals("admin")) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.option_item, menu);
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fabHomePlusId:
                if (isOpen) {
                    fabAnimationStop();
                } else {
                    fabAnimationStart();
                }
                break;
            case R.id.fabHomeAddMealId:
                fabAnimationStop();
                addMealDialog();
                break;
            case R.id.fabHomeUpdateId:
                fabAnimationStop();
                showUpdateDialog();
                break;
        }
    }
}
