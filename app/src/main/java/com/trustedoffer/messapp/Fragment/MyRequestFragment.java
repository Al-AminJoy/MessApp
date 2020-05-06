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

public class MyRequestFragment extends Fragment {
    private TabLayout tlMyReq;
    private ViewPager vpMyReq;
    private ViewPagerAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_request, container, false);
        findId(view);
        getActivity().setTitle("My Request List");
        adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.AddFragment(new MyDebitReqFragment(), "Expense Request");
        adapter.AddFragment(new MyUpdateReqFragment(), "Update Request");
        vpMyReq.setAdapter(adapter);
        tlMyReq.setupWithViewPager(vpMyReq);
        return view;
    }

    private void findId(View view) {
        tlMyReq = view.findViewById(R.id.tlMyReqId);
        vpMyReq = view.findViewById(R.id.vpMyReqId);

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
