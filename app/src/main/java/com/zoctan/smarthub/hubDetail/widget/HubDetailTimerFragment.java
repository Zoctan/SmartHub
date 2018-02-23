package com.zoctan.smarthub.hubDetail.widget;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.vansuita.library.Icon;
import com.zoctan.smarthub.R;
import com.zoctan.smarthub.base.BaseFragment;
import com.zoctan.smarthub.beans.TimerBean;
import com.zoctan.smarthub.hubDetail.presenter.HubDetailTimerPresenter;
import com.zoctan.smarthub.hubDetail.view.HubDetailTimerView;
import com.zoctan.smarthub.utils.AlerterUtil;
import com.zoctan.smarthub.utils.NiftyDialog;
import com.zoctan.smarthub.utils.NiftyDialogUtil;

import org.angmarch.views.NiceSpinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HubDetailTimerFragment extends BaseFragment implements HubDetailTimerView {

    @BindView(R.id.RecyclerView_hub_detail_timer)
    RecyclerView mRecyclerView;
    @BindView(R.id.SmartRefreshLayout_timer_list)
    SmartRefreshLayout mSmartRefreshLayout;
    private final Calendar calendar = Calendar.getInstance();
    private final HubDetailTimerPresenter mHubDetailPresenter = new HubDetailTimerPresenter(this);
    private HubDetailTimerListAdapter mAdapter;
    private List<TimerBean> mData;
    private final HubDetailTimerListAdapter.OnItemClickListener mOnItemClickListener = new HubDetailTimerListAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(final String action, final View view, final int position) {
            if (mData.size() <= 0) {
                return;
            }
            final TimerBean timer = mAdapter.getItem(position);
            timer.setAction(action);
            switch (action) {
                case "update":
                    showTimerDialog(timer);
                    //ToastUtils.showShort("update");
                    break;
                case "close":
                case "open":
                    timer.setStatus(timer.getAction().equals("close") ? 0 : 1);
                    mHubDetailPresenter.doHubTimer(
                            mSPUtil.getString("user_password"),
                            timer);
                    //ToastUtils.showShort("update");
                    break;
                case "delete":
                    mHubDetailPresenter.doHubTimer(
                            mSPUtil.getString("user_password"),
                            timer);
                    //ToastUtils.showShort("delete");
                    break;
            }
        }
    };

    public static HubDetailTimerFragment newInstance() {
        return new HubDetailTimerFragment();
    }

    @Override
    protected int bindLayout() {
        return R.layout.fragment_hub_detail_timer;
    }

    @Override
    protected void initView(final View view, final Bundle savedInstanceState) {
        mRecyclerView.setHasFixedSize(true);
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mAdapter = new HubDetailTimerListAdapter();
        mAdapter.setOnItemClickListener(mOnItemClickListener);
        mRecyclerView.setAdapter(mAdapter);
        setSmartRefresh();
        refreshTimerList();
    }

    private void setSmartRefresh() {
        mSmartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(final RefreshLayout refreshlayout) {
                refreshTimerList();
                refreshlayout.finishRefresh(2000/*,false*/);//传入false表示刷新失败
            }
        });
        mSmartRefreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(final RefreshLayout refreshlayout) {
                refreshlayout.finishLoadmore(2000/*,false*/);//传入false表示加载失败
            }
        });
    }

    @OnClick(R.id.FloatingActionButton_timer_list)
    public void addTimer() {
        final TimerBean timer = new TimerBean();
        timer.setAction("add");
        timer.setHub_id(mSPUtil.getString("hub_onenet_id"));
        timer.setPower(0);
        timer.setRepeat("每天");
        showTimerDialog(timer);
    }

    public void showTimerDialog(final TimerBean timer) {
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
        mSpinnerOpenClose.addOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> adapterView, final View view, final int i, final long l) {
                if (openCloseList.get(i).equals("定时关机")) {
                    timer.setPower(0);
                } else {
                    timer.setPower(1);
                }
                //ToastUtils.showShort(openCloseList.get(i));
            }
        });

        final NiceSpinner mSpinnerRepeat = view.findViewById(R.id.NiceSpinner_timer_repeat);
        final LinkedList<String> repeatList = new LinkedList<>(Arrays.asList("每天", "每周1-5", "一次性"));
        timer.setRepeat(timer.getRepeat());
        mSpinnerRepeat.attachDataSource(repeatList);
        mSpinnerRepeat.addOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> adapterView, final View view, final int i, final long l) {
                timer.setRepeat(repeatList.get(i));
                //ToastUtils.showShort(repeatList.get(i));
            }
        });

        // 时间选择器
        final TimePicker mTimePicker = view.findViewById(R.id.TimePicker_timer);
        calendar.setTimeInMillis(System.currentTimeMillis());
        mTimePicker.setIs24HourView(true);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = Calendar.MINUTE;
        if (timer.getTime() != null) {
            hour = Integer.parseInt(timer.getTime().split(":")[0]);
            minute = Integer.parseInt(timer.getTime().split(":")[1]);
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
                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        if (mEtTimerName.getText().length() > 0
                                && mLayoutTimerName.getError() == null) {
                            timer.setTime(mTimePicker.getHour() + ":" + mTimePicker.getMinute());
                            timer.setName(mEtTimerName.getText().toString());
                            mHubDetailPresenter.doHubTimer(
                                    mSPUtil.getString("user_password"),
                                    timer);
                            dialog.dismiss();
                        }
                    }
                })
                .show();
    }

    public void refreshTimerList() {
        if (mData != null) {
            mData.clear();
        }
        mHubDetailPresenter.loadHubTimerList(
                mSPUtil.getString("user_password"),
                mSPUtil.getString("hub_onenet_id"));
    }

    @Override
    public void loadTimerList(final List<TimerBean> timerList) {
        mData = new ArrayList<>();
        if (timerList != null) {
            mData.addAll(timerList);
            mAdapter.setData(mData);
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showSuccessMsg(final String msg) {
        AlerterUtil.showInfo(getHoldingActivity(), msg);
        refreshTimerList();
    }

    @Override
    public void showFailedMsg(final String msg) {
        AlerterUtil.showDanger(getHoldingActivity(), msg);
    }
}

class HubDetailTimerListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<TimerBean> mData;

    private OnItemClickListener mOnItemClickListener;

    public void setData(final List<TimerBean> data) {
        this.mData = data;
        this.notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_timer, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ItemViewHolder) {
            final TimerBean timer = mData.get(position);
            if (timer == null) {
                return;
            }
            if (timer.getPower() == 1) {
                Icon.on(((ItemViewHolder) holder).mTvTimerPic).color(R.color.accent).icon(R.drawable.ic_switch).put();
            }
            ((ItemViewHolder) holder).mTvTimerName.setText(timer.getName());
            final String power = timer.getPower() == 0 ? "定时关机 " : "定时开机 ";
            final String detail_time = power + timer.getRepeat() + " " + timer.getTime();
            ((ItemViewHolder) holder).mTvTimerRepeat.setText(detail_time);
            ((ItemViewHolder) holder).mSwitchOpenClose.setChecked(timer.getStatus() == 1);
        }
    }

    @Override
    public int getItemCount() {
        if (mData == null) {
            return 0;
        }
        return mData.size();
    }

    TimerBean getItem(final int position) {
        return mData.get(position);
    }

    void setOnItemClickListener(final OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(String action, View view, int position);
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.ImageView_timer_pic)
        ImageView mTvTimerPic;
        @BindView(R.id.ImageView_timer_name)
        TextView mTvTimerName;
        @BindView(R.id.ImageView_timer_repeat)
        TextView mTvTimerRepeat;
        @BindView(R.id.Switch_timer_open_close)
        Switch mSwitchOpenClose;

        ItemViewHolder(final View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        @OnClick({R.id.Switch_timer_open_close, R.id.GridLayout_timer, R.id.Button_timer_delete})
        public void onClick(final View view) {
            if (mOnItemClickListener == null) {
                return;
            }
            String action = null;
            switch (view.getId()) {
                case R.id.Switch_timer_open_close:
                    action = mSwitchOpenClose.isChecked() ? "open" : "close";
                    break;
                case R.id.GridLayout_timer:
                    action = "update";
                    break;
                case R.id.Button_timer_delete:
                    action = "delete";
                    break;
            }
            mOnItemClickListener.onItemClick(action, view, this.getLayoutPosition());
        }
    }
}
