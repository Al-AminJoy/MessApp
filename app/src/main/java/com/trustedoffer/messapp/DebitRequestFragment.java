package com.trustedoffer.messapp;

import android.app.ProgressDialog;
import android.content.Context;
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
import com.trustedoffer.messapp.Adapter.MemberAdapter;
import com.trustedoffer.messapp.ConstantClasses.SharedPref;
import com.trustedoffer.messapp.Interface.NoMessageShowListener;
import com.trustedoffer.messapp.ModelClass.DebitReqModelClass;
import com.trustedoffer.messapp.ModelClass.MemberInfoModelClass;

import java.util.ArrayList;
import java.util.List;

public class DebitRequestFragment extends Fragment implements NoMessageShowListener {
    private RecyclerView recyclerView;
    private List<DebitReqModelClass> list=new ArrayList<>();
    private FirebaseFirestore db;
    private DocumentReference debitRef;
    private DebitReqAdapter adapter;
    private ProgressDialog progressDialog;
    private TextView tvNoDataMessage;
    private NoMessageShowListener noMessageShowListener;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_debit_request, container, false);
        findId(view);
        db = FirebaseFirestore.getInstance();
        debitRef=db.document("messDatabase/debitRequest");
        noMessageShowListener=this;
        initRecyclerView();
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
        progressOp();
        final SharedPreferences preferences=getActivity().getSharedPreferences(SharedPref.AppPackage,Context.MODE_PRIVATE);

        debitRef.collection("debitReqCollection")
                .whereEqualTo("mess_key",preferences.getString(SharedPref.SpMessKey,""))
                .whereEqualTo("approved",false)
                .orderBy("request_time", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                               DebitReqModelClass data=document.toObject(DebitReqModelClass.class);
                                data.setKey(document.getId());
                                list.add(data);
                            }
                            if (list.size()==0){
                                tvNoDataMessage.setVisibility(View.VISIBLE);
                                tvNoDataMessage.setText("No Request Found");
                                progressDialog.dismiss();
                            }
                            else {
                                tvNoDataMessage.setVisibility(View.GONE);
                                adapter=new DebitReqAdapter(noMessageShowListener,getContext(),list);
                                recyclerView.setAdapter(adapter);
                                progressDialog.dismiss();
                            }


                        } else {
                        }
                    }
                })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Failure","Exception: "+e);
            }
        });
    }

    private void findId(View view) {
        recyclerView=view.findViewById(R.id.rvDebitRequestId);
        tvNoDataMessage=view.findViewById(R.id.tvDebitReqNoDataFoundId);
    }

    private void initRecyclerView() {

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    public void NoMessageListener(boolean exist) {
        if (exist==false){
            tvNoDataMessage.setVisibility(View.VISIBLE);
            tvNoDataMessage.setText("No Debit Request");
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
