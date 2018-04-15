package com.zoctan.smarthub.ui.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TimePicker;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.zoctan.smarthub.R;
import com.zoctan.smarthub.model.bean.smart.HubBean;
import com.zoctan.smarthub.model.bean.smart.TimerBean;
import com.zoctan.smarthub.presenter.BasePresenter;
import com.zoctan.smarthub.presenter.HubDetailTimerPresenter;
import com.zoctan.smarthub.ui.adapter.HubDetailTimerListAdapter;
import com.zoctan.smarthub.ui.base.BaseFragment;
import com.zoctan.smarthub.utils.AlerterUtil;
import com.zoctan.smarthub.utils.NiftyDialog;
import com.zoctan.smarthub.utils.NiftyDialogUtil;
import com.zyao89.view.zloading.ZLoadingView;

import org.angmarch.views.NiceSpinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;

public class HubDetailTimerFragment extends BaseFragment {
    @BindView(R.id.RecyclerView_hub_detail_timer)
    RecyclerView mRecyclerView;
    @BindView(R.id.SmartRefreshLayout_timer_list)
    SmartRefreshLayout mSmartRefreshLayout;
    @BindView(R.id.ZLoadingView_hub_detail_timer)
    ZLoadingView zLoadingView;
    private final Calendar calendar = Calendar.getInstance();
    private final HubDetailTimerPresenter mPresenter = new HubDetailTimerPresenter(this);
    private HubDetailTimerListAdapter mAdapter;
    private List<TimerBean> mData;
    protected HubBean hubBean;

    public static HubDetailTimerFragment newInstance() {
        return new HubDetailTimerFragment();
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            hubBean = new HubBean();
            hubBean.setName(getArguments().getString("hub_name"));
            hubBean.setOnenet_id(getArguments().getString("hub_onenet_id"));
            hubBean.setIs_electric(getArguments().getBoolean("hub_is_electric"));
            hubBean.setConnected(getArguments().getBoolean("hub_connected"));
        }
    }

    @Override
    protected int bindLayout() {
        return R.layout.fragment_hub_detail_timer;
    }

    @Override
    protected BasePresenter bindPresenter() {
        return mPresenter;
    }

    private final HubDetailTimerListAdapter.OnItemClickListener mOnItemClickListener = new HubDetailTimerListAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(final String action, final View view, final int position) {
            if (mData.size() <= 0) {
                return;
            }
            final TimerBean timer = mAdapter.getItem(position);
            switch (action) {
                case "update":
                    showTimerDialog(timer, "update");
                    break;
                case "close":
                case "open":
                    timer.setStatus(action.equals("close") ? 0 : 1);
                    mPresenter.crudTimer(timer, "update");
                    break;
                case "delete":
                    mPresenter.crudTimer(timer, "delete");
                    break;
            }
        }
    };

    @Override
    protected void initView(final View view, final Bundle savedInstanceState) {
        mRecyclerView.setHasFixedSize(true);
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getHoldingActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mAdapter = new HubDetailTimerListAdapter();
        mAdapter.setOnItemClickListener(mOnItemClickListener);
        mRecyclerView.setAdapter(mAdapter);
        setSmartRefresh();
        refreshTimerList();
    }

    private void setSmartRefresh() {
        mSmartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            refreshTimerList();
            refreshLayout.finishRefresh(2000/*,false*/);//传入false表示刷新失败
        });
        mSmartRefreshLayout.setOnLoadMoreListener(refreshLayout -> refreshLayout.finishLoadMore(2000/*,false*/));//传入false表示加载失败
    }

    @OnClick(R.id.FloatingActionButton_timer_list)
    public void addTimer() {
        final TimerBean timer = new TimerBean();
        timer.setHub_id(hubBean.getOnenet_id());
        timer.setPower(0);
        timer.setRepeat("每天");
        showTimerDialog(timer, "add");
    }

    private void showTimerDialog(final TimerBean timer, final String action) {
        @SuppressLint("InflateParams") final View view = getLayoutInflater().inflate(R.layout.dialog_timer, null);

        final TextInputEditText mEtTimerName = view.findViewById(R.id.EditText_timer_name);
        final TextInputLayout mLayoutTimerName = view.findViewById(R.id.TextInputLayout_timer_name);

        mEtTimerName.setText(timer.getName());
        mEtTimerName.setSelection(mEtTimerName.getText().length());
        mEtTimerName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(final CharSequence charSequence, final int i, final int i1, final int i2) {
            }

            @Override
            public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
                if (s.length() > 12) {
                    mLayoutTimerName.setErrorEnabled(true);
                    mEtTimerName.setError(getString(R.string.all_max_name));
                } else {
                    mEtTimerName.setError(null);
                }
            }

            @Override
            public void afterTextChanged(final Editable editable) {
            }
        });
        // 下拉菜单
        final NiceSpinner mSpinnerOpenClose = view.findViewById(R.id.NiceSpinner_timer_open_close);
        final LinkedList<String> openCloseList = new LinkedList<>(Arrays.asList("定时关机", "定时开机"));
        timer.setPower(timer.getPower());
        mSpinnerOpenClose.attachDataSource(openCloseList);
        mSpinnerOpenClose.addOnItemClickListener((adapterView, view1, i, l) -> {
            if (openCloseList.get(i).equals("定时关机")) {
                timer.setPower(0);
            } else {
                timer.setPower(1);
            }
        });

        final NiceSpinner mSpinnerRepeat = view.findViewById(R.id.NiceSpinner_timer_repeat);
        final LinkedList<String> repeatList = new LinkedList<>(Arrays.asList("每天", "每周1-5", "一次性"));
        timer.setRepeat(timer.getRepeat());
        mSpinnerRepeat.attachDataSource(repeatList);
        mSpinnerRepeat.addOnItemClickListener((adapterView, v, i, l) -> timer.setRepeat(repeatList.get(i)));

        // 时间选择器
        final TimePicker mTimePicker = view.findViewById(R.id.TimePicker_timer);
        mTimePicker.setIs24HourView(true);
        final int hour;
        final int minute;
        if (timer.getTime() != null) {
            hour = Integer.parseInt(timer.getTime().split(":")[0]);
            minute = Integer.parseInt(timer.getTime().split(":")[1]);
        } else {
            calendar.setTimeInMillis(System.currentTimeMillis());
            hour = calendar.get(Calendar.HOUR_OF_DAY);
            minute = Calendar.MINUTE;
        }
        mTimePicker.setHour(hour);
        mTimePicker.setMinute(minute);

        final NiftyDialog dialog = new NiftyDialogUtil(getHoldingActivity())
                .init(R.string.hub_detail_timer_setting,
                        null,
                        R.drawable.ic_edit,
                        R.string.all_ensure);
        dialog
                .setCustomView(view, getHoldingActivity())
                .setButton1Click(v -> {
                    if (mEtTimerName.getText().length() > 0
                            && mLayoutTimerName.getError() == null) {
                        getHoldingActivity().hideSoftKeyBoard(mEtTimerName, getContext());
                        // 补零
                        final String hour1 = String.format(Locale.CHINA, "%02d", mTimePicker.getHour());
                        final String minute1 = String.format(Locale.CHINA, "%02d", mTimePicker.getMinute());
                        timer.setTime(String.format("%s:%s", hour1, minute1));
                        timer.setName(mEtTimerName.getText().toString());
                        mPresenter.crudTimer(timer, action);
                        dialog.dismiss();
                    }
                })
                .show();
    }

    public void refreshTimerList() {
        if (mData != null) {
            mData.clear();
        }
        mPresenter.listTimer(hubBean.getOnenet_id());
    }

    public void loadTimerList(final List<TimerBean> timerList) {
        mData = new ArrayList<>();
        if (timerList != null) {
            mData.addAll(timerList);
            mAdapter.setData(mData);
        }
        mAdapter.notifyDataSetChanged();
    }

    public void showLoading() {
        zLoadingView.setVisibility(View.VISIBLE);
    }

    public void hideLoading() {
        zLoadingView.setVisibility(View.GONE);
    }

    public void showSuccessMsg(final String msg) {
        AlerterUtil.showInfo(getHoldingActivity(), msg);
        refreshTimerList();
    }

    public void showFailedMsg(final String msg) {
        AlerterUtil.showDanger(getHoldingActivity(), msg);
    }
}