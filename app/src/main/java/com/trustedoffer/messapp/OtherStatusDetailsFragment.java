package com.trustedoffer.messapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;

public class OtherStatusDetailsFragment extends Fragment {
    private TabLayout tlOtherStatDetails;
    private ViewPager vpOtherStatDetais;
    private ViewPagerAdapter vpAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_other_status_details, container, false);
        findId(view);


        vpAdapter=new ViewPagerAdapter(getChildFragmentManager());
        vpAdapter.AddFragment(new OtherStatDetailsTotalOverviewFragment(),"Total Overview");
        vpAdapter.AddFragment(new OtherStatDetailsDailyOverviewFragment(),"Daily Overview");
        vpOtherStatDetais.setAdapter(vpAdapter);
        tlOtherStatDetails.setupWithViewPager(vpOtherStatDetais);
        return view;
    }

    private void findId(View view) {
        tlOtherStatDetails=view.findViewById(R.id.tlOthersStatDetailsId);
        vpOtherStatDetais=view.findViewById(R.id.vpOthersStatDetailsId);
    }
}
