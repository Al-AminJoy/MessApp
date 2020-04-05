package com.trustedoffer.messapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

public class MyStatFragment extends Fragment {
    private TabLayout tlMyStat;
    private TabItem tiTotalOverview,tiDailyOverview;
    private ViewPager vpMyStat;
    private MyStatVPAdapter myStatVPAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_my_stat, container, false);
        findId(view);

        myStatVPAdapter=new MyStatVPAdapter(getChildFragmentManager());
        myStatVPAdapter.AddFragment( new MyStatTotalOverViewFragment(),"Total Overview");
        myStatVPAdapter.AddFragment(new MyStatDailyOverViewFragment(),"Daily Overview");
        vpMyStat.setAdapter(myStatVPAdapter);
        tlMyStat.setupWithViewPager(vpMyStat);
        return view;
    }

    private void findId(View view) {
        tlMyStat=view.findViewById(R.id.tlMyStatId);
        tiTotalOverview=view.findViewById(R.id.tiMyStatusTotalOverviewId);
        tiDailyOverview=view.findViewById(R.id.tiMyStatusDailyOverviewId);
        vpMyStat=view.findViewById(R.id.vpMyStatId);

    }
}
