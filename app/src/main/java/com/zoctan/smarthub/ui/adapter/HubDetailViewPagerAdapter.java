package com.zoctan.smarthub.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

public class HubDetailViewPagerAdapter extends FragmentPagerAdapter {
    private final List<Fragment> mFragmentList;

    public HubDetailViewPagerAdapter(final FragmentManager fragmentManager, final List<Fragment> mFragmentList) {
        super(fragmentManager);
        this.mFragmentList = mFragmentList;
    }

    @Override
    public Fragment getItem(final int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }
}