package com.trustedoffer.messapp.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.trustedoffer.messapp.Activity.JoinMessActivity;
import com.trustedoffer.messapp.Constant.SharedPref;
import com.trustedoffer.messapp.ModelClass.MemberInfoModelClass;
import com.trustedoffer.messapp.R;

public class SignUpFragment extends Fragment {
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    private EditText etName, etEmail, etNumber, etGender, etPassword;
    private MaterialButton btLogIn;
    private ImageButton btNext;
    private String name, email, gender, number, userImageUrl;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        findId(view);
        Bundle bundle = this.getArguments();
        //Receiving Data From Start Fragment
        if (bundle != null) {
            email = bundle.getString("logInUserEmail");
            userImageUrl = bundle.getString("logInUserImageUrl");
        }
        //init database
        databaseReference = FirebaseDatabase.getInstance().getReference("userInfo");

        btNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkData();
            }
        });
        etGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showGenderDialog();
            }
        });
        return view;
    }

    private void checkData() {
        name = etName.getText().toString().trim();
        gender = etGender.getText().toString().trim();
        number = etNumber.getText().toString().trim();
        if (etName.length() > 64) {
            etName.setError("Too Long Name");
        }
        if (etName.length() <= 0) {
            etName.setError("Empty Name");
        }
        if (etNumber.length() > 15) {
            etNumber.setError("Too Long Number");
        }
        if (etNumber.length() <= 0) {
            number = "";
        }
        if (etGender.length() <= 0) {
            etGender.setError("Empty Gender");
        }
        if (etName.length() > 0 && etGender.length() > 0 && etName.length() <= 64 && etNumber.length() <= 15) {
            register();
        }

    }

    private void register() {
        final String key = databaseReference.push().getKey();
        MemberInfoModelClass info = new MemberInfoModelClass(name, email, "member", "", "", number, gender, userImageUrl, key);
        databaseReference.child(key).setValue(info).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getContext(), "Success : ", Toast.LENGTH_LONG).show();
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SharedPref.AppPackage, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(SharedPref.SpEmail, email);
                editor.putString(SharedPref.SpName, name);
                editor.putString(SharedPref.SpNumber, number);
                editor.putString(SharedPref.SpMessKey, "");
                editor.putString(SharedPref.SpGender, gender);
                editor.putString(SharedPref.SpStatus, "member");
                editor.putString(SharedPref.SpMessName, "");
                editor.putString(SharedPref.SpUserKey, key);
                editor.putString(SharedPref.SpUserImage, userImageUrl);
                editor.apply();
                startActivity(new Intent(getActivity(), JoinMessActivity.class));
                getActivity().finish();
            }
        });


    }

    private void findId(View view) {
        etName = view.findViewById(R.id.etRegiNameId);
        etGender = view.findViewById(R.id.etRegiGenderId);
        etNumber = view.findViewById(R.id.etRegiNumberId);
        btNext = view.findViewById(R.id.ibtRegiNextId);

    }

    private void showGenderDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View alertView = getLayoutInflater().inflate(R.layout.gender_dialog, null);
        builder.setView(alertView);
        final AlertDialog dialog = builder.create();
        CardView cvMale, cvFemale, cvOther;
        cvMale = alertView.findViewById(R.id.cvRegisterMaleId);
        cvFemale = alertView.findViewById(R.id.cvRegisterFemaleId);
        cvOther = alertView.findViewById(R.id.cvRegisterOtherId);
        cvMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etGender.setText("Male");
                dialog.dismiss();

            }
        });
        cvFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etGender.setText("Female");
                dialog.dismiss();
            }
        });
        cvOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etGender.setText("Other");
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
