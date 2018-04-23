package com.zoctan.smarthub.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.zoctan.smarthub.R;
import com.zoctan.smarthub.model.bean.smart.HubBean;
import com.zoctan.smarthub.presenter.BasePresenter;
import com.zoctan.smarthub.ui.adapter.HubDetailViewPagerAdapter;
import com.zoctan.smarthub.ui.base.BaseActivity;
import com.zoctan.smarthub.ui.custom.TitleBar;
import com.zoctan.smarthub.ui.fragment.HubDetailNowFragment;
import com.zoctan.smarthub.ui.fragment.HubDetailSpareFragment;
import com.zoctan.smarthub.ui.fragment.HubDetailTimerFragment;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import eu.long1.spacetablayout.SpaceTabLayout;

public class HubDetailActivity extends BaseActivity {
    @BindView(R.id.TitleBar_hub_detail)
    TitleBar mTitleBar;
    @BindView(R.id.ViewPager_hub_detail)
    ViewPager mViewPager;
    @BindView(R.id.SpaceTabLayout_hub_detail)
    SpaceTabLayout mTabLayout;
    protected HubBean hubBean = new HubBean();

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
            hubBean.setRoom(bundle.getString("hub_room"));
            hubBean.setIs_electric(bundle.getBoolean("hub_is_electric"));
            hubBean.setConnected(bundle.getBoolean("hub_connected"));
        }

        super.onCreate(savedInstanceState);
        final List<Fragment> mFragmentList = Arrays.asList(
                HubDetailSpareFragment.newInstance(hubBean),
                HubDetailNowFragment.newInstance(hubBean),
                HubDetailTimerFragment.newInstance(hubBean)
        );
        mViewPager.setAdapter(new HubDetailViewPagerAdapter(getSupportFragmentManager(), mFragmentList));
        //we need the savedInstanceState to get the position
        mTabLayout.initialize(mViewPager, getSupportFragmentManager(), mFragmentList, savedInstanceState);
        mTabLayout.setOnClickListener(view -> mViewPager.setCurrentItem(mTabLayout.getCurrentPosition()));
    }

    @Override
    protected void initView() {
        mTitleBar.setCenterText(hubBean.getName());
        setSupportActionBar(mTitleBar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mTitleBar.setNavigationOnClickListener(v -> onBackPressed());
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        mTabLayout.saveState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, android.R.anim.slide_out_right);
    }
}