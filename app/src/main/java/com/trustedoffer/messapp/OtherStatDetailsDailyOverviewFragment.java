package com.trustedoffer.messapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.trustedoffer.messapp.Adapter.DailyStatAdapter;
import com.trustedoffer.messapp.ConstantClasses.StoredValues;
import com.trustedoffer.messapp.ModelClass.UserDataModelClass;

import java.util.ArrayList;
import java.util.List;


public class OtherStatDetailsDailyOverviewFragment extends Fragment {
     private String userEmail;
     private RecyclerView recyclerView;
    private List<UserDataModelClass> dataList=new ArrayList<>();
    private List<UserDataModelClass> list=new ArrayList<>();
    private DailyStatAdapter adapter;
    private TextView tvMonthName;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_other_stat_details_daily_overview, container, false);
        findId(view);
        tvMonthName.setText(StoredValues.monthName);
        Bundle bundle=this.getArguments();
        if (bundle!=null){
            userEmail=bundle.getString("test");
            Toast.makeText(getContext(),"Find Email : "+userEmail,Toast.LENGTH_LONG).show();
        }
        initRecyclerView();
        loadData();
        return view;
    }

    private void loadData() {
      //  int month=StoredValues.month;
        dataList= StoredValues.messThisMonthData;
        for (UserDataModelClass data:dataList){
            if (userEmail.equals(data.getUser_email())){
                list.add(data);
            }
        }
        adapter=new DailyStatAdapter(getContext(),list);
        recyclerView.setAdapter(adapter);
    }

    private void initRecyclerView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void findId(View view) {
        recyclerView=view.findViewById(R.id.rvOtherStatDailyId);
        tvMonthName=view.findViewById(R.id.tvOtherDailyStatMonthId);
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
