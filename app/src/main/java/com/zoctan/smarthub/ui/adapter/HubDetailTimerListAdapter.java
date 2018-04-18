package com.zoctan.smarthub.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nightonke.jellytogglebutton.JellyToggleButton;
import com.vansuita.library.Icon;
import com.zoctan.smarthub.R;
import com.zoctan.smarthub.model.bean.smart.TimerBean;
import com.zoctan.smarthub.ui.custom.PopupList;

import java.util.ArrayList;
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

            final List<String> popupMenuItemList = new ArrayList<>();
            popupMenuItemList.add("删除");
            final PopupList popupList = new PopupList(holder.itemView.getContext());
            popupList.setIndicatorSize(30, 40);
            popupList.bind(holder.itemView, popupMenuItemList, new PopupList.PopupListListener() {
                @Override
                public void onPopupListClick(final View contextView, final int contextPosition, final int position) {
                    mOnItemClickListener.onItemClick("delete", holder.itemView, holder.getAdapterPosition());
                }
            });

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
        @BindView(R.id.ImageView_timer_pic)
        ImageView mTvTimerPic;
        @BindView(R.id.ImageView_timer_name)
        TextView mTvTimerName;
        @BindView(R.id.ImageView_timer_repeat)
        TextView mTvTimerRepeat;
        @BindView(R.id.Switch_timer_open_close)
        JellyToggleButton mSwitchOpenClose;

        ItemViewHolder(final View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        @OnClick({R.id.Switch_timer_open_close, R.id.GridLayout_timer})
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
            }
            mOnItemClickListener.onItemClick(action, view, this.getLayoutPosition());
        }
    }
}
