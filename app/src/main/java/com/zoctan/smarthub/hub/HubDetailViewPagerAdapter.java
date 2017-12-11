package com.zoctan.smarthub.hub;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

public class HubDetailViewPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> mList;
    private String[] mStrings;

    /**
     * @param fragmentManager Fragment管理器
     * @param list Fragment列表
     * @param titles 页面标题
     */
    public HubDetailViewPagerAdapter(FragmentManager fragmentManager, List<Fragment> list, String[] titles) {
        super(fragmentManager);
        mList = list;
        mStrings = titles;
    }

    @Override
    public Fragment getItem(int position) {
        return mList.get(position);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mStrings == null ? super.getPageTitle(position) : mStrings[position];
    }
}
