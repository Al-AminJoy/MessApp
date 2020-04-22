package com.trustedoffer.messapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.trustedoffer.messapp.ConstantClasses.SharedPref;
import com.trustedoffer.messapp.ConstantClasses.StoredValues;
import com.trustedoffer.messapp.ModelClass.JoinReqModelClass;
import com.trustedoffer.messapp.ModelClass.MessListModelClass;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class JoinMessActivity extends AppCompatActivity implements View.OnClickListener {
    private MaterialButton btJoinMess,btCreateMess;
    private ProgressBar pbJoin;
    private LinearLayout linLayButtons;
    private List<MessListModelClass> listMess=new ArrayList<>();
    private FirebaseFirestore db;
    private DocumentReference ref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_mess);
        SharedPreferences preferences=getSharedPreferences(SharedPref.AppPackage,Context.MODE_PRIVATE);
        String prevMessKey=preferences.getString(SharedPref.SpMessKey,"");
        if (prevMessKey.length()>0){
            Intent intent=new Intent(JoinMessActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
        findId();
        pbJoin.setVisibility(View.VISIBLE);
        linLayButtons.setVisibility(View.GONE);
        db=FirebaseFirestore.getInstance();
        ref=db.document("messDatabase/messList");
        loadData();
        btJoinMess.setOnClickListener(this);
        btCreateMess.setOnClickListener(this);
    }

    private void loadData() {
        ref.collection("messListCollection").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                MessListModelClass info= document.toObject(MessListModelClass.class);
                                listMess.add(info);
                            }
                            Toast.makeText(getApplicationContext(),"Mess Size: "+listMess.size(),Toast.LENGTH_SHORT).show();
                            pbJoin.setVisibility(View.GONE);
                            linLayButtons.setVisibility(View.VISIBLE);

                        } else {
                        }
                    }
                });
    }

    private void findId() {
        btJoinMess=findViewById(R.id.btJoinMessId);
        btCreateMess=findViewById(R.id.btCreateMessId);
        pbJoin=findViewById(R.id.pbJoinMessId);
        linLayButtons=findViewById(R.id.linLayJoinMessId);
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
        }

    }

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
                    db.collection("messDatabase").document("joinRequest").collection("joinReqCollection")
                            .whereEqualTo("user_email",preferences.getString(SharedPref.SpEmail,""))
                            .whereEqualTo("approved",false)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()){
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            JoinReqModelClass req=document.toObject(JoinReqModelClass.class);
                                            reqList.add(req);
                                        }
                                        if (reqList.size()==0){
                                            Task task1=db.document("messDatabase/userInfo/userInfoCollection/"+""+ StoredValues.userKey).update("mess_key",key,"mess_name",messName,"user_status","admin");
                                            String time= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                                            MessListModelClass list=new MessListModelClass(messName,key,time);
                                            Task task2=db.collection("messDatabase").document("messList").collection("messListCollection").add(list);
                                            Tasks.whenAllSuccess(task1,task2).addOnSuccessListener(new OnSuccessListener<List<Object>>() {
                                                @Override
                                                public void onSuccess(List<Object> objects) {
                                                    SharedPreferences.Editor editor=preferences.edit();
                                                    editor.putString(SharedPref.SpMessKey,key);
                                                    editor.putString(SharedPref.SpMessName,messName);
                                                    editor.apply();
                                                    Toast.makeText(getApplicationContext(),"Success",Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(JoinMessActivity.this,MainActivity.class));
                                                    finish();
                                                    alertDialog.cancel();
                                                }
                                            })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(getApplicationContext(),"Failed",Toast.LENGTH_SHORT).show();
                                                            btCreate.setVisibility(View.VISIBLE);
                                                            pbCreateMess.setVisibility(View.GONE);
                                                        }
                                                    });
                                        }
                                        else {
                                            Toast.makeText(getApplicationContext(),"Already Pending Request For Join",Toast.LENGTH_SHORT).show();
                                            btCreate.setVisibility(View.VISIBLE);
                                            pbCreateMess.setVisibility(View.GONE);
                                        }
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("Failed","Failed Reason :"+e);
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
                 final String joinTime=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                 final String joinMessKey=key;
                 db.collection("messDatabase").document("joinRequest").collection("joinReqCollection")
                         .whereEqualTo("user_email",joinEmail)
                         .whereEqualTo("approved",false)
                         .get()
                         .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                             @Override
                             public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                 if (task.isSuccessful()){
                                     for (QueryDocumentSnapshot document : task.getResult()) {
                                         JoinReqModelClass req=document.toObject(JoinReqModelClass.class);
                                         reqList.add(req);
                                     }
                                     if (reqList.size()==0){
                                         JoinReqModelClass join=new JoinReqModelClass(joinName,joinEmail,joinGender,joinTime,joinMessKey,false);
                                         db.collection("messDatabase").document("joinRequest").collection("joinReqCollection").add(join)
                                                 .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                     @Override
                                                     public void onSuccess(DocumentReference documentReference) {
                                                         Toast.makeText(getApplicationContext(),"Success\nWait Until\nApprove Request",Toast.LENGTH_SHORT).show();
                                                         alertDialog.cancel();
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
                             }
                         })
                 .addOnFailureListener(new OnFailureListener() {
                     @Override
                     public void onFailure(@NonNull Exception e) {
                         Log.d("Failed","Failed Reason :"+e);
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
