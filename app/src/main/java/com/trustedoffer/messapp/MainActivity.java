package com.trustedoffer.messapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private ActionBarDrawerToggle mToggle;
    private FloatingActionButton fabAdd,fabAddMeal,fabAddDebit,fabAddExpense;
    private Animation animOpen,animClose,animClockwise,animAnticlockwise;
    private Boolean isOpen=false;
    Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findId();
        toolbar();
        /**
         * Setting HomeFragment as Default
         */
        fragment=new HomeFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameMainActivityId, fragment).commit();
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
        fabAddDebit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fabAnimationStop();
                addDebitDialog();
            }
        });


    }
    private void toolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToggle=new ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close);
        mToggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.whiteColor));
        drawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView.setNavigationItemSelectedListener(this);
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
        return super.onOptionsItemSelected(item);
    }
    private void addDebitDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View view = layoutInflater.inflate(R.layout.add_debit_dialogue, null);
        ImageButton ibClose=view.findViewById(R.id.ibtAddDebitDialogCloseId);
        final CardView cvDatePicker=view.findViewById(R.id.cvAddDebitDialogueDatePickerId);
        final TextView tvDebitDate=view.findViewById(R.id.tvAddDebitDialogueDateShower);
        final androidx.appcompat.app.AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(view).setCancelable(false).create();

        cvDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDate(tvDebitDate);
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

    private void addMealDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View view = layoutInflater.inflate(R.layout.add_meal_dialog, null);
        String[] breakfast,lunch,dinner;
        Spinner spBreakfast=view.findViewById(R.id.spDialogBreakfastId);
        Spinner spLunch=view.findViewById(R.id.spDialogLunchId);
        Spinner spDinner=view.findViewById(R.id.spDialogDinnerId);
        ImageButton ibClose=view.findViewById(R.id.ibtAddMealDialogCloseId);
        final CardView cvDatePicker=view.findViewById(R.id.cvAddMealDatePickerDialogId);
        final TextView tvDate=view.findViewById(R.id.tvAddMealDateShowerDialogId);
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
                showDate(tvDate);
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
    private void showDate(final TextView tvDate) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        tvDate.setText(day + "/" + (month + 1) + "/" + year);
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
        fabAddDebit=findViewById(R.id.fabHomeAddDebitId);
        navigationView=findViewById(R.id.homeNav);
    }

    public void fabAnimationStart()
    {
        fabAddMeal.startAnimation(animOpen);
        fabAddDebit.startAnimation(animOpen);
        fabAdd.setAnimation(animClockwise);
        fabAddMeal.setClickable(true);
        fabAddDebit.setClickable(true);
        isOpen=true;
    }
    public void fabAnimationStop()
    {
        fabAddMeal.startAnimation(animClose);
        fabAddDebit.startAnimation(animClose);
        fabAdd.setAnimation(animAnticlockwise);
        fabAddMeal.setClickable(false);
        fabAddDebit.setClickable(false);
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
                getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.frameMainActivityId,
                        new ProfileFragment()).commit();
                break;
            }
            case R.id.menu_member_id: {
               getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.frameMainActivityId,
                        new MemberFragment()).commit();
                break;
            }
            case R.id.menu_add_meal_id: {
                getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.frameMainActivityId,
                        new AddMealFragment()).commit();
                break;
            }
            case R.id.menu_add_debit_id: {
                getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.frameMainActivityId,
                        new AddDebitFragment()).commit();

                break;
            }
            case R.id.menu_my_status_id: {
                getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.frameMainActivityId,
                        new MyStatFragment()).commit();
                break;
            }
            case R.id.menu_others_status_id: {
                getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.frameMainActivityId,
                        new OtherStatFragment()).commit();
                break;
            }
            case R.id.menu_previous_month_id: {
                getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.frameMainActivityId,
                        new PreviousMonthFragment()).commit();
                break;
            }
            case R.id.menu_feedback: {
                getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.frameMainActivityId,
                        new FeedbackFragment()).commit();
                break;
            }
            case R.id.menu_logout: {

                break;
            }
        }
        //close navigation drawer
        drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }
    @Override
    public void onBackPressed() {
        if (isOpen==true){
            fabAnimationStop();
        }
        Fragment frag = getSupportFragmentManager().findFragmentById(R.id.frameMainActivityId);
        if(frag instanceof ProfileFragment ){
            getSupportFragmentManager().beginTransaction().replace(R.id.frameMainActivityId,
                    new HomeFragment(), "Home").commit();
        }
        else if(frag instanceof AddDebitFragment ){
            getSupportFragmentManager().beginTransaction().replace(R.id.frameMainActivityId,
                    new HomeFragment(), "Home").commit();
        }
        else if(frag instanceof AddMealFragment ){
            getSupportFragmentManager().beginTransaction().replace(R.id.frameMainActivityId,
                    new HomeFragment(), "Home").commit();
        }
        else if(frag instanceof MemberFragment){
            getSupportFragmentManager().beginTransaction().replace(R.id.frameMainActivityId,
                    new HomeFragment(), "Home").commit();
        }
        else if(frag instanceof MyStatFragment){
            getSupportFragmentManager().beginTransaction().replace(R.id.frameMainActivityId,
                    new HomeFragment(), "Home").commit();
        }
        else if(frag instanceof OtherStatFragment){
            getSupportFragmentManager().beginTransaction().replace(R.id.frameMainActivityId,
                    new HomeFragment(), "Home").commit();
        }
        else if(frag instanceof OtherStatusDetailsFragment){
            getSupportFragmentManager().beginTransaction().replace(R.id.frameMainActivityId,
                    new OtherStatFragment(), "Other Stat").commit();
        }
        else if(frag instanceof PreviousMonthFragment){
            getSupportFragmentManager().beginTransaction().replace(R.id.frameMainActivityId,
                    new HomeFragment(), "Home").commit();
        }
        else if(frag instanceof FeedbackFragment){
            getSupportFragmentManager().beginTransaction().replace(R.id.frameMainActivityId,
                    new HomeFragment(), "Home").commit();
        }

        else {
            AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("Are you sure want to exit?").setTitle("Exit !");
            alertDialogBuilder.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                }
            });
            alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alertDialogBuilder.setNeutralButton("Rate us", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    final String appPackageNameRating = getPackageName(); // getPackageName() from Context or Activity object
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageNameRating)));
                    } catch (ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageNameRating)));
                    }
                }
            });
            alertDialogBuilder.show();
        }
    }

}
