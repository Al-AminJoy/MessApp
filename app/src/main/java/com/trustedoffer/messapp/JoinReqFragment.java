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
import com.trustedoffer.messapp.Adapter.JoinReqAdapter;
import com.trustedoffer.messapp.ConstantClasses.SharedPref;
import com.trustedoffer.messapp.Interface.NoMessageShowListener;
import com.trustedoffer.messapp.ModelClass.DebitReqModelClass;
import com.trustedoffer.messapp.ModelClass.JoinReqModelClass;

import java.util.ArrayList;
import java.util.List;

public class JoinReqFragment extends Fragment implements NoMessageShowListener {
    private RecyclerView recyclerView;
    private List<JoinReqModelClass> list=new ArrayList<>();
    private FirebaseFirestore db;
    private DocumentReference joinRef;
    private JoinReqAdapter adapter;
    private ProgressDialog progressDialog;
    private TextView tvNoDataMessage;
    private NoMessageShowListener noMessageShowListener;
    private SwipeRefreshLayout refreshLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_join_req, container, false);
        findId(view);
        getActivity().setTitle("Join Request");
        db=FirebaseFirestore.getInstance();
        joinRef=db.document("messDatabase/joinRequest");
        noMessageShowListener=this;
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

        joinRef.collection("joinReqCollection")
                .whereEqualTo("mess_key",preferences.getString(SharedPref.SpMessKey,""))
                .whereEqualTo("approved",false)
                .orderBy("send_time", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                JoinReqModelClass data=document.toObject(JoinReqModelClass.class);
                                data.setKey(document.getId());
                                list.add(data);
                            }
                            if (list.size()==0){
                                tvNoDataMessage.setVisibility(View.VISIBLE);
                                tvNoDataMessage.setText("No Request Found");
                                refreshLayout.setRefreshing(false);
                            }
                            else {
                                tvNoDataMessage.setVisibility(View.GONE);
                                adapter=new JoinReqAdapter(noMessageShowListener,getContext(),list);
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
                        refreshLayout.setRefreshing(false);
                        Log.d("Failure","Exception: "+e);
                    }
                });
    }

    private void findId(View view) {
        recyclerView=view.findViewById(R.id.rvJoinRequestId);
        tvNoDataMessage=view.findViewById(R.id.tvJoinReqNoDataFoundId);
        refreshLayout=view.findViewById(R.id.swipeJoinReqId);
    }

    private void initRecyclerView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    public void NoMessageListener(boolean exist) {
        if (exist==false){
            tvNoDataMessage.setVisibility(View.VISIBLE);
            tvNoDataMessage.setText("No Join Request");
        }
        else {
            tvNoDataMessage.setVisibility(View.GONE);
        }

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
