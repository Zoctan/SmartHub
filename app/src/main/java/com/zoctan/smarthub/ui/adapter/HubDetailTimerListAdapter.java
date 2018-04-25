package com.zoctan.smarthub.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nightonke.jellytogglebutton.JellyToggleButton;
import com.zoctan.smarthub.R;
import com.zoctan.smarthub.model.bean.smart.TimerBean;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HubDetailTimerListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<TimerBean> mData;
    private OnItemClickListener mOnItemClickListener;

    public void setData(final List<TimerBean> data) {
        this.mData = data;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_timer, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ItemViewHolder) {
            final TimerBean timer = mData.get(position);
            if (timer == null) {
                return;
            }
            ((ItemViewHolder) holder).mTvTimerTime.setText(timer.getTime());
            ((ItemViewHolder) holder).mTvTimerName.setText(timer.getName());
            final String power = timer.getPower() == 0 ? "关机" : "开机";
            final String detail_time = timer.getRepeat() + " " + power;
            ((ItemViewHolder) holder).mTvTimerRepeat.setText(detail_time);
            ((ItemViewHolder) holder).mSwitchOpenClose.setCheckedImmediately(timer.getStatus() == 1);
        }
    }

    @Override
    public int getItemCount() {
        if (mData == null) {
            return 0;
        }
        return mData.size();
    }

    public TimerBean getItem(final int position) {
        return mData.get(position);
    }

    public void setOnItemClickListener(final OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(String action, View view, int position);
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.TextView_timer_name)
        TextView mTvTimerName;
        @BindView(R.id.TextView_timer_time)
        TextView mTvTimerTime;
        @BindView(R.id.TextView_timer_repeat)
        TextView mTvTimerRepeat;
        @BindView(R.id.Switch_timer_open_close)
        JellyToggleButton mSwitchOpenClose;

        ItemViewHolder(final View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        @OnClick({R.id.Switch_timer_open_close, R.id.CardView_timer, R.id.Button_timer_delete})
        public void onClick(final View view) {
            if (mOnItemClickListener == null) {
                return;
            }
            String action = null;
            switch (view.getId()) {
                case R.id.Switch_timer_open_close:
                    action = mSwitchOpenClose.isChecked() ? "open" : "close";
                    break;
                case R.id.CardView_timer:
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
