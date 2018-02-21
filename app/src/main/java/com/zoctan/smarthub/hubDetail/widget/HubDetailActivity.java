package com.zoctan.smarthub.hubDetail.widget;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.zoctan.smarthub.App;
import com.zoctan.smarthub.R;
import com.zoctan.smarthub.base.BaseActivity;

import java.util.ArrayList;

import butterknife.BindView;

public class HubDetailActivity extends BaseActivity {
    @BindView(R.id.Toolbar_all)
    Toolbar mToolbar;
    @BindView(R.id.ViewPager_hub_detail)
    ViewPager mViewPager;
    @BindView(R.id.CommonTabLayout_hub_detail)
    CommonTabLayout mTabLayout;

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
    private final ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();

    @Override
    protected int bindLayout() {
        return R.layout.activity_hub_detail;
    }

    @Override
    protected void initView() {
        mToolbar.setTitle(mSPUtil.getString("hub_name"));
        setSupportActionBar(mToolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        mViewPager.setAdapter(new HubDetailViewPagerAdapter(getSupportFragmentManager(), mFragmentList));
        for (int i = 0; i < mTabTextList.length; i++) {
            mTabEntities.add(new TabEntity(getString(mTabTextList[i]), mIconSelectIds[i], mIconUnSelectIds[i]));
        }
        mTabLayout.setTabData(mTabEntities);
        setListener();
    }

    private void setListener() {
        mTabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                mViewPager.setCurrentItem(position);
            }

            @Override
            public void onTabReselect(int position) {
            }
        });
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mTabLayout.setCurrentTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
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

    HubDetailViewPagerAdapter(FragmentManager fragmentManager, Fragment[] fragments) {
        super(fragmentManager);
        mList = fragments;
    }

    @Override
    public Fragment getItem(int position) {
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

    TabEntity(String title, int selectedIcon, int unSelectedIcon) {
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

