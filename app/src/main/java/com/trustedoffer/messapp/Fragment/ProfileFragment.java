package com.trustedoffer.messapp.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.trustedoffer.messapp.Constant.SharedPref;
import com.trustedoffer.messapp.ModelClass.MemberInfoModelClass;
import com.trustedoffer.messapp.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment implements View.OnClickListener {
    private ImageButton ibtEdit;
    private TextView tvName, tvStatus, tvMessName, tvEmail, tvNumber, tvGender;
    private CircleImageView ivUser;
    private DatabaseReference ref;
    private FirebaseDatabase firebaseDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        findId(view);
        ref = FirebaseDatabase.getInstance().getReference("userInfo");
        firebaseDatabase = FirebaseDatabase.getInstance();
        getActivity().setTitle("Profile");
        setViews();
        ibtEdit.setOnClickListener(this);
        return view;
    }

    private void setViews() {
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences(SharedPref.AppPackage, Context.MODE_PRIVATE);
        String name = sharedPreferences.getString(SharedPref.SpName, "");
        String image = sharedPreferences.getString(SharedPref.SpUserImage, "");
        tvName.setText(name);
        Picasso.get().load(image).into(ivUser);
        String status = sharedPreferences.getString(SharedPref.SpStatus, "");
        if (status.equals("admin")) {
            tvStatus.setText("Admin (" + sharedPreferences.getString(SharedPref.SpMessKey, "") + ")");
        } else {
            tvStatus.setText("Member");
        }
        tvMessName.setText(sharedPreferences.getString(SharedPref.SpMessName, ""));
        tvEmail.setText(sharedPreferences.getString(SharedPref.SpEmail, ""));
        String number = sharedPreferences.getString(SharedPref.SpNumber, "");
        if (number.equals("0")) {
            tvNumber.setText("---");
        } else {
            tvNumber.setText(number);
        }
        String gender = sharedPreferences.getString(SharedPref.SpGender, "");
        tvGender.setText(gender);

    }

    private void findId(View view) {
        ibtEdit = view.findViewById(R.id.ibtProfileEdit);
        tvName = view.findViewById(R.id.tvProfileNameId);
        tvStatus = view.findViewById(R.id.tvProfileStatusId);
        tvMessName = view.findViewById(R.id.tvProfileMessNameId);
        tvEmail = view.findViewById(R.id.tvProfileEmailId);
        tvNumber = view.findViewById(R.id.tvProfileNumberId);
        tvGender = view.findViewById(R.id.tvProfileGenderId);
        ivUser = view.findViewById(R.id.ivProfileId);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ibtProfileEdit:
                editProfile();
                break;
        }
    }

    /**
     * Showing Dialog
     */
    private void editProfile() {
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences(SharedPref.AppPackage, Context.MODE_PRIVATE);
        String name = sharedPreferences.getString(SharedPref.SpName, "");
        String number = sharedPreferences.getString(SharedPref.SpNumber, "");
        String gender = sharedPreferences.getString(SharedPref.SpGender, "");
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        final View dialogView = layoutInflater.inflate(R.layout.edite_profile_dialog, null);
        ImageButton ibClose = dialogView.findViewById(R.id.ibtEditeProfileDialogCloseId);
        final RadioGroup rgGender = dialogView.findViewById(R.id.rgEditGenderId);
        final ProgressBar progressBar = dialogView.findViewById(R.id.pbUpdateProfileProgressBarId);
        final MaterialButton btSave = dialogView.findViewById(R.id.btEditProfileSaveId);
        final TextInputEditText inTxtName = dialogView.findViewById(R.id.inTxtEditNameId);
        final TextInputEditText inTxtNumber = dialogView.findViewById(R.id.inTxtEditNumberId);
        RadioButton rbMale = dialogView.findViewById(R.id.rbEditProfileMaleId);
        RadioButton rbFemale = dialogView.findViewById(R.id.rbEditProfileFemaleId);
        RadioButton rbOther = dialogView.findViewById(R.id.rbEditProfileOtherId);
        final androidx.appcompat.app.AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setView(dialogView).setCancelable(false).create();
        switch (gender) {
            case "Male":
                rbMale.setChecked(true);
                break;
            case "Female":
                rbFemale.setChecked(true);
                break;
            case "Other":
                rbOther.setChecked(true);
                break;
        }
        inTxtName.setText(name);
        inTxtNumber.setText(number);
        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String updatedName = inTxtName.getText().toString().trim();
                String updateNumber = inTxtNumber.getText().toString().trim();
                int updateGenderId = rgGender.getCheckedRadioButtonId();
                RadioButton radioButton = dialogView.findViewById(updateGenderId);
                String updateGender = radioButton.getText().toString();
                if (inTxtName.length() > 64) {
                    inTxtName.setError("Too Long Name");
                }
                if (inTxtNumber.length() > 15) {
                    inTxtNumber.setError("Too Long Number");
                }
                if (inTxtName.length() <= 0) {
                    inTxtName.setError("Empty Name");
                }
                if (inTxtNumber.length() <= 0) {
                    updateNumber = "";
                }
                if (inTxtName.length() > 0 && inTxtName.length() <= 64 && inTxtNumber.length() <= 15) {
                    update(updatedName, updateNumber, updateGender, progressBar, btSave, alertDialog);
                }

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
     * Updating Profile
     */
    private void update(final String updatedName, final String updateNumber, final String updateGender, final ProgressBar progressBar, final MaterialButton btSave, final AlertDialog alertDialog) {
        progressBar.setVisibility(View.VISIBLE);
        btSave.setVisibility(View.GONE);
        SharedPreferences spUpdate = this.getActivity().getSharedPreferences(SharedPref.AppPackage, Context.MODE_PRIVATE);
        String userKey = spUpdate.getString(SharedPref.SpUserKey, "");
        String userEmail = spUpdate.getString(SharedPref.SpEmail, "");
        String userImage = spUpdate.getString(SharedPref.SpUserImage, "");
        String userStatus = spUpdate.getString(SharedPref.SpStatus, "");
        String userMessName = spUpdate.getString(SharedPref.SpMessName, "");
        String userMessKey = spUpdate.getString(SharedPref.SpMessKey, "");
        //Updating UserData
        ref.child(userKey)
                .setValue(new MemberInfoModelClass(updatedName, userEmail, userStatus, userMessName, userMessKey, updateNumber, updateGender, userImage, userKey))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
                        SharedPreferences preferences = getActivity().getSharedPreferences(SharedPref.AppPackage, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString(SharedPref.SpName, updatedName);
                        editor.putString(SharedPref.SpNumber, updateNumber);
                        editor.putString(SharedPref.SpGender, updateGender);
                        editor.apply();
                        tvName.setText(updatedName);
                        tvGender.setText(updateGender);
                        tvNumber.setText(updateNumber);
                        alertDialog.cancel();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                btSave.setVisibility(View.VISIBLE);

            }
        });

    }
    /**
     *
     */

    /**
     * Hide overflow menu option
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }
    /**
     *
     *
     */
}
