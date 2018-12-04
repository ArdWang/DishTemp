package com.dt.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.dt.ui.fragment.ChartFragment;
import com.dt.ui.fragment.TempFragment;

import java.util.List;

public class DataVpAdapter extends FragmentPagerAdapter {
    private List<String> titles;

    public DataVpAdapter(FragmentManager fm, List<String> titles) {
        super(fm);
        this.titles = titles;
    }

    @Override
    public Fragment getItem(int position) {
        if(position==0){
            return new TempFragment();
        }
        return new ChartFragment();
    }

    @Override
    public int getCount() {
        return titles.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }
}
