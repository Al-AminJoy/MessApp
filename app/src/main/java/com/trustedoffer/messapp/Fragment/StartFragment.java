package com.trustedoffer.messapp.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.trustedoffer.messapp.Activity.JoinMessActivity;
import com.trustedoffer.messapp.Activity.MainActivity;
import com.trustedoffer.messapp.Constant.SharedPref;
import com.trustedoffer.messapp.Fragment.SignUpFragment;
import com.trustedoffer.messapp.ModelClass.MemberInfoModelClass;
import com.trustedoffer.messapp.R;

import java.util.ArrayList;
import java.util.List;

public class StartFragment extends Fragment {
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    private MaterialButton btStart;
    private FirebaseAuth mAuth;
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;
    private List<MemberInfoModelClass> list = new ArrayList<>();
    private boolean exist = false;
    private ProgressBar pbStart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_start, container, false);
        findId(view);
        databaseReference = FirebaseDatabase.getInstance().getReference("userInfo");
        firebaseDatabase = FirebaseDatabase.getInstance();
        btStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btStart.setVisibility(View.GONE);
                pbStart.setVisibility(View.VISIBLE);
                signIn();
            }
        });
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
        mAuth = FirebaseAuth.getInstance();
        return view;
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        btStart.setVisibility(View.GONE);
        pbStart.setVisibility(View.VISIBLE);
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            btStart.setVisibility(View.GONE);
                            pbStart.setVisibility(View.VISIBLE);
                            loadData();

                        } else {
                            Toast.makeText(getActivity(), "Failed", Toast.LENGTH_SHORT).show();
                            btStart.setVisibility(View.VISIBLE);
                            pbStart.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void loadData() {
        final FirebaseUser user = mAuth.getCurrentUser();
        final String email = user.getEmail();
        final String userImageUrl = user.getPhotoUrl().toString();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    MemberInfoModelClass info = dataSnapshot1.getValue(MemberInfoModelClass.class);
                    String userName = info.getUser_name();
                    String userEmail = info.getUser_email();
                    String userNumber = info.getUser_number();
                    String userStatus = info.getUser_status();
                    String userMessKey = info.getMess_key();
                    String userMessName = info.getMess_name();
                    String userGender = info.getGender();
                    String userKey = info.getKey();
                    if (email.equals(userEmail)) {
                        exist = true;
                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SharedPref.AppPackage, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(SharedPref.SpEmail, userEmail);
                        editor.putString(SharedPref.SpName, userName);
                        editor.putString(SharedPref.SpNumber, userNumber);
                        editor.putString(SharedPref.SpMessKey, userMessKey);
                        editor.putString(SharedPref.SpGender, userGender);
                        editor.putString(SharedPref.SpStatus, userStatus);
                        editor.putString(SharedPref.SpMessName, userMessName);
                        editor.putString(SharedPref.SpUserKey, userKey);
                        editor.putString(SharedPref.SpUserImage, userImageUrl);
                        editor.apply();
                        // Toast.makeText(getApplicationContext(),"Key : "+StoredValues.userKey,Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
                //Checking User Previously Logged in or not
                if (exist == true) {
                    SharedPreferences preferences = getActivity().getSharedPreferences(SharedPref.AppPackage, Context.MODE_PRIVATE);
                    String messKey = preferences.getString(SharedPref.SpMessKey, "");
                    if (messKey.length() == 0) {
                        Intent intent = new Intent(getActivity(), JoinMessActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    } else {
                        startActivity(new Intent(getActivity(), MainActivity.class));
                        getActivity().finish();
                    }
                } else {
                    //Passing Data To SignUp Fragment
                    Bundle bundle = new Bundle();
                    bundle.putString("logInUserEmail", email);
                    bundle.putString("logInUserImageUrl", userImageUrl);
                    SignUpFragment frag = new SignUpFragment();
                    frag.setArguments(bundle);
                    getFragmentManager()
                            .beginTransaction()
                            .replace(R.id.frameLogInActivityId, frag)
                            .commit();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            btStart.setVisibility(View.GONE);
            pbStart.setVisibility(View.VISIBLE);
            loadData();
        } else {
            btStart.setVisibility(View.VISIBLE);
            pbStart.setVisibility(View.GONE);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {

            }
        }
        //After User Select Email or Cancel to Sign In
        btStart.setVisibility(View.VISIBLE);
        pbStart.setVisibility(View.GONE);

    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void findId(View view) {
        btStart = view.findViewById(R.id.btStartId);
        pbStart = view.findViewById(R.id.pbStartId);
    }
}
