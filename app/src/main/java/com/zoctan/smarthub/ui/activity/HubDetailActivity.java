package com.zoctan.smarthub.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.zoctan.smarthub.R;
import com.zoctan.smarthub.model.bean.smart.HubBean;
import com.zoctan.smarthub.presenter.BasePresenter;
import com.zoctan.smarthub.ui.adapter.HubDetailViewPagerAdapter;
import com.zoctan.smarthub.ui.base.BaseActivity;
import com.zoctan.smarthub.ui.custom.TitleBar;
import com.zoctan.smarthub.ui.fragment.HubDetailNowFragment;
import com.zoctan.smarthub.ui.fragment.HubDetailSpareFragment;
import com.zoctan.smarthub.ui.fragment.HubDetailTimerFragment;
import com.zoctan.smarthub.ui.tab.TabEntity;

import java.util.ArrayList;

import butterknife.BindView;

public class HubDetailActivity extends BaseActivity {
    @BindView(R.id.TitleBar_hub_detail)
    TitleBar mTitleBar;
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
    protected BasePresenter bindPresenter() {
        return null;
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
        initToolBar();
        mViewPager.setAdapter(new HubDetailViewPagerAdapter(getSupportFragmentManager(), mFragmentList, hubBean));
        initData();
        setListener();
    }

    private void initData() {
        final ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
        for (int i = 0; i < mTabTextList.length; i++) {
            mTabEntities.add(new TabEntity(getString(mTabTextList[i]), mIconSelectIds[i], mIconUnSelectIds[i]));
        }
        mTabLayout.setTabData(mTabEntities);
    }

    private void initToolBar() {
        mTitleBar.setCenterText(hubBean.getName());
        setSupportActionBar(mTitleBar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mTitleBar.setNavigationOnClickListener(v -> onBackPressed());
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