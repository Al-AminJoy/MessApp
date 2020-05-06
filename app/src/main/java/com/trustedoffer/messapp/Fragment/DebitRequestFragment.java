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
import com.trustedoffer.messapp.Adapter.DebitReqAdapter;
import com.trustedoffer.messapp.Constant.SharedPref;
import com.trustedoffer.messapp.Interface.NoMessageShowListener;
import com.trustedoffer.messapp.ModelClass.DebitReqModelClass;
import com.trustedoffer.messapp.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DebitRequestFragment extends Fragment implements NoMessageShowListener {
    private RecyclerView recyclerView;
    private List<DebitReqModelClass> list = new ArrayList<>();
    private DebitReqAdapter adapter;
    private TextView tvNoDataMessage;
    private NoMessageShowListener noMessageShowListener;
    private SwipeRefreshLayout refreshLayout;
    private DatabaseReference ref;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_debit_request, container, false);
        findId(view);
        //set title name
        getActivity().setTitle("Expense Request");
        //int database
        ref = FirebaseDatabase.getInstance().getReference("debitRequest");
        //for show there is no message when all debit work done
        noMessageShowListener = this;

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

    private void loadData() {
        list.clear();
        refreshLayout.setRefreshing(true);
        final SharedPreferences preferences = getActivity().getSharedPreferences(SharedPref.AppPackage, Context.MODE_PRIVATE);
        ref.orderByChild("request_time").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    DebitReqModelClass data = dataSnapshot1.getValue(DebitReqModelClass.class);
                    String messKey = preferences.getString(SharedPref.SpMessKey, "");
                    if (data.getMess_key().equals(messKey) && data.isApproved() == false) {
                        list.add(data);
                    }
                }
                if (list.size() == 0) {
                    tvNoDataMessage.setVisibility(View.VISIBLE);
                    tvNoDataMessage.setText("No Request Found");
                    refreshLayout.setRefreshing(false);
                } else {
                    tvNoDataMessage.setVisibility(View.GONE);
                    adapter = new DebitReqAdapter(noMessageShowListener, getContext(), list);
                    Collections.reverse(list);
                    recyclerView.setAdapter(adapter);
                    refreshLayout.setRefreshing(false);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void findId(View view) {
        recyclerView = view.findViewById(R.id.rvDebitRequestId);
        tvNoDataMessage = view.findViewById(R.id.tvDebitReqNoDataFoundId);
        refreshLayout = view.findViewById(R.id.swipeDebitReqId);
    }

    private void initRecyclerView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    public void NoMessageListener(boolean exist) {
        if (exist == false) {
            tvNoDataMessage.setVisibility(View.VISIBLE);
            tvNoDataMessage.setText("No Expense Request");
        } else {
            tvNoDataMessage.setVisibility(View.GONE);
        }
    }

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
}
