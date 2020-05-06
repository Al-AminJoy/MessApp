package com.trustedoffer.messapp.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.trustedoffer.messapp.Adapter.MyDebitReqAdapter;
import com.trustedoffer.messapp.Constant.SharedPref;
import com.trustedoffer.messapp.Constant.StoredValues;
import com.trustedoffer.messapp.ModelClass.DebitReqModelClass;
import com.trustedoffer.messapp.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MyDebitReqFragment extends Fragment {
    private RecyclerView recyclerView;
    private List<DebitReqModelClass> list = new ArrayList<>();
    private MyDebitReqAdapter adapter;
    private ProgressDialog progressDialog;
    private TextView tvNoDataMessage;
    private SwipeRefreshLayout swipeRefreshLayout;
    private DatabaseReference ref;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_debit_req, container, false);
        findId(view);
        //init database
        ref = FirebaseDatabase.getInstance().getReference("debitRequest");
        //
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

    private void loadData() {
        list.clear();
        swipeRefreshLayout.setRefreshing(true);
        final SharedPreferences preferences = getActivity().getSharedPreferences(SharedPref.AppPackage, Context.MODE_PRIVATE);
        ref.orderByChild("request_time").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    DebitReqModelClass modelClass = dataSnapshot1.getValue(DebitReqModelClass.class);
                    String messKey = preferences.getString(SharedPref.SpMessKey, "");
                    String userEmail = preferences.getString(SharedPref.SpEmail, "");
                    if (modelClass.getMess_key().equals(messKey) && modelClass.getUser_email().equals(userEmail)) {
                        int month = StoredValues.month;
                        int previousMonth = (month - 1);
                        if (month == modelClass.getMonth() || previousMonth == modelClass.getMonth()) {
                            list.add(modelClass);
                        }
                    }
                }
                if (list.size() == 0) {
                    tvNoDataMessage.setVisibility(View.VISIBLE);
                    tvNoDataMessage.setText("No Request Found");
                    swipeRefreshLayout.setRefreshing(false);
                } else {
                    tvNoDataMessage.setVisibility(View.GONE);
                    adapter = new MyDebitReqAdapter(getContext(), list);
                    Collections.reverse(list);
                    recyclerView.setAdapter(adapter);
                    swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void findId(View view) {
        recyclerView = view.findViewById(R.id.rvMyDebitRequestId);
        tvNoDataMessage = view.findViewById(R.id.tvMyDebitReqNoDataFoundId);
        swipeRefreshLayout = view.findViewById(R.id.swipeMyDebitReqId);
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
