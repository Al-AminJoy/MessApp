package com.trustedoffer.messapp.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.trustedoffer.messapp.Adapter.ViewPagerAdapter;
import com.trustedoffer.messapp.R;

public class PreviousMonthFragment extends Fragment {
    private TabLayout tlPrevMonth;
    private ViewPager vpPrevMonth;
    private ViewPagerAdapter vpPrevMonthAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_previous_month, container, false);
        findId(view);
        getActivity().setTitle("Last Month Status");
        vpPrevMonthAdapter = new ViewPagerAdapter(getChildFragmentManager());
        vpPrevMonthAdapter.AddFragment(new PrevMonthTotalOverviewFragment(), "Total Overview");
        vpPrevMonthAdapter.AddFragment(new PrevMonthIndividualOverviewFragment(), "Individual Overview");
        vpPrevMonth.setAdapter(vpPrevMonthAdapter);
        tlPrevMonth.setupWithViewPager(vpPrevMonth);
        return view;
    }

    private void findId(View view) {
        tlPrevMonth = view.findViewById(R.id.tlPrevMonthStatId);
        vpPrevMonth = view.findViewById(R.id.vpPrevMonthStatId);
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
