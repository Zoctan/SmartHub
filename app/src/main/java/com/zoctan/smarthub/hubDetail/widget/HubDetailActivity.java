package com.zoctan.smarthub.hubDetail.widget;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.zoctan.smarthub.R;
import com.zoctan.smarthub.base.BaseActivity;
import com.zoctan.smarthub.beans.HubBean;

import java.util.ArrayList;

import butterknife.BindView;

public class HubDetailActivity extends BaseActivity {
    @BindView(R.id.Toolbar_all)
    Toolbar mToolbar;
    @BindView(R.id.ViewPager_hub_detail)
    ViewPager mViewPager;
    @BindView(R.id.CommonTabLayout_hub_detail)
    CommonTabLayout mTabLayout;
    protected HubBean hubBean = new HubBean();

    private final Fragment[] mFragmentList = new Fragment[]{
            HubDetailNowFragment.newInstance(),
            HubDetailSpareFragment.newInstance(),
            HubDetailTimerFragment.newInstance()};
    private final int[] mTabTextList = new int[]{
            R.string.hub_detail_real_time_info,
            R.string.hub_detail_energy_used,
            R.string.hub_detail_timer};
    private final int[] mIconUnSelectIds = {
            R.drawable.ic_realtime,
            R.drawable.ic_line_chart,
            R.drawable.ic_timer};
    private final int[] mIconSelectIds = {
            R.drawable.ic_realtime_select,
            R.drawable.ic_line_chart_select,
            R.drawable.ic_timer_select};

    @Override
    protected int bindLayout() {
        return R.layout.activity_hub_detail;
    }

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        final Intent intent = getIntent();
        final Bundle bundle = intent.getExtras();
        if (bundle != null) {
            hubBean.setName(bundle.getString("hub_name"));
            hubBean.setOnenet_id(bundle.getString("hub_onenet_id"));
            hubBean.setIs_electric(bundle.getBoolean("hub_is_electric"));
            hubBean.setConnected(bundle.getBoolean("hub_connected"));
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        mToolbar.setTitle(hubBean.getName());
        setSupportActionBar(mToolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                onBackPressed();
            }
        });

        mViewPager.setAdapter(new HubDetailViewPagerAdapter(getSupportFragmentManager(), mFragmentList, hubBean));

        final ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
        for (int i = 0; i < mTabTextList.length; i++) {
            mTabEntities.add(new TabEntity(getString(mTabTextList[i]), mIconSelectIds[i], mIconUnSelectIds[i]));
        }
        mTabLayout.setTabData(mTabEntities);
        setListener();
    }

    private void setListener() {
        mTabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(final int position) {
                mViewPager.setCurrentItem(position);
            }

            @Override
            public void onTabReselect(final int position) {
            }
        });
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(final int position) {
                mTabLayout.setCurrentTab(position);
            }

            @Override
            public void onPageScrollStateChanged(final int state) {
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, android.R.anim.slide_out_right);
    }
}

class HubDetailViewPagerAdapter extends FragmentPagerAdapter {
    private final Fragment[] mList;
    private final HubBean hub;

    HubDetailViewPagerAdapter(final FragmentManager fragmentManager, final Fragment[] fragments, final HubBean hubBean) {
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

class TabEntity implements CustomTabEntity {
    private final String title;
    private final int selectedIcon;
    private final int unSelectedIcon;

    TabEntity(final String title, final int selectedIcon, final int unSelectedIcon) {
        this.title = title;
        this.selectedIcon = selectedIcon;
        this.unSelectedIcon = unSelectedIcon;
    }

    @Override
    public String getTabTitle() {
        return title;
    }

    @Override
    public int getTabSelectedIcon() {
        return selectedIcon;
    }

    @Override
    public int getTabUnselectedIcon() {
        return unSelectedIcon;
    }
}

