package com.trustedoffer.messapp.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.widget.FrameLayout;

import com.trustedoffer.messapp.R;
import com.trustedoffer.messapp.Fragment.StartFragment;

public class StartActivity extends AppCompatActivity {
    private FrameLayout layout;
    private Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        findId();
        intDefaultFragment();
    }

    private void intDefaultFragment() {
        fragment = new StartFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLogInActivityId, fragment).commit();
    }

    private void findId() {
        layout = findViewById(R.id.frameLogInActivityId);
    }
}
