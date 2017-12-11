package com.zoctan.smarthub.hub.widget;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zoctan.smarthub.R;
import com.zoctan.smarthub.beans.HubBean;
import com.zoctan.smarthub.hub.HubListAdapter;
import com.zoctan.smarthub.hub.presenter.HubListPresenter;
import com.zoctan.smarthub.hub.view.HubListView;
import com.zoctan.smarthub.utils.SPUtils;
import com.zoctan.smarthub.utils.ToastUtils;
import com.zoctan.smarthub.zxing.activity.CaptureActivity;

import java.util.ArrayList;
import java.util.List;

public class HubListFragment extends Fragment implements HubListView, SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView mRecyclerView;
    private HubListAdapter mAdapter;
    private HubListPresenter mHubListPresenter;
    private List<HubBean> mData;
    private SwipeRefreshLayout mSwipeRefreshWidget;
    private SPUtils mSPUtils;

    //打开扫描界面请求码
    private int REQUEST_CODE = 0x01;
    //扫描成功返回码
    private int RESULT_OK = 0xA1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mHubListPresenter = new HubListPresenter(this);
        @SuppressLint("InflateParams") final View view = inflater.inflate(R.layout.fragment_hub, null);

        // 下拉刷新组件SwipeRefreshLayout
        mSwipeRefreshWidget = view.findViewById(R.id.mSwipeRefreshLayoutHub);
        // 设置刷新时动画的颜色，可以设置4个
        mSwipeRefreshWidget.setColorSchemeResources(R.color.primary, R.color.divider, R.color.lime, R.color.accent);
        // 下拉刷新监听
        mSwipeRefreshWidget.setOnRefreshListener(this);

        // RecyclerView setup
        mRecyclerView = view.findViewById(R.id.mRecyclerViewHub);
        // 固定RecyclerView大小
        mRecyclerView.setHasFixedSize(true);
        // 设置布局管理器
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        // 设置item动画
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        // adapter relative
        mAdapter = new HubListAdapter(getActivity().getApplicationContext());
        mAdapter.setOnItemClickListener(mOnItemClickListener);
        mRecyclerView.setAdapter(mAdapter);

        FloatingActionButton mFloatingActionButton = view.findViewById(R.id.mFloatingActionButton);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CaptureActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
                ToastUtils.showShort(getContext(), "^_^");
            }
        });

        mSPUtils = new SPUtils(getContext());
        onRefresh();
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //扫描结果回调
        if (resultCode == RESULT_OK) { //RESULT_OK = -1
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString("qr_scan_result");
            //将扫描出的信息显示出来
            View view = getActivity() == null ? mRecyclerView.getRootView() : getActivity().findViewById(R.id.mDrawerLayout);
            Snackbar.make(view, scanResult, Snackbar.LENGTH_SHORT).show();

        }
    }

    private HubListAdapter.OnItemClickListener mOnItemClickListener = new HubListAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            if (mData.size() <= 0) {
                return;
            }
            HubBean hub = mAdapter.getItem(position);
            mSPUtils.putString("hubName", hub.getName());
            Intent intent = new Intent();
            intent.setAction("hubDetail");
            intent.addCategory("hub");
            startActivity(intent);
        }
    };

    @Override
    public void onRefresh() {
        if (mData != null) {
            mData.clear();
        }
        if (mSPUtils.getBoolean("Login")) {
            mHubListPresenter.loadHubList(mSPUtils.getString("userPassword"));
        } else {
            View view = getActivity() == null ? mRecyclerView.getRootView() : getActivity().findViewById(R.id.mDrawerLayout);
            Snackbar.make(view, getString(R.string.needLogin), Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void addHub(List<HubBean> hubList) {
        if (mData == null) {
            mData = new ArrayList<>();
        }
        if (hubList != null) {
            mData.addAll(hubList);
        }
        mAdapter.setData(mData);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void hideLoading() {
        mSwipeRefreshWidget.setRefreshing(false);
    }

    @Override
    public void showLoading() {
        mSwipeRefreshWidget.setRefreshing(true);
    }

    @Override
    public void showLoadingFailedMsg() {
        View view = getActivity() == null ? mRecyclerView.getRootView() : getActivity().findViewById(R.id.mDrawerLayout);
        Snackbar.make(view, getString(R.string.load_fail), Snackbar.LENGTH_SHORT).show();
    }
}
