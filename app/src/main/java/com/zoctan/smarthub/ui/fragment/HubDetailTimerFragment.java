package com.zoctan.smarthub.ui.fragment;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.utilcode.util.StringUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.zoctan.smarthub.R;
import com.zoctan.smarthub.model.bean.smart.HubBean;
import com.zoctan.smarthub.model.bean.smart.TimerBean;
import com.zoctan.smarthub.presenter.BasePresenter;
import com.zoctan.smarthub.presenter.HubDetailTimerPresenter;
import com.zoctan.smarthub.ui.adapter.HubDetailTimerListAdapter;
import com.zoctan.smarthub.ui.base.BaseFragment;
import com.zoctan.smarthub.ui.custom.MyTextWatcher;
import com.zyao89.view.zloading.ZLoadingView;

import java.util.ArrayList;
import java.util.Calendar;
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
    protected static HubBean hubBean;

    public static HubDetailTimerFragment newInstance(final HubBean hub) {
        final Bundle args = new Bundle();
        final HubDetailTimerFragment fragment = new HubDetailTimerFragment();
        fragment.setArguments(args);
        hubBean = hub;
        return fragment;
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
                    new MaterialDialog.Builder(getHoldingActivity())
                            .title(R.string.timer_delete)
                            .iconRes(R.drawable.ic_alert)
                            .negativeText(R.string.all_cancel)
                            .positiveText(R.string.all_ensure)
                            .onPositive((dialog, which) -> {
                                mPresenter.crudTimer(timer, "delete");
                                dialog.dismiss();
                            }).show();
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
        final TextInputEditText[] mEtTimerName = new TextInputEditText[1];
        final TextInputLayout[] mLayoutTimerName = new TextInputLayout[1];
        // 下拉菜单
        final Spinner mSpinnerOpenClose;
        final Spinner mSpinnerRepeat;
        // 时间选择器
        final TimePicker[] mTimePicker = new TimePicker[1];

        final MaterialDialog dialog = new MaterialDialog.Builder(getHoldingActivity())
                .title(R.string.hub_detail_timer_setting)
                .iconRes(R.drawable.ic_edit)
                .customView(R.layout.dialog_timer, true)
                .negativeText(R.string.all_cancel)
                .positiveText(R.string.all_ensure)
                .onPositive((_dialog, which) -> {
                    String name = mEtTimerName[0].getText().toString();
                    if (StringUtils.isEmpty(name)) {
                        this.showFailedMsg("定时器名称不能为空");
                        return;
                    }
                    if (mEtTimerName[0].getError() == null) {
                        // 补零
                        final String hour1 = String.format(Locale.CHINA, "%02d", mTimePicker[0].getCurrentHour());
                        final String minute1 = String.format(Locale.CHINA, "%02d", mTimePicker[0].getCurrentMinute());
                        timer.setTime(String.format("%s:%s", hour1, minute1));
                        timer.setName(name);
                        mPresenter.crudTimer(timer, action);
                        _dialog.dismiss();
                    }
                })
                .build();

        final View view = dialog.getCustomView();
        if (view != null) {
            mEtTimerName[0] = view.findViewById(R.id.EditText_timer_name);
            mLayoutTimerName[0] = view.findViewById(R.id.TextInputLayout_timer_name);
            mEtTimerName[0].setText(timer.getName());
            mEtTimerName[0].setSelection(mEtTimerName[0].getText().length());
            mEtTimerName[0].addTextChangedListener(new MyTextWatcher() {
                @Override
                public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
                    if (s.length() > 12) {
                        mLayoutTimerName[0].setErrorEnabled(true);
                        mEtTimerName[0].setError(getString(R.string.all_max_name));
                    } else {
                        mEtTimerName[0].setError(null);
                    }
                }
            });

            timer.setPower(timer.getPower());
            // 下拉菜单
            mSpinnerOpenClose = view.findViewById(R.id.Spinner_timer_open_close);
            mSpinnerOpenClose.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(final AdapterView<?> parent, final View view, final int pos, final long id) {
                    timer.setPower(pos);
                }

                @Override
                public void onNothingSelected(final AdapterView<?> parent) {
                    timer.setPower(0);
                }
            });

            timer.setRepeat(timer.getRepeat());
            final String[] repeat = getResources().getStringArray(R.array.timer_repeat);
            mSpinnerRepeat = view.findViewById(R.id.Spinner_timer_repeat);
            mSpinnerRepeat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(final AdapterView<?> parent, final View view, final int pos, final long id) {
                    timer.setRepeat(repeat[pos]);
                }

                @Override
                public void onNothingSelected(final AdapterView<?> parent) {
                    timer.setRepeat(repeat[0]);
                }
            });

            mTimePicker[0] = view.findViewById(R.id.TimePicker_timer);
            mTimePicker[0].setIs24HourView(true);
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
            mTimePicker[0].setCurrentHour(hour);
            mTimePicker[0].setCurrentMinute(minute);
        }

        dialog.show();
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
}