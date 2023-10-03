package com.macroyau.blue2serial.demo.pagerAdapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.macroyau.blue2serial.demo.fragment.UserAccountFragment1;


public class UserAccountPageAdapter extends FragmentPagerAdapter {

    public UserAccountPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                UserAccountFragment1 tab1 = new UserAccountFragment1();
                return tab1;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 1;
    }
}