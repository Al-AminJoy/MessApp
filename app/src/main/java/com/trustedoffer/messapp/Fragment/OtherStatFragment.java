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
import android.widget.TextView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.trustedoffer.messapp.Adapter.OtherStatAdapter;
import com.trustedoffer.messapp.Constant.StoredValues;
import com.trustedoffer.messapp.Interface.ClickEvent;
import com.trustedoffer.messapp.ModelClass.MemberInfoModelClass;
import com.trustedoffer.messapp.ModelClass.OthersStatModelClass;
import com.trustedoffer.messapp.ModelClass.UserDataModelClass;
import com.trustedoffer.messapp.R;

import java.util.ArrayList;
import java.util.List;

public class OtherStatFragment extends Fragment implements ClickEvent {
    private RecyclerView recyclerView;
    private List<OthersStatModelClass> listStat = new ArrayList<>();
    private List<UserDataModelClass> listData = new ArrayList<>();
    private OtherStatAdapter adapter;
    private double mealRate;
    private ProgressDialog progressDialog;
    private TextView tvMonthName, tvNoDataMessage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_other_stat, container, false);
        findId(view);
        getActivity().setTitle("Everyone's Status");
        tvMonthName.setText(StoredValues.monthName);
        initRecyclerView();
        loadData();
        return view;
    }

    private void loadData() {
        progressOp();
        List<MemberInfoModelClass> list = new ArrayList<>();
        list = StoredValues.memberInfo;
        if (list.size() == 0) {
            tvNoDataMessage.setText("No Data Found");
            tvNoDataMessage.setVisibility(View.VISIBLE);
            progressDialog.dismiss();
        } else {
            tvNoDataMessage.setVisibility(View.GONE);
            listData = StoredValues.messThisMonthData;

            // int listSize= list.size();

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
                mealRate = StoredValues.mealRate;
                used = (mealRate * totalMeal);
                getBack = (userDebit - used);
                if (totalMeal > 0 || userDebit > 0) {
                    OthersStatModelClass dataSet = new OthersStatModelClass(userName, userEmail, totalMeal, userDebit, used, getBack);
                    listStat.add(dataSet);
                }


            }
            adapter = new OtherStatAdapter(getContext(), listStat);
            recyclerView.setAdapter(adapter);
            progressDialog.dismiss();
            //Click Event Should Set After Set Adapter
            adapter.setClickEvent(OtherStatFragment.this);
        }

    }

    private void progressOp() {
        progressDialog = new ProgressDialog(getActivity(), R.style.ProgressColor);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void initRecyclerView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void findId(View view) {
        recyclerView = view.findViewById(R.id.rvOtherStatId);
        tvMonthName = view.findViewById(R.id.tvOtherStatMonthNameId);
        tvNoDataMessage = view.findViewById(R.id.tvOtherStatNoDataFoundId);
    }

    @Override
    public void clickEventItem(String data) {
        Bundle bundle = new Bundle();
        bundle.putString("test", data);
        OtherStatDetailsDailyOverviewFragment fragment = new OtherStatDetailsDailyOverviewFragment();
        fragment.setArguments(bundle);
        getFragmentManager().beginTransaction().replace(R.id.frameMainActivityId, fragment).commit();
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
