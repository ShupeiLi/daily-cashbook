package com.example.daily_cashbook.fragment;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

class KeepAccountAdapter extends FragmentPagerAdapter {

    Context context;
    int totalTabs;
    public KeepAccountAdapter(Context c, FragmentManager fm, int totalTabs) {
        super(fm);
        context = c;
        this.totalTabs = totalTabs;
    }
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                KeepAccountIncomeFragment keepAccountIncomeFragmentFragment = new KeepAccountIncomeFragment();
                return keepAccountIncomeFragmentFragment;
            case 1:
                KeepAccountExpenditureFragment keepAccountExpenditureFragment = new KeepAccountExpenditureFragment();
                return keepAccountExpenditureFragment;
            default:
                return null;
        }
    }
    @Override
    public int getCount() {
        return totalTabs;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "支出";
            case 1:
                return "收入";
        }
        return null;
    }

}
