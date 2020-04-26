package com.trustedoffer.messapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
import android.widget.FrameLayout;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.trustedoffer.messapp.ConstantClasses.SharedPref;
import com.trustedoffer.messapp.ConstantClasses.StoredValues;
import com.trustedoffer.messapp.ModelClass.DebitReqModelClass;
import com.trustedoffer.messapp.ModelClass.MemberInfoModelClass;
import com.trustedoffer.messapp.ModelClass.MemberRetriveModelClass;
import com.trustedoffer.messapp.ModelClass.UpdateReqModelClass;
import com.trustedoffer.messapp.ModelClass.UserDataModelClass;

import java.security.PrivateKey;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private ActionBarDrawerToggle mToggle;
    private FloatingActionButton fabAdd,fabAddMeal,fabUpdate;
    private Animation animOpen,animClose,animClockwise,animAnticlockwise;
    private Boolean isOpen=false;
    private Fragment fragment;

    private double currDebit;
    private int selectedMonth;
    private int selectedyear;
    private int selectedDay;
    private int preBre,preLunch,preDinner;
    private double preDebit;
    private DocumentReference updateRef,ref,debitRef;
    private Boolean isThisMonth=false;
    private FirebaseFirestore db;
    private List<MemberInfoModelClass> listItem=new ArrayList<>();
    private List<UserDataModelClass> list=new ArrayList<>();

    private List<UserDataModelClass> listMeal=new ArrayList<>();
    private int todaysBreakfast;
    private int todaysLunch;
    private int todaysDinner;
    private double inputDebit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findId();
        toolbar();
        setDefaultFragment();
        db = FirebaseFirestore.getInstance();
        updateRef = db.document("messDatabase/updateRequest");
        ref = db.document("messDatabase/userData");
        debitRef = db.document("messDatabase/debitRequest");
        /**
         * Floating Action Button Work
         */
        animOpen= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_open);
        animClose=AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);
        animClockwise=AnimationUtils.loadAnimation(getApplicationContext(),R.anim.animation_clockwise);
        animAnticlockwise=AnimationUtils.loadAnimation(getApplicationContext(),R.anim.animation_anticlockwise);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isOpen)
                {
                    fabAnimationStop();

                }
                else
                {
                    fabAnimationStart();
                }
            }
        });
        fabAddMeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fabAnimationStop();
                addMealDialog();
            }
        });
        fabUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fabAnimationStop();
                showUpdateDialog();
            }
        });


    }

    private void setDefaultFragment() {
        checkInternet();
        fragment=new HomeFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameMainActivityId, fragment).commit();
    }


    private void toolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.whiteColor));
        mToggle=new ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close);
        mToggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.whiteColor));
        drawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        SharedPreferences spHeader=getSharedPreferences(SharedPref.AppPackage,Context.MODE_PRIVATE);

        TextView title = headerView.findViewById(R.id.tvDrawerHeaderNameId);
        title.setText(spHeader.getString(SharedPref.SpName,""));
        //TextView subTitle = headerView.findViewById(R.id.nav_sub_title);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (isOpen==true){
            fabAnimationStop();
        }
        if (mToggle.onOptionsItemSelected(item))
        {

            return true;
        }
        switch (item.getItemId()){
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
    private void leftOp() {
        final SharedPreferences leftSp=getSharedPreferences(SharedPref.AppPackage,Context.MODE_PRIVATE);
        int countAdmin=0;
        listItem=StoredValues.memberInfo;
        for (MemberInfoModelClass memberInfo:listItem){
            if (memberInfo.getUser_status().equals("admin")){
                countAdmin=(countAdmin+1);
            }
        }
      leftDialog(countAdmin);
    }
    private void leftDialog(final int countAdmin) {
        final SharedPreferences leftSp=getSharedPreferences(SharedPref.AppPackage,Context.MODE_PRIVATE);
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View view = layoutInflater.inflate(R.layout.left_dialog, null);
        MaterialButton btYes = view.findViewById(R.id.btLeftYesDialogId);
        MaterialButton btNo = view.findViewById(R.id.btLeftNoDialogId);
        TextView tvNote=view.findViewById(R.id.tvAdminLeftDialogNoteId);
        if (countAdmin>1){
            tvNote.setVisibility(View.GONE);
        }
        else {
            tvNote.setVisibility(View.VISIBLE);
        }
        final androidx.appcompat.app.AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(view).setCancelable(false).create();

        btYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (countAdmin>1){
                    FirebaseFirestore.getInstance().document("messDatabase/userInfo/userInfoCollection/"+""+leftSp.getString(SharedPref.SpUserKey,"")).update("user_status","member","mess_key","","mess_name","")
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getApplicationContext(),"Success",Toast.LENGTH_SHORT).show();
                                    SharedPreferences.Editor editorSp= leftSp.edit();
                                    editorSp.putString(SharedPref.SpMessKey,"");
                                    editorSp.putString(SharedPref.SpStatus,"member");
                                    editorSp.putString(SharedPref.SpMessName,"");
                                    editorSp.apply();
                                    startActivity(new Intent(MainActivity.this,JoinMessActivity.class));
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(),"Failed",Toast.LENGTH_SHORT).show();
                                }
                            });
                }
                else {
                    Task task1=FirebaseFirestore.getInstance().document("messDatabase/userInfo/userInfoCollection/"+""+leftSp.getString(SharedPref.SpUserKey,"")).update("user_status","member","mess_key","","mess_name","");

                    Task task2=FirebaseFirestore.getInstance().collection("messDatabase").document("userInfo").collection("userInfoCollection")
                            .whereEqualTo("mess_key",leftSp.getString(SharedPref.SpMessKey,""))
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            MemberRetriveModelClass info= document.toObject(MemberRetriveModelClass.class);
                                            String leftUserEmail=leftSp.getString(SharedPref.SpEmail,"");
                                            if (!info.getUser_email().equals(leftUserEmail)){
                                                FirebaseFirestore.getInstance().document("messDatabase/userInfo/userInfoCollection/"+""+document.getId()).update("user_status","admin");
                                            }

                                        }
                                    }


                                }
                            });
                    Tasks.whenAllSuccess(task1,task2).addOnSuccessListener(new OnSuccessListener<List<Object>>() {
                        @Override
                        public void onSuccess(List<Object> objects) {
                            Toast.makeText(getApplicationContext(),"Success",Toast.LENGTH_SHORT).show();
                            SharedPreferences.Editor editorSp= leftSp.edit();
                            editorSp.putString(SharedPref.SpMessKey,"");
                            editorSp.putString(SharedPref.SpStatus,"member");
                            editorSp.putString(SharedPref.SpMessName,"");
                            editorSp.apply();
                            startActivity(new Intent(MainActivity.this,JoinMessActivity.class));
                            finish();
                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("LOG","Error:"+e);
                                    Toast.makeText(getApplicationContext(),"Failed",Toast.LENGTH_SHORT).show();
                                }
                            });
                }


                        alertDialog.cancel();
            }
        });
        btNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Cancelled",Toast.LENGTH_SHORT).show();
                alertDialog.cancel();
            }
        });
        alertDialog.show();
    }
    private void showUpdateDialog() {
        selectedDay=0;
        selectedMonth=0;
        selectedyear=0;
        preDebit=0;
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View dialogView = layoutInflater.inflate(R.layout.update_data_dialog, null);
        String[] breakfast,lunch,dinner;
        final Spinner spBreakfast=dialogView.findViewById(R.id.spUpdateDialogBreakfastId);
        final Spinner spLunch=dialogView.findViewById(R.id.spUpdateDialogLunchId);
        final Spinner spDinner=dialogView.findViewById(R.id.spUpdateDialogDinnerId);
        ImageButton ibClose=dialogView.findViewById(R.id.ibtUpdateDebitDialogCloseId);
        final TextInputEditText inDebit=dialogView.findViewById(R.id.inTextUpdateDebitId);
        final MaterialButton btUpdate=dialogView.findViewById(R.id.btUpdateMealDialog);
        final ProgressBar pbUpdate=dialogView.findViewById(R.id.pbUpdateProgressBar);
        final CardView cvDatePicker=dialogView.findViewById(R.id.cvUpdateMealDatePickerDialogId);
        final TextView tvDate=dialogView.findViewById(R.id.tvUpdateMealDateShowerDialogId);
        final androidx.appcompat.app.AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(dialogView).setCancelable(false).create();
        breakfast=getResources().getStringArray(R.array.Breakfast);
        ArrayAdapter<String> breakfastAdapter=new ArrayAdapter<>(this,R.layout.spinner_layout,R.id.tvMealSpinnerValueId,breakfast);
        spBreakfast.setAdapter(breakfastAdapter);

        lunch=getResources().getStringArray(R.array.Lunch);
        ArrayAdapter<String> lunchAdapter=new ArrayAdapter<>(this,R.layout.spinner_layout,R.id.tvMealSpinnerValueId,lunch);
        spLunch.setAdapter(lunchAdapter);

        dinner=getResources().getStringArray(R.array.Dinner);
        ArrayAdapter<String> dinnerAdapter=new ArrayAdapter<>(this,R.layout.spinner_layout,R.id.tvMealSpinnerValueId,dinner);
        spDinner.setAdapter(dinnerAdapter);
        cvDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUpdateDate(tvDate,inDebit);

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
                if (selectedDay>0){
                    if (isThisMonth==true){
                        try {
                            currDebit=Double.parseDouble(inDebit.getText().toString().trim());
                            if (inDebit.length()==0){
                                currDebit=0;
                            }
                        }
                        catch (Exception e){
                        }
                        if (currDebit>50000 || currDebit<0){
                            inDebit.setError("Max Expense 50000 and Min 0" );
                        }
                        else {
                            final String currBre=spBreakfast.getSelectedItem().toString();
                            final String currLunch=spLunch.getSelectedItem().toString();
                            final String currDinner=spDinner.getSelectedItem().toString();
                            showWarning(currBre,currLunch,currDinner,currDebit,alertDialog,pbUpdate,btUpdate);
                        }
                    }
                    else {
                        Toast.makeText(getApplicationContext(),"You Can Update Only\n" +
                                " This Month Data\n" +
                                "And Exist Data",Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    tvDate.setTextColor(Color.RED);
                    Toast.makeText(getApplicationContext(),"Please Peak Date",Toast.LENGTH_SHORT).show();
                }
            }
        });
        alertDialog.show();
    }
    private void showWarning(final String currBre, final String currLunch, final String currDinner, final double currDebit, final AlertDialog updateDialog, final ProgressBar pbUpdate, final MaterialButton btUpdate) {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View dialogView = layoutInflater.inflate(R.layout.update_warning, null);
        ImageButton btClose=dialogView.findViewById(R.id.ibtUpdateWarnDialogCloseId);
        MaterialButton btYes = dialogView.findViewById(R.id.btUpdateWarnDialogYesId);
        final androidx.appcompat.app.AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(dialogView).setCancelable(false).create();

        btYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btUpdate.setVisibility(View.GONE);
                pbUpdate.setVisibility(View.VISIBLE);
                int updateBreakfast=Integer.parseInt(currBre);
                int updateLunch=Integer.parseInt(currLunch);
                int updateDinner=Integer.parseInt(currDinner);
                SharedPreferences preferences=getSharedPreferences(SharedPref.AppPackage, Context.MODE_PRIVATE);
                String name=preferences.getString(SharedPref.SpName,"");
                String email=preferences.getString(SharedPref.SpEmail,"");
                String messKey=preferences.getString(SharedPref.SpMessKey,"");
                String todaysDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                UpdateReqModelClass update=new UpdateReqModelClass(name,email,messKey,todaysDate,"","",updateBreakfast,updateLunch,updateDinner,selectedDay,selectedMonth,selectedyear,preBre,preLunch,preDinner,currDebit,preDebit,false);

                updateRef.collection("updateReqCollection").add(update)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                updateDialog.cancel();
                                Toast.makeText(getApplicationContext(),"Success",Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(),"Failed",Toast.LENGTH_SHORT).show();
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

    private void showUpdateDate(final TextView tvDate, final TextInputEditText inDebit) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        tvDate.setTextColor(Color.WHITE);
                        selectedDay=day;
                        selectedMonth=(month+1);
                        selectedyear=year;
                        list=StoredValues.userData;
                        for (UserDataModelClass data:list){

                            if (selectedDay==data.getDay() && selectedMonth==StoredValues.month && selectedyear==StoredValues.year){
                                preBre=data.getBreakfast();
                                preLunch=data.getLunch();
                                preDinner=data.getDinner();
                                preDebit=data.getDebit();
                                isThisMonth=true;
                                inDebit.setText(Double.toString(preDebit));
                                Toast.makeText(getApplicationContext(),"Pre Debit : "+preDebit+" "+preBre+" "+preLunch,Toast.LENGTH_LONG).show();
                                break;
                            }
                            else {
                                isThisMonth=false;
                                inDebit.setText("0");
                            }
                        }
                        tvDate.setText(day + "/" + (month + 1) + "/" + year);


                    }
                }, year, month, dayOfMonth);
        datePickerDialog.getDatePicker();
        datePickerDialog.show();
    }

    private void addMealDialog() {
        selectedDay=0;
        selectedMonth=0;
        selectedyear=0;
        inputDebit=0;
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View view = layoutInflater.inflate(R.layout.add_meal_dialog, null);
        String[] breakfast,lunch,dinner;
        final Spinner spBreakfast=view.findViewById(R.id.spDialogBreakfastId);
        final Spinner spLunch=view.findViewById(R.id.spDialogLunchId);
        final Spinner spDinner=view.findViewById(R.id.spDialogDinnerId);
        ImageButton ibClose=view.findViewById(R.id.ibtAddMealDialogCloseId);
        final CardView cvDatePicker=view.findViewById(R.id.cvAddMealDatePickerDialogId);
        final TextView tvDateShower=view.findViewById(R.id.tvAddMealDateShowerDialogId);
        final MaterialButton btAdd=view.findViewById(R.id.btSubmitMealDialog);
        final TextInputEditText inTextDebit=view.findViewById(R.id.etAddDebitDialogId);
        final ProgressBar pbAddData=view.findViewById(R.id.pbAddDataDialogId);
        final androidx.appcompat.app.AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(view).setCancelable(false).create();
        breakfast=getResources().getStringArray(R.array.Breakfast);
        ArrayAdapter<String> breakfastAdapter=new ArrayAdapter<>(this,R.layout.spinner_layout,R.id.tvMealSpinnerValueId,breakfast);
        spBreakfast.setAdapter(breakfastAdapter);

        lunch=getResources().getStringArray(R.array.Lunch);
        ArrayAdapter<String> lunchAdapter=new ArrayAdapter<>(this,R.layout.spinner_layout,R.id.tvMealSpinnerValueId,lunch);
        spLunch.setAdapter(lunchAdapter);

        dinner=getResources().getStringArray(R.array.Dinner);
        ArrayAdapter<String> dinnerAdapter=new ArrayAdapter<>(this,R.layout.spinner_layout,R.id.tvMealSpinnerValueId,dinner);
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
              postData(alertDialog,spBreakfast,spLunch,spDinner,tvDateShower,inTextDebit,pbAddData,btAdd);
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
        inputDebit=0;
        SharedPreferences shp=getSharedPreferences(SharedPref.AppPackage, Context.MODE_PRIVATE);
        String email=shp.getString(SharedPref.SpEmail,"");
        boolean exist=false;
        Toast.makeText(getApplicationContext(),"Date"+selectedDay,Toast.LENGTH_SHORT).show();
        if (selectedDay>0 ){
            listMeal=StoredValues.messThisMonthData;
            for (UserDataModelClass data:listMeal){
                if (selectedDay==data.getDay() && email.equals(data.getUser_email())){
                    exist=true;
                    break;
                }

            }
            if (exist==true || selectedMonth!=StoredValues.month ||selectedyear!=StoredValues.year){
                Toast.makeText(getApplicationContext(),"This Day's Data Already Exist\n" +
                        "or\n" +
                        " Other Month's Data"+selectedDay,Toast.LENGTH_LONG).show();
              //  Toast.makeText(getApplicationContext(),"Date"+selectedDay,Toast.LENGTH_SHORT).show();

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
                        showDebitDialogWarning(inputDebit,spBreakfast,spLunch,spDinner,pbAddData,btAdd,alertDialog);
                    }
                    else if (inputDebit==0){
                        pbAddData.setVisibility(View.VISIBLE);
                        btAdd.setVisibility(View.GONE);
                        todaysBreakfast=Integer.parseInt(spBreakfast.getSelectedItem().toString());
                        todaysLunch=Integer.parseInt(spLunch.getSelectedItem().toString());
                        todaysDinner=Integer.parseInt(spDinner.getSelectedItem().toString());
                        SharedPreferences preferences=getSharedPreferences(SharedPref.AppPackage, Context.MODE_PRIVATE);
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
                                Toast.makeText(getApplicationContext(),"Success",Toast.LENGTH_SHORT).show();
                                listMeal.add(user);
                                StoredValues.messThisMonthData=listMeal;
                                pbAddData.setVisibility(View.GONE);
                                btAdd.setVisibility(View.VISIBLE);
                                alertDialog.cancel();


                            }
                        })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(),"Failed",Toast.LENGTH_SHORT).show();
                                        pbAddData.setVisibility(View.GONE);
                                        btAdd.setVisibility(View.VISIBLE);
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
            Toast.makeText(getApplicationContext(),"Please Peak Date",Toast.LENGTH_SHORT).show();
        }
    }

    private void showDebitDialogWarning(final double passedDebit, final Spinner spBreakfast, final Spinner spLunch, final Spinner spDinner, final ProgressBar pbAddData, final MaterialButton btAdd, final AlertDialog dialog) {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View dialogView = layoutInflater.inflate(R.layout.debit_warning, null);
        ImageButton btClose=dialogView.findViewById(R.id.ibtDebitWarnDialogCloseId);
        MaterialButton btYes = dialogView.findViewById(R.id.btDebitWarnDialogYesId);
        final androidx.appcompat.app.AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(dialogView).setCancelable(false).create();

        btYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pbAddData.setVisibility(View.VISIBLE);
                btAdd.setVisibility(View.GONE);
                todaysBreakfast=Integer.parseInt(spBreakfast.getSelectedItem().toString());
                todaysLunch=Integer.parseInt(spLunch.getSelectedItem().toString());
                todaysDinner=Integer.parseInt(spDinner.getSelectedItem().toString());
                SharedPreferences preferences=getSharedPreferences(SharedPref.AppPackage, Context.MODE_PRIVATE);
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
                        Toast.makeText(getApplicationContext(),"Success",Toast.LENGTH_SHORT).show();
                        listMeal.add(user);
                        StoredValues.messThisMonthData=listMeal;
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
                                Toast.makeText(getApplicationContext(),"Failed",Toast.LENGTH_SHORT).show();

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
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        tvDate.setText(day + "/" + (month + 1) + "/" + year);
                        tvDate.setTextColor(Color.WHITE);
                        selectedDay=day;
                        selectedMonth=(month+1);
                        selectedyear=year;

                    }
                }, year, month, dayOfMonth);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }
    private void findId() {
        drawerLayout = findViewById(R.id.HomeDrawerId);
        toolbar=findViewById(R.id.toolbarId);
        fabAdd=findViewById(R.id.fabHomePlusId);
        fabAddMeal=findViewById(R.id.fabHomeAddMealId);
        fabUpdate=findViewById(R.id.fabHomeUpdateId);
        navigationView=findViewById(R.id.homeNav);
    }

    public void fabAnimationStart()
    {
        fabAddMeal.startAnimation(animOpen);
        fabUpdate.startAnimation(animOpen);
        fabAdd.setAnimation(animClockwise);
        fabAddMeal.setClickable(true);
        fabUpdate.setClickable(true);
        isOpen=true;
    }
    public void fabAnimationStop()
    {
        fabAddMeal.startAnimation(animClose);
        fabUpdate.startAnimation(animClose);
        fabAdd.setAnimation(animAnticlockwise);
        fabAddMeal.setClickable(false);
        fabUpdate.setClickable(false);
        isOpen=false;
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
         /*   case R.id.menu_update_request_id: {
                checkInternet();
                getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.frameMainActivityId,
                        new UpdateReqFragment()).commit();

                break;
            }*/
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
                Toast.makeText(getApplicationContext(),"Cancelled",Toast.LENGTH_SHORT).show();
            }
        });
        alertDialog.show();
    }

    private void logoutOp() {
        Intent intent=new Intent(MainActivity.this,LogInActivity.class);
        drawerLayout.closeDrawer(GravityCompat.START);
        SharedPreferences sharedPreferences=getSharedPreferences(SharedPref.AppPackage,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor= sharedPreferences.edit();
        editor.putString(SharedPref.SpEmail,"");
        editor.putString(SharedPref.SpMessKey,"");
        editor.putString(SharedPref.SpStatus,"");
        editor.putString(SharedPref.SpMessName,"");
        editor.putString(SharedPref.SpGender,"");
        editor.putString(SharedPref.SpNumber,"");
        editor.putString(SharedPref.SpUserKey,"");
        editor.putString(SharedPref.SpName,"");
        editor.apply();
        startActivity(intent);
        finish();
    }

    private void feedbackDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View view = layoutInflater.inflate(R.layout.feedback_content, null);
        ImageButton ibClose=view.findViewById(R.id.ibtFeedDialogCloseId);
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
     * @param etFeedback
     */
    private void send(EditText etFeedback) {
        String receiver="alaminislam3555@gmail.com";
        String[] recevername=receiver.split(",");
        String subject="Mess App";
        String message=etFeedback.getText().toString();
        Intent intent=new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL,recevername);
        intent.putExtra(Intent.EXTRA_SUBJECT,subject);
        intent.putExtra(Intent.EXTRA_TEXT,message);
        intent.setType("message/rfc822");
        startActivity(Intent.createChooser(intent,"Choose App For Send"));
    }
    @Override
    public void onBackPressed() {
        if (isOpen==true){
            fabAnimationStop();
        }
        Fragment frag = getSupportFragmentManager().findFragmentById(R.id.frameMainActivityId);
        if(frag instanceof ProfileFragment ){

            checkInternet();
            getSupportFragmentManager().beginTransaction().replace(R.id.frameMainActivityId,
                    new HomeFragment(), "Home").commit();
        }
        else if(frag instanceof DebitRequestFragment ){
            checkInternet();
            getSupportFragmentManager().beginTransaction().replace(R.id.frameMainActivityId,
                    new HomeFragment(), "Home").commit();
        }
        else if(frag instanceof UpdateReqFragment ){
            checkInternet();
            getSupportFragmentManager().beginTransaction().replace(R.id.frameMainActivityId,
                    new HomeFragment(), "Home").commit();
        }
        else if(frag instanceof JoinReqFragment ){
            checkInternet();
            getSupportFragmentManager().beginTransaction().replace(R.id.frameMainActivityId,
                    new HomeFragment(), "Home").commit();
        }
        else if(frag instanceof AddMealFragment ){
            checkInternet();
            getSupportFragmentManager().beginTransaction().replace(R.id.frameMainActivityId,
                    new HomeFragment(), "Home").commit();
        }
        else if(frag instanceof MyRequestFragment ){
            checkInternet();
            getSupportFragmentManager().beginTransaction().replace(R.id.frameMainActivityId,
                    new HomeFragment(), "Home").commit();
        }
        else if(frag instanceof MemberFragment){
            checkInternet();
            getSupportFragmentManager().beginTransaction().replace(R.id.frameMainActivityId,
                    new HomeFragment(), "Home").commit();
        }
        else if(frag instanceof MyStatFragment){
            checkInternet();
            getSupportFragmentManager().beginTransaction().replace(R.id.frameMainActivityId,
                    new HomeFragment(), "Home").commit();
        }
        else if(frag instanceof OtherStatFragment){
            checkInternet();
            getSupportFragmentManager().beginTransaction().replace(R.id.frameMainActivityId,
                    new HomeFragment(), "Home").commit();
        }
        else if(frag instanceof OtherStatDetailsDailyOverviewFragment){
            checkInternet();
            getSupportFragmentManager().beginTransaction().replace(R.id.frameMainActivityId,
                    new OtherStatFragment(), "Other Stat").commit();
        }
        else if(frag instanceof PreviousMonthFragment){
            checkInternet();
            getSupportFragmentManager().beginTransaction().replace(R.id.frameMainActivityId,
                    new HomeFragment(), "Home").commit();
        }

        else {
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
                    Toast.makeText(getApplicationContext(),"Cancelled",Toast.LENGTH_SHORT).show();
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
    private void checkInternet() {
        ConnectivityManager connectivityManager=(ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info=connectivityManager.getActiveNetworkInfo();
        if (info==null)
        {
            final AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("No Internet Connection")
                    .setCancelable(false)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
            alertDialogBuilder.show();
            //  Toast.makeText(getContext(),"No Internet",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        SharedPreferences preferences=getSharedPreferences(SharedPref.AppPackage,Context.MODE_PRIVATE);
            if ((preferences.getString(SharedPref.SpStatus,"")).equals("admin") ){
                MenuInflater inflater=getMenuInflater();
                inflater.inflate(R.menu.option_item,menu);
            }
        return super.onCreateOptionsMenu(menu);
    }

}
