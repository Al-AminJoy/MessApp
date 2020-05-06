package com.trustedoffer.messapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.trustedoffer.messapp.Constant.SharedPref;
import com.trustedoffer.messapp.ModelClass.JoinReqModelClass;
import com.trustedoffer.messapp.ModelClass.MemberInfoModelClass;
import com.trustedoffer.messapp.ModelClass.MessListModelClass;
import com.trustedoffer.messapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JoinMessActivity extends AppCompatActivity implements View.OnClickListener {
    private MaterialButton btJoinMess,btCreateMess,btMyJoinReq,btMyJoinReqLogOut;
    private ProgressBar pbJoin,pbMyPendingReq;
    private LinearLayout linLayButtons;
    private List<MessListModelClass> listMess=new ArrayList<>();
    private FirebaseFirestore db;
    private DocumentReference ref;
    private String joinMessName;
    private String myReqKey,myReqMessName;
    private RequestQueue requestQueue;
    String URL="https://fcm.googleapis.com/fcm/send";
    private DatabaseReference infoReference,joinReference,messListReference;
    private FirebaseDatabase firebaseDatabase;
    private GoogleSignInClient mGoogleSignInClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_mess);
        findId();
        //For Authentication Log Out
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        requestQueue= Volley.newRequestQueue(this);

        pbJoin.setVisibility(View.VISIBLE);
        linLayButtons.setVisibility(View.GONE);
        btMyJoinReqLogOut.setVisibility(View.GONE);
      //Init Database
        joinReference = FirebaseDatabase.getInstance().getReference("joinRequest");
        infoReference = FirebaseDatabase.getInstance().getReference("userInfo");
        messListReference= FirebaseDatabase.getInstance().getReference("messList");
        firebaseDatabase = FirebaseDatabase.getInstance();
        loadData();
        btJoinMess.setOnClickListener(this);
        btCreateMess.setOnClickListener(this);
        btMyJoinReq.setOnClickListener(this);
        btMyJoinReqLogOut.setOnClickListener(this);
    }
    private void loadData() {
        messListReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    MessListModelClass mess = dataSnapshot1.getValue(MessListModelClass.class);
                    listMess.add(mess);
                }
                Toast.makeText(getApplicationContext(),"Mess Size: "+listMess.size(),Toast.LENGTH_SHORT).show();
                pbJoin.setVisibility(View.GONE);
                linLayButtons.setVisibility(View.VISIBLE);
                btMyJoinReqLogOut.setVisibility(View.VISIBLE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void findId() {
        btJoinMess=findViewById(R.id.btJoinMessId);
        btCreateMess=findViewById(R.id.btCreateMessId);
        pbJoin=findViewById(R.id.pbJoinMessId);
        linLayButtons=findViewById(R.id.linLayJoinMessId);
        btMyJoinReqLogOut=findViewById(R.id.btJoinMessLogOutId);
        btMyJoinReq=findViewById(R.id.btMyMessReqId);
        pbMyPendingReq=findViewById(R.id.pbMyMessReqShowDialogId);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btCreateMessId:
                showCreateMessDialog();
                break;
            case R.id.btJoinMessId:
                showJoinDialog();
                break;
            case R.id.btMyMessReqId:
                    myReq();
                break;
            case R.id.btJoinMessLogOutId:
                logoutOp();
                break;
        }
    }

    private void logoutOp() {
        FirebaseAuth.getInstance().signOut();
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplicationContext(),"Success",Toast.LENGTH_SHORT).show();
                    }
                });
        Intent intent=new Intent(JoinMessActivity.this, StartActivity.class);
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
    /**
     Checking User Previous Request
     */
    private void myReq() {
        btMyJoinReq.setVisibility(View.GONE);
        pbMyPendingReq.setVisibility(View.VISIBLE);
        final List<JoinReqModelClass> joinReqList=new ArrayList<>();
        final SharedPreferences pendingPreferences=getSharedPreferences(SharedPref.AppPackage, Context.MODE_PRIVATE);
        joinReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    JoinReqModelClass data = dataSnapshot1.getValue(JoinReqModelClass.class);
                    String email=pendingPreferences.getString(SharedPref.SpEmail,"");
                    if (data.getUser_email().equals(email) && data.isApproved()==false){
                        myReqKey=data.getKey();
                        joinReqList.add(data);
                    }
                }

                if (joinReqList.size()>0){
                    showMyPendingReqDialog(joinReqList);
                    btMyJoinReq.setVisibility(View.VISIBLE);
                    pbMyPendingReq.setVisibility(View.GONE);
                }
                else {
                    btMyJoinReq.setVisibility(View.VISIBLE);
                    pbMyPendingReq.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(),"No Request Found",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    /**
     Dialog For Show Pending Request
     */
    private void showMyPendingReqDialog(List<JoinReqModelClass> joinReqList) {

            myReqMessName=joinReqList.get(0).getMess_name();
        LayoutInflater layoutInflater = LayoutInflater.from(this);
            View view = layoutInflater.inflate(R.layout.my_join_req_dialog, null);
            ImageButton ibClose=view.findViewById(R.id.ibtMyMessReqDialogCloseId);
            final TextView tvMessName=view.findViewById(R.id.tvMyMessReqDialogNameId);
            final MaterialButton btCancel=view.findViewById(R.id.btMyMessReqDialogCancelId);
            final ProgressBar pbCancelMess=view.findViewById(R.id.pbMyMessReqDialogCancelId);

            final androidx.appcompat.app.AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setView(view).setCancelable(false).create();
            tvMessName.setText(myReqMessName);
            btCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pbCancelMess.setVisibility(View.VISIBLE);
                    btCancel.setVisibility(View.GONE);
                    joinReference.child(myReqKey)
                            .removeValue()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    pbCancelMess.setVisibility(View.GONE);
                                    btCancel.setVisibility(View.VISIBLE);
                                    alertDialog.cancel();
                                    Toast.makeText(getApplicationContext(),"Success",Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    pbCancelMess.setVisibility(View.GONE);
                                    btCancel.setVisibility(View.VISIBLE);
                                    Toast.makeText(getApplicationContext(),"Failed",Toast.LENGTH_SHORT).show();
                                }
                            });

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
     Dialog For Mess Create
     */
    private void showCreateMessDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View view = layoutInflater.inflate(R.layout.create_mess_dialog, null);
        ImageButton ibClose=view.findViewById(R.id.ibtCreateMessDialogCloseId);
        final TextInputEditText inTxtName=view.findViewById(R.id.inTxtCreateMessDialogNameId);
        final TextInputEditText inTxtKey=view.findViewById(R.id.inTxtCreateMessDialogKeyId);
        final MaterialButton btCreate=view.findViewById(R.id.btCreateMessDialogId);
        final ProgressBar pbCreateMess=view.findViewById(R.id.pbCreateMessDialogId);

        final androidx.appcompat.app.AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(view).setCancelable(false).create();

        btCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final List<JoinReqModelClass> reqList=new ArrayList<>();
                boolean exist=false;
                final String key=inTxtKey.getText().toString().trim();
                final String messName=inTxtName.getText().toString().trim();
                //Checking Key Is Exist or Not
                for (MessListModelClass data:listMess){
                    if (data.getMess_key().equals(key)){
                        exist=true;
                        break;
                    }
                    else {
                        exist=false;
                    }
                }
                if (inTxtKey.length()==0){
                    inTxtKey.setError("Insert Key");
                }
                if (inTxtKey.length()>16){
                    inTxtKey.setError("Too Long Size");
                }
                if (inTxtName.length()==0){
                    inTxtName.setError("Insert Name");
                }
                if (inTxtName.length()>64){
                    inTxtName.setError("Too Long Size");
                }
                if (exist==true){
                    inTxtKey.setError("Already Taken\nPlease,Choose Another");
                }

                if (exist==false && inTxtKey.length()>0 && inTxtKey.length()<=16 && inTxtName.length()>0 && inTxtName.length()<=64){
                    btCreate.setVisibility(View.GONE);
                    pbCreateMess.setVisibility(View.VISIBLE);
                    final SharedPreferences preferences=getSharedPreferences(SharedPref.AppPackage, Context.MODE_PRIVATE);
                    joinReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                JoinReqModelClass info = dataSnapshot1.getValue(JoinReqModelClass.class);
                                String reqEmail=preferences.getString(SharedPref.SpEmail,"");
                                //Check User Already Sent Request to Other mess or Not
                                if (info.getUser_email().equals(reqEmail) && info.isApproved()==false){
                                    reqList.add(info);
                                }
                            }
                            if (reqList.size()==0){
                                String createUserKey=preferences.getString(SharedPref.SpUserKey,"");
                                String user_name=preferences.getString(SharedPref.SpName,"");
                                String user_email=preferences.getString(SharedPref.SpEmail,"");
                                String user_number=preferences.getString(SharedPref.SpNumber,"");
                                String user_gender=preferences.getString(SharedPref.SpGender,"");
                                String user_image_url=preferences.getString(SharedPref.SpUserImage,"");
                                infoReference.child(createUserKey)
                                        .setValue(new MemberInfoModelClass(user_name,user_email,"admin",messName,key,user_number,user_gender,user_image_url,createUserKey))
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                final String messListKey = messListReference.push().getKey();
                                                String time= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                                                MessListModelClass list=new MessListModelClass(messName,key,time,messListKey);
                                                messListReference.child(messListKey).setValue(list).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        SharedPreferences.Editor editor=preferences.edit();
                                                        editor.putString(SharedPref.SpMessKey,key);
                                                        editor.putString(SharedPref.SpMessName,messName);
                                                        editor.putString(SharedPref.SpStatus,"admin");
                                                        editor.apply();
                                                        Toast.makeText(getApplicationContext(),"Success",Toast.LENGTH_SHORT).show();
                                                        startActivity(new Intent(JoinMessActivity.this,MainActivity.class));
                                                        finish();
                                                        alertDialog.cancel();
                                                        btCreate.setVisibility(View.GONE);
                                                        pbCreateMess.setVisibility(View.VISIBLE);
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(getApplicationContext(),"Failed",Toast.LENGTH_SHORT).show();

                                                    }
                                                });
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });
                            }

                            else {
                                Toast.makeText(getApplicationContext(),"Already Pending Request For Join",Toast.LENGTH_SHORT).show();
                                btCreate.setVisibility(View.VISIBLE);
                                pbCreateMess.setVisibility(View.GONE);
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
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
     *
     Posting Notification To Admin
     */
    private void sendNotification(String name,String messKey) {
        JSONObject json = new JSONObject();
        try {
            String topic=messKey+"Admin";
            json.put("to","/topics/"+topic);
            JSONObject notificationObj = new JSONObject();
           notificationObj.put("title",name+" Send Join Request");

            notificationObj.put("body",name+"Want To Join Your Mess\n" +
                    "Accept To Add In Your Mess");
            json.put("notification",notificationObj);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL,
                    json,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            Log.d("MUR", "onResponse: "+response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("MUR", "onError: "+error.networkResponse);
                }
            }
            ){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String,String> header = new HashMap<>();
                    header.put("content-type","application/json");
                    header.put("authorization","key=AAAAEObyC4E:APA91bEwcQeO2dePuv64QX6bVvFn83dWZKblxRxyFP9VSvJjozx0veuyx-4tEqWYiTIjFPfdq3DxGSJ9UG7HUkTvEDP9EmdWIEj9lLa-fsl1DBTNqQB87OYQF8Lf8qrfs3WQrNtCMP9y");
                    return header;
                }
            };
            requestQueue.add(request);
        }
        catch (JSONException e)

        {
            e.printStackTrace();
        }
    }
    /**
     Dialog For Join Mess By Key
     */
    private void showJoinDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View view = layoutInflater.inflate(R.layout.join_mess_dialog, null);
        ImageButton ibClose=view.findViewById(R.id.ibtJoinMessDialogCloseId);
        final TextInputEditText inTxtKey=view.findViewById(R.id.inTxtJoinMessDialogKeyId);
        final MaterialButton btJoin=view.findViewById(R.id.btJoinMessDialogId);
        final ProgressBar pbJoin=view.findViewById(R.id.pbJoinMessDialogId);

        final androidx.appcompat.app.AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(view).setCancelable(false).create();

        btJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final List<JoinReqModelClass> reqList=new ArrayList<>();


             if (inTxtKey.length()==0){
                 inTxtKey.setError("Insert Key");
                 return;
             }
              boolean exist=false;
             String key=inTxtKey.getText().toString().trim();
             for (MessListModelClass data:listMess){
                 if (data.getMess_key().equals(key)){
                    exist=true;
                     joinMessName=data.getMess_name();
                    break;
                 }
                 else {
                     exist=false;
                 }
             }
             if (exist==true && inTxtKey.length()>0){
                 btJoin.setVisibility(View.GONE);
                 pbJoin.setVisibility(View.VISIBLE);
                 final SharedPreferences sp=getSharedPreferences(SharedPref.AppPackage,Context.MODE_PRIVATE);
                 final String joinName=sp.getString(SharedPref.SpName,"");
                 final String joinEmail=sp.getString(SharedPref.SpEmail,"");
                 final String joinGender=sp.getString(SharedPref.SpGender,"");
                 final String joinUserKey=sp.getString(SharedPref.SpUserKey,"");
                 final String joinUserNumber=sp.getString(SharedPref.SpNumber,"");
                 final String joinUserImage=sp.getString(SharedPref.SpUserImage,"");
                 final String joinTime=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                 final String joinMessKey=key;
                 joinReference.addListenerForSingleValueEvent(new ValueEventListener() {
                     @Override
                     public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                         for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                             JoinReqModelClass info = dataSnapshot1.getValue(JoinReqModelClass.class);
                             String reqEmail=sp.getString(SharedPref.SpEmail,"");
                             //Checking User Already Have Sent Request Or Not
                             if (info.getUser_email().equals(reqEmail) && info.isApproved()==false){
                                 reqList.add(info);
                             }
                         }
                         if (reqList.size()==0){
                             final String joinReqKey = messListReference.push().getKey();
                             JoinReqModelClass join=new JoinReqModelClass(joinName,joinEmail,joinGender,joinTime,joinMessKey,joinMessName,joinUserKey,joinUserNumber,joinUserImage,false,joinReqKey);
                             //Add Data To Join Request Database
                             joinReference.child(joinReqKey).setValue(join).addOnSuccessListener(new OnSuccessListener<Void>() {
                                 @Override
                                 public void onSuccess(Void aVoid) {
                                     alertDialog.cancel();
                                     sendNotification(joinName,joinMessKey);
                                     Toast.makeText(getApplicationContext(),"Success!",Toast.LENGTH_SHORT).show();

                                     btJoin.setVisibility(View.VISIBLE);
                                     pbJoin.setVisibility(View.GONE);
                                 }
                             })
                                     .addOnFailureListener(new OnFailureListener() {
                                         @Override
                                         public void onFailure(@NonNull Exception e) {
                                             Toast.makeText(getApplicationContext(),"Failed",Toast.LENGTH_SHORT).show();
                                             btJoin.setVisibility(View.VISIBLE);
                                             pbJoin.setVisibility(View.GONE);
                                         }
                                     });
                         }

                         else {
                             Toast.makeText(getApplicationContext(),"Already Pending Request",Toast.LENGTH_SHORT).show();
                             btJoin.setVisibility(View.VISIBLE);
                             pbJoin.setVisibility(View.GONE);
                         }
                     }

                     @Override
                     public void onCancelled(@NonNull DatabaseError databaseError) {

                     }
                 });
             }
             else {
                 Toast.makeText(getApplicationContext(),"Couldn't Find",Toast.LENGTH_SHORT).show();
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
}
