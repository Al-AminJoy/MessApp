package com.trustedoffer.messapp.Fragment;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.trustedoffer.messapp.Adapter.MemberAdapter;
import com.trustedoffer.messapp.Constant.StoredValues;
import com.trustedoffer.messapp.ModelClass.MemberInfoModelClass;
import com.trustedoffer.messapp.R;

import java.util.ArrayList;
import java.util.List;

public class MemberFragment extends Fragment {
    private RecyclerView recyclerView;
    private List<MemberInfoModelClass> listItem = new ArrayList<>();
    private MemberAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_member, container, false);
        findId(view);
        getActivity().setTitle("Members");
        initRecyclerView();
        loadData();
        return view;
    }

    private void initRecyclerView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void loadData() {
        listItem = StoredValues.memberInfo;
        adapter = new MemberAdapter(getContext(), listItem);
        recyclerView.setAdapter(adapter);
    }

    private void findId(View view) {
        recyclerView = view.findViewById(R.id.rvMemberId);
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
