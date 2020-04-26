package com.trustedoffer.messapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.trustedoffer.messapp.Adapter.MyUpdateReqAdapter;
import com.trustedoffer.messapp.Adapter.UpdateReqAdapter;
import com.trustedoffer.messapp.ConstantClasses.SharedPref;
import com.trustedoffer.messapp.ConstantClasses.StoredValues;
import com.trustedoffer.messapp.ModelClass.UpdateReqModelClass;

import java.util.ArrayList;
import java.util.List;

public class MyUpdateReqFragment extends Fragment {
    private RecyclerView recyclerView;
    private List<UpdateReqModelClass> list=new ArrayList<>();
    private FirebaseFirestore db;
    private DocumentReference updateRef;
    private MyUpdateReqAdapter adapter;
    private ProgressDialog progressDialog;
    private TextView tvNoDataMessage;
    private SwipeRefreshLayout refreshLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_my_update_req, container, false);
        findId(view);
        db = FirebaseFirestore.getInstance();
        updateRef=db.document("messDatabase/updateRequest");
        initRecyclerView();
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });
        loadData();
        return view;
    }
    private void progressOp() {
        progressDialog = new ProgressDialog(getActivity(), R.style.ProgressColor);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }
    private void loadData() {
        list.clear();
        refreshLayout.setRefreshing(true);
        final SharedPreferences preferences=getActivity().getSharedPreferences(SharedPref.AppPackage, Context.MODE_PRIVATE);
        updateRef.collection("updateReqCollection")
                .whereEqualTo("mess_key",preferences.getString(SharedPref.SpMessKey,""))
                .whereEqualTo("user_email",preferences.getString(SharedPref.SpEmail,""))
                .orderBy("request_time", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                UpdateReqModelClass data=document.toObject(UpdateReqModelClass.class);
                                int month= StoredValues.month;
                                int previousMonth=(month-1);
                                if (month==data.getMonth() || previousMonth==data.getMonth()){
                                    data.setKey(document.getId());
                                    list.add(data);
                                }

                            }
                            if (list.size()==0){
                                tvNoDataMessage.setVisibility(View.VISIBLE);
                                tvNoDataMessage.setText("No Request Found");
                                refreshLayout.setRefreshing(false);
                            }
                            else {
                                tvNoDataMessage.setVisibility(View.GONE);
                                adapter=new MyUpdateReqAdapter(getContext(),list);
                                recyclerView.setAdapter(adapter);
                                refreshLayout.setRefreshing(false);
                            }


                        } else {
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Failure","Exception: "+e);
                        refreshLayout.setRefreshing(false);
                    }
                });
    }

    private void findId(View view) {
        recyclerView=view.findViewById(R.id.rvMyUpdateRequestId);
        tvNoDataMessage=view.findViewById(R.id.tvMyUpdateReqNoDataFoundId);
        refreshLayout=view.findViewById(R.id.swipeMyUpdateReq);
    }

    private void initRecyclerView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

    }
}
