package com.trustedoffer.messapp.Fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.trustedoffer.messapp.Adapter.PrevIndivAdapter;
import com.trustedoffer.messapp.Constant.StoredValues;
import com.trustedoffer.messapp.ModelClass.MemberInfoModelClass;
import com.trustedoffer.messapp.ModelClass.OthersStatModelClass;
import com.trustedoffer.messapp.ModelClass.UserDataModelClass;
import com.trustedoffer.messapp.R;

import java.util.ArrayList;
import java.util.List;

public class PrevMonthIndividualOverviewFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<OthersStatModelClass> listStat = new ArrayList<>();
    private List<UserDataModelClass> listData = new ArrayList<>();
    List<MemberInfoModelClass> list = new ArrayList<>();
    private PrevIndivAdapter adapter;
    private double mealRate = 0;
    private ProgressDialog progressDialog;
    private TextView tvMonthName, tvNoDataMessage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_prev_month_individual_overview, container, false);
        findId(view);
        tvMonthName.setText(StoredValues.prevMonth);
        initRecyclerView();
        loadData();
        return view;
    }

    private void initRecyclerView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void loadData() {
        progressOp();
        list = StoredValues.memberInfo;
        listData = StoredValues.messLastMonthData;
        for (MemberInfoModelClass data : list) {

            String userEmail = data.getUser_email();
            int totalMeal = 0;
            double userDebit = 0;
            double getBack;
            double used;
            String userName = "";

            for (UserDataModelClass userData : listData) {

                if (userEmail.equals(userData.getUser_email())) {
                    userName = userData.getUser_name();
                    int breakfast = userData.getBreakfast();
                    int lunch = userData.getLunch();
                    int dinner = userData.getDinner();
                    double debit = userData.getDebit();
                    userDebit = (userDebit + debit);
                    int singleDayMeal = (breakfast + lunch + dinner);
                    totalMeal = (totalMeal + singleDayMeal);
                }
            }
            if (totalMeal <= 0) {
                mealRate = 0;
            } else {
                mealRate = userDebit / totalMeal;
            }
            used = (mealRate * totalMeal);

            getBack = (userDebit - used);
            if (totalMeal > 0 || userDebit > 0) {
                OthersStatModelClass dataSet = new OthersStatModelClass(userName, userEmail, totalMeal, userDebit, used, getBack);
                listStat.add(dataSet);

            }
        }
        if (listStat.size() == 0) {
            tvNoDataMessage.setVisibility(View.VISIBLE);
            tvNoDataMessage.setText("No Previous Data Found");
            progressDialog.dismiss();
        } else {
            tvNoDataMessage.setVisibility(View.GONE);
            adapter = new PrevIndivAdapter(getContext(), listStat);
            recyclerView.setAdapter(adapter);
            progressDialog.dismiss();
        }

    }

    private void progressOp() {
        progressDialog = new ProgressDialog(getActivity(), R.style.ProgressColor);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void findId(View view) {
        recyclerView = view.findViewById(R.id.rvPrevMonthIndivId);
        tvMonthName = view.findViewById(R.id.tvPrevMonthIndivStatMonthId);
        tvNoDataMessage = view.findViewById(R.id.tvPrevMonthNoDataFoundId);
    }


}
