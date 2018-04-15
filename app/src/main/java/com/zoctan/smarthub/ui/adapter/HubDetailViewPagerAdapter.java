package com.zoctan.smarthub.ui.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.zoctan.smarthub.model.bean.smart.HubBean;

public class HubDetailViewPagerAdapter extends FragmentPagerAdapter {
    private final Fragment[] mList;
    private final HubBean hub;

    public HubDetailViewPagerAdapter(final FragmentManager fragmentManager, final Fragment[] fragments, final HubBean hubBean) {
        super(fragmentManager);
        mList = fragments;
        hub = hubBean;
    }

    @Override
    public Fragment getItem(final int position) {
        final Bundle bundle = new Bundle();
        bundle.putString("hub_name", hub.getName());
        bundle.putString("hub_onenet_id", hub.getOnenet_id());
        bundle.putBoolean("hub_is_electric", hub.getIs_electric());
        bundle.putBoolean("hub_connected", hub.getConnected());
        mList[position].setArguments(bundle);
        return mList[position];
    }

    @Override
    public int getCount() {
        return mList.length;
    }
}