package com.trustedoffer.messapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.trustedoffer.messapp.Adapter.MemberAdapter;
import com.trustedoffer.messapp.ConstantClasses.SharedPref;
import com.trustedoffer.messapp.ConstantClasses.StoredValues;
import com.trustedoffer.messapp.ModelClass.MemberInfoModelClass;
import com.trustedoffer.messapp.ModelClass.UserInfoModelClass;

import java.util.ArrayList;
import java.util.List;

public class MemberFragment extends Fragment {
    private RecyclerView recyclerView;
    private List<MemberInfoModelClass> listItem=new ArrayList<>();
    private MemberAdapter adapter;
    private ProgressDialog progressDialog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_member, container, false);
        findId(view);
        initRecyclerView();
        loadData();
        return view;
    }

    private void initRecyclerView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }
    private void progressOp() {
        progressDialog = new ProgressDialog(getActivity(), R.style.ProgressColor);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }
    private void loadData() {
        progressOp();
       /* ref.collection("userInfoCollection").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                MemberInfoModelClass info= document.toObject(MemberInfoModelClass.class);

                                String userMessKey=info.getMess_key();
                                String email=info.getUser_email();
                                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SharedPref.AppPackage, Context.MODE_PRIVATE);
                                String key=sharedPreferences.getString(SharedPref.SpMessKey,"");
                                Log.d("Log_Tag","Name : "+key+" Email : "+email);
                                if (userMessKey.equals(key)){
                                    Log.d("Add_Data_Log_Tag","Name : "+info.getUser_name()+" Email : "+info.getUser_email());
                                    info.setKey(document.getId());
                                    listItem.add(info);
                                }
                            }
                            adapter=new MemberAdapter(getContext(),listItem);
                            recyclerView.setAdapter(adapter);
                            progressDialog.dismiss();

                        } else {
                        }
                    }
                });*/
        listItem= StoredValues.memberInfo;
        adapter=new MemberAdapter(getContext(),listItem);
        recyclerView.setAdapter(adapter);
        progressDialog.dismiss();

    }

    private void findId(View view) {
    recyclerView=view.findViewById(R.id.rvMemberId);
    }
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
}
