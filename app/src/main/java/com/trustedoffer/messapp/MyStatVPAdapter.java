package com.trustedoffer.messapp;



import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class MyStatVPAdapter extends FragmentPagerAdapter {

    private final ArrayList<Fragment> fragmentList=new ArrayList<>();
    private final ArrayList<String> fragmentTitle=new ArrayList<>();

    public MyStatVPAdapter(FragmentManager fm) {
        super(fm);
    }


    @Override
    public Fragment getItem(int i) {
        return fragmentList.get(i);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentTitle.get(position);
    }

    @Override
    public int getCount() {
        return fragmentTitle.size();
    }
    public void AddFragment(Fragment fragment, String title)
    {
        fragmentList.add(fragment);
        fragmentTitle.add(title);
    }
}
