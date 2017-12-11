package com.zoctan.smarthub.hub.widget;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

import com.orhanobut.logger.Logger;
import com.zoctan.smarthub.R;
import com.zoctan.smarthub.beans.HubBean;
import com.zoctan.smarthub.hub.HubDetailViewPagerAdapter;
import com.zoctan.smarthub.hub.presenter.HubDetailPresenter;
import com.zoctan.smarthub.hub.view.HubDetailView;
import com.zoctan.smarthub.utils.ActivityCollector;
import com.zoctan.smarthub.utils.SPUtils;
import com.zoctan.smarthub.utils.SwipeBackActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import me.imid.swipebacklayout.lib.SwipeBackLayout;

public class HubDetailActivity extends SwipeBackActivity implements HubDetailView {

    private HubBean mHub;
    private HubDetailPresenter mHubDetailPresenter;
    private ProgressBar mPbLoading;
    private SwipeBackLayout mSwipeBackLayout;
    private Toolbar mToolbar;
    private SPUtils mSPUtils;
    private TabLayout mTabLayout;
    private List<Fragment> mFragmentList;
    private HubDetailViewPagerAdapter mHubDetailViewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 如果为日间模式
        mSPUtils = new SPUtils(this);
        if (Objects.equals(mSPUtils.getString("toggle"), "day")) {
            // 日间
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            // 将mSwitch置为false, 夜间
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        // 设置评测详情要显示的视图
        setContentView(R.layout.activity_hub_detail);

        // 初始化控件
        initView();

        // 将该Activity添加到ActivityCollector管理器中
        ActivityCollector.addActivity(this);
    }

    // 初始化控件
    private void initView() {
        mToolbar = (Toolbar) this.findViewById(R.id.mToolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setTitle(mSPUtils.getString("hubName"));
        Logger.d(mSPUtils.getString("hubName"));
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        // 获得SwipeBackLayout对象
        mSwipeBackLayout = getSwipeBackLayout();
        // 滑动删除的效果只能从边界滑动才有效果，如果要扩大touch的范围，可以调用
        //mSwipeBackLayout.setEdgeSize(this.getResources().getDisplayMetrics().widthPixels);
        //mSwipeBackLayout.setEdgeSize(int size);
        // 设定从左边可以滑动,EDGE_ALL表示向下、左、右滑动均可EDGE_LEFT，EDGE_RIGHT，EDGE_BOTTOM
        mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);

        ViewPager mViewPager = (ViewPager) findViewById(R.id.mViewPager);
        mTabLayout = (TabLayout) findViewById(R.id.mTabLayout);

        mFragmentList = new ArrayList<>();
        Fragment mHubDetailNowFragment = HubDetailNowFragment.newInstance(0);
        Fragment mHubDetailSpareFragment = HubDetailSpareFragment.newInstance(1);
        Fragment mHubDetailTimerFragment = HubDetailTimerFragment.newInstance(2);
        mFragmentList.add(mHubDetailNowFragment);
        mFragmentList.add(mHubDetailSpareFragment);
        mFragmentList.add(mHubDetailTimerFragment);
        String[] mTabTextList = new String[]{getString(R.string.tabNow), getString(R.string.tabSpare), getString(R.string.tabTimer)};

        mHubDetailViewPagerAdapter = new HubDetailViewPagerAdapter(getSupportFragmentManager(), mFragmentList, mTabTextList);
        mViewPager.setAdapter(mHubDetailViewPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public void showLoading() {
        // Loading圈圈设置成可见
        mPbLoading.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        // 移除Loading圈圈
        mPbLoading.setVisibility(View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 从管理器中移除该Activity
        ActivityCollector.removeActivity(this);
    }
}
