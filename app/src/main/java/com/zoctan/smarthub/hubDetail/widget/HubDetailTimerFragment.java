package com.zoctan.smarthub.hubDetail.widget;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.zoctan.smarthub.App;
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
        public void onItemClick(String action, View view, int position) {
            if (mData.size() <= 0) {
                return;
            }
            TimerBean timer = mAdapter.getItem(position);
            timer.setAction(action);
            switch (action) {
                case "replace":
                    setTime(timer);
                    //ToastUtils.showShort("replace");
                    break;
                case "add":
                    doTime(timer, true);
                    //ToastUtils.showShort("add");
                    break;
                case "delete":
                    // server need hour and minute...
                    doTime(timer, true);
                    //ToastUtils.showShort("delete");
                    break;
            }
            refreshTimerList();
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
    protected void initView(View view, Bundle savedInstanceState) {
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
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
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshTimerList();
                refreshlayout.finishRefresh(2000/*,false*/);//传入false表示刷新失败
            }
        });
        mSmartRefreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                refreshlayout.finishLoadmore(2000/*,false*/);//传入false表示加载失败
            }
        });
    }

    private void doTime(final TimerBean timer, Boolean empty) {
        if (timer.getName().equals("定时开机")) {
            timer.setWhich("power_on");
        } else {
            timer.setWhich("power_off");
        }
        if (empty) {
            timer.setRepeat("每天");
            timer.setHour(calendar.get(Calendar.HOUR_OF_DAY) + "");
            timer.setMinute(Calendar.MINUTE + "");
        }
        mHubDetailPresenter.doHubTimer(
                mSPUtil.getString("user_password"),
                mSPUtil.getString("hub_onenet_id"),
                timer);
    }

    public void setTime(final TimerBean timer) {
        @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.dialog_timer, null);
        final TimePicker mTimePicker = view.findViewById(R.id.TimePicker_timer);
        NiceSpinner mSpinnerOpenClose = view.findViewById(R.id.NiceSpinner_timer_open_close);
        final LinkedList<String> openCloseList = new LinkedList<>(
                Arrays.asList("定时开机", "定时关机"));
        mSpinnerOpenClose.attachDataSource(openCloseList);
        mSpinnerOpenClose.addOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (openCloseList.get(i).equals("定时开机")) {
                    timer.setWhich("power_on");
                } else {
                    timer.setWhich("power_off");
                }
                //ToastUtils.showShort(openCloseList.get(i));
            }
        });

        NiceSpinner mSpinnerRepeat = view.findViewById(R.id.NiceSpinner_timer_repeat);
        final LinkedList<String> repeatList = new LinkedList<>(
                Arrays.asList("每天", "每周1-5", "一次性"));
        mSpinnerRepeat.attachDataSource(repeatList);
        mSpinnerRepeat.addOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                timer.setRepeat(repeatList.get(i));
                //ToastUtils.showShort(repeatList.get(i));
            }
        });

        // 初始化时间
        calendar.setTimeInMillis(System.currentTimeMillis());
        mTimePicker.setIs24HourView(true);
        mTimePicker.setHour(calendar.get(Calendar.HOUR_OF_DAY));
        mTimePicker.setMinute(Calendar.MINUTE);

        final NiftyDialog dialog = new NiftyDialogUtil(getHoldingActivity())
                .init(R.string.hub_detail_timer_setting,
                        null,
                        R.drawable.ic_edit,
                        R.string.all_ensure);
        dialog
                .setCustomView(view, getHoldingActivity())
                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        timer.setHour(mTimePicker.getHour() + "");
                        timer.setMinute(mTimePicker.getMinute() + "");
                        doTime(timer, false);
                        dialog.dismiss();
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
    public void loadTimerList(List<TimerBean> timerList) {
        mData = new ArrayList<>();
        if (timerList != null) {
            mData.addAll(timerList);
            mAdapter.setData(mData);
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showSuccessMsg(String msg) {
        AlerterUtil.showInfo(getHoldingActivity(), msg);
        refreshTimerList();
    }

    @Override
    public void showFailedMsg(String msg) {
        AlerterUtil.showDanger(getHoldingActivity(), msg);
    }
}

class HubDetailTimerListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<TimerBean> mData;

    private OnItemClickListener mOnItemClickListener;

    public void setData(List<TimerBean> data) {
        this.mData = data;
        this.notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_timer, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            TimerBean timer = mData.get(position);
            if (timer == null) {
                return;
            }
            if (timer.getStatus()) {
                Icon.on(((ItemViewHolder) holder).mTvTimerPic).color(R.color.accent).icon(R.drawable.ic_switch).put();
            }
            ((ItemViewHolder) holder).mTvTimerName.setText(timer.getName());
            String detail_time = timer.getRepeat() + " " + timer.getTime();
            ((ItemViewHolder) holder).mTvTimerRepeat.setText(detail_time);
            ((ItemViewHolder) holder).mSwitchOpenClose.setChecked(timer.getStatus());
        }
    }

    public int getItemCount() {
        if (mData == null) {
            return 0;
        }
        return mData.size();
    }

    TimerBean getItem(int position) {
        return mData.get(position);
    }

    void setOnItemClickListener(OnItemClickListener onItemClickListener) {
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

        ItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        @OnClick({R.id.Switch_timer_open_close, R.id.GridLayout_timer})
        public void onClick(View view) {
            if (mOnItemClickListener == null) {
                return;
            }
            String action;
            switch (view.getId()) {
                case R.id.Switch_timer_open_close:
                    action = mSwitchOpenClose.isChecked() ? "add" : "delete";
                    break;
                default:
                    action = mSwitchOpenClose.isChecked() ? "replace" : "add";
                    break;
            }
            mOnItemClickListener.onItemClick(action, view, this.getLayoutPosition());
        }
    }
}
