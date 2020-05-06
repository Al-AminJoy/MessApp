package com.trustedoffer.messapp.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.trustedoffer.messapp.Constant.SharedPref;
import com.trustedoffer.messapp.R;

public class SplashActivity extends AppCompatActivity {
    private ImageView ivSplashIcon;
    private TextView tvSplashTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        findId();
        //For Fill Notch Full Screen
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            getWindow().setAttributes(layoutParams);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        }
        //Animation In Splash Screen
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.anim_splash);
        tvSplashTxt.setAnimation(anim);
        ivSplashIcon.setAnimation(anim);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                //thread inside thread
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        dowork();
                    }
                });
                thread.start();

            }
        });
        thread.start();
    }

    private void dowork() {
        try {
            Thread.sleep(2000);
            SharedPreferences preferences = getSharedPreferences(SharedPref.AppPackage, Context.MODE_PRIVATE);
            String email = preferences.getString(SharedPref.SpEmail, "");
            String messKey = preferences.getString(SharedPref.SpMessKey, "");
            if (!email.isEmpty() && !messKey.isEmpty()) {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Intent intent = new Intent(SplashActivity.this, StartActivity.class);
                startActivity(intent);
                finish();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void findId() {
        ivSplashIcon = findViewById(R.id.ivSplashIconId);
        tvSplashTxt = findViewById(R.id.tvSplashTextId);
    }
}
