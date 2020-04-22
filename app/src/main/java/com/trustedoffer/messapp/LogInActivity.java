package com.trustedoffer.messapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.BoringLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.protobuf.Empty;
import com.trustedoffer.messapp.ConstantClasses.SharedPref;
import com.trustedoffer.messapp.ConstantClasses.StoredValues;
import com.trustedoffer.messapp.ModelClass.UserInfoModelClass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LogInActivity extends AppCompatActivity {
    private ImageButton ibtLogIn;
    private MaterialButton btRegister;
    private EditText etEmail,etPassword;
    private FirebaseFirestore db;
    private DocumentReference ref;
    private List<UserInfoModelClass> list=new ArrayList<>();
    private ProgressBar progressBar;
    private RelativeLayout rlLogInContent;
    private TextView tvLogInTitle,tvLogInSubTitle,tvForgetPassword;
    private ImageView ivLogInEmail,ivLogInPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        findId();

        SharedPreferences preferences=getSharedPreferences(SharedPref.AppPackage,Context.MODE_PRIVATE);
        String email=preferences.getString(SharedPref.SpEmail,"");
        if (!email.isEmpty()){
            Intent intent=new Intent(LogInActivity.this,JoinMessActivity.class);
            startActivity(intent);
            finish();
        }

        db=FirebaseFirestore.getInstance();
        ref=db.document("messDatabase/userInfo");
        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LogInActivity.this,RegisterActivity.class);
                startActivity(intent);

            }
        });
        ibtLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logInOp();
            }
        });
    }

    private void logInOp() {
        final String inputEmail = etEmail.getText().toString();
        final String inputPass = etPassword.getText().toString().trim();
        if (inputEmail.isEmpty() && inputPass.isEmpty())
        {
            etEmail.setError("Enter Email");
            etPassword.setError("Enter Password");
        }
        if (inputPass.isEmpty()){
            etPassword.setError("Enter Password");
        }
        if (inputEmail.isEmpty()){
            etEmail.setError("Enter Email");
        }
        if (inputEmail.length()>50)
        {
            etEmail.setError("Email Size Is Too Long");

        }
      //  Toast.makeText(LogInActivity.this,"Clicked",Toast.LENGTH_SHORT).show();
        if (!inputEmail.isEmpty() && !inputPass.isEmpty() && inputEmail.length()<=50)
        {
            ibtLogIn.setVisibility(View.GONE);
            rlLogInContent.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
           loadUserInfo(inputEmail,inputPass);

        }

    }

    private void loadUserInfo(final String inputEmail, final String inputPass) {

        ref.collection("userInfoCollection").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            boolean exist=false;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                UserInfoModelClass data = document.toObject(UserInfoModelClass.class);
                                String userName=data.getUser_name();
                                String userEmail=data.getUser_email();
                                String userPassword=data.getUser_password();
                                String userNumber=data.getUser_number();
                                String userStatus=data.getUser_status();
                                String userMessKey=data.getMess_key();
                                String userMessName=data.getMess_name();
                                String userGender=data.getGender();
                                Log.d("Log_Tag","Gender : "+userGender+" "+" Pass : "+userPassword);
                                if (userEmail.equals(inputEmail) && userPassword.equals(inputPass)){
                                    exist=true;
                                    SharedPreferences sharedPreferences=getSharedPreferences(SharedPref.AppPackage, Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor=sharedPreferences.edit();
                                    editor.putString(SharedPref.SpEmail,userEmail);
                                    editor.putString(SharedPref.SpName,userName);
                                    editor.putString(SharedPref.SpNumber,userNumber);
                                    editor.putString(SharedPref.SpMessKey,userMessKey);
                                    editor.putString(SharedPref.SpGender,userGender);
                                    editor.putString(SharedPref.SpMessName,userMessName);
                                    editor.apply();
                                    StoredValues.userKey=document.getId();
                                    Toast.makeText(getApplicationContext(),"Key : "+StoredValues.userKey,Toast.LENGTH_SHORT).show();
                                    break;

                                }
                            }
                            if (exist==true){
                                startActivity(new Intent(LogInActivity.this,JoinMessActivity.class));
                                finish();
                            }
                            else {
                                etPassword.setError("Incorrect Password");
                                rlLogInContent.setVisibility(View.VISIBLE);
                                ibtLogIn.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                            }
                        } else {
                        }
                    }
                });

    }

    private void findId() {
        ibtLogIn=findViewById(R.id.ibtLogInId);
        btRegister=findViewById(R.id.btLogInRegisterId);
        etEmail=findViewById(R.id.etLogInEmailId);
        etPassword=findViewById(R.id.etLogInPasswordId);
        tvLogInTitle=findViewById(R.id.tvLogInTitle);
        tvLogInSubTitle=findViewById(R.id.tvLogInSubTitle);
        tvForgetPassword=findViewById(R.id.tvForgetPassId);
        ivLogInEmail=findViewById(R.id.ivLogInEmailID);
        ivLogInPass=findViewById(R.id.ivLogInPassID);
        progressBar=findViewById(R.id.pbLogInProgressBar);
        rlLogInContent=findViewById(R.id.rlLogInContent);
    }
}
