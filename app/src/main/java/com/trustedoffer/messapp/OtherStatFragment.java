package com.trustedoffer.messapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class OtherStatFragment extends Fragment {
    private CardView cvOtherStat;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_other_stat, container, false);
        findId(view);

        cvOtherStat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 Fragment fragment=new OtherStatusDetailsFragment();
                getFragmentManager().beginTransaction()
                        .replace(R.id.frameMainActivityId,fragment)
                        .commit();

            }
        });
        return view;
    }

    private void findId(View view) {
        cvOtherStat=view.findViewById(R.id.cvOtherStat);
    }
}
