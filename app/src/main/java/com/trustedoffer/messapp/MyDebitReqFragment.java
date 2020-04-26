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
import android.view.Menu;
import android.view.MenuInflater;
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
import com.trustedoffer.messapp.Adapter.DebitReqAdapter;
import com.trustedoffer.messapp.Adapter.MyDebitReqAdapter;
import com.trustedoffer.messapp.ConstantClasses.SharedPref;
import com.trustedoffer.messapp.ConstantClasses.StoredValues;
import com.trustedoffer.messapp.ModelClass.DebitReqModelClass;

import java.util.ArrayList;
import java.util.List;

public class MyDebitReqFragment extends Fragment {
    private RecyclerView recyclerView;
    private List<DebitReqModelClass> list=new ArrayList<>();
    private FirebaseFirestore db;
    private DocumentReference debitRef;
    private MyDebitReqAdapter adapter;
    private ProgressDialog progressDialog;
    private TextView tvNoDataMessage;
    private SwipeRefreshLayout swipeRefreshLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_my_debit_req, container, false);
        findId(view);
        db = FirebaseFirestore.getInstance();
        debitRef=db.document("messDatabase/debitRequest");
        initRecyclerView();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
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
        swipeRefreshLayout.setRefreshing(true);
        final SharedPreferences preferences=getActivity().getSharedPreferences(SharedPref.AppPackage, Context.MODE_PRIVATE);

        debitRef.collection("debitReqCollection")
                .whereEqualTo("mess_key",preferences.getString(SharedPref.SpMessKey,""))
                .whereEqualTo("user_email",preferences.getString(SharedPref.SpEmail,""))
                .orderBy("request_time", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                int month= StoredValues.month;
                                int previousMonth=(month-1);
                                DebitReqModelClass data=document.toObject(DebitReqModelClass.class);
                                if (month==data.getMonth() || previousMonth==data.getMonth()){
                                    data.setKey(document.getId());
                                    list.add(data);
                                }

                            }
                            if (list.size()==0){
                                tvNoDataMessage.setVisibility(View.VISIBLE);
                                tvNoDataMessage.setText("No Request Found");
                                swipeRefreshLayout.setRefreshing(false);
                            }
                            else {
                                tvNoDataMessage.setVisibility(View.GONE);
                                adapter=new MyDebitReqAdapter(getContext(),list);
                                recyclerView.setAdapter(adapter);
                                swipeRefreshLayout.setRefreshing(false);
                            }


                        } else {
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Failure","Exception: "+e);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
    }

    private void findId(View view) {
        recyclerView=view.findViewById(R.id.rvMyDebitRequestId);
        tvNoDataMessage=view.findViewById(R.id.tvMyDebitReqNoDataFoundId);
        swipeRefreshLayout=view.findViewById(R.id.swipeMyDebitReqId);
    }

    private void initRecyclerView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
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
