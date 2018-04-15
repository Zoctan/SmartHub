package com.zoctan.smarthub.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kyleduo.switchbutton.SwitchButton;
import com.zoctan.smarthub.R;
import com.zoctan.smarthub.model.bean.smart.HubBean;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HubListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<HubBean> mData;
    private OnItemClickListener mOnItemClickListener;

    public void setData(final List<HubBean> data) {
        mData = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hub, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ItemViewHolder) {
            final HubBean hub = mData.get(position);
            if (hub == null) {
                return;
            }
            ((ItemViewHolder) holder).mTvHub.setText((hub.getName()));
            ((ItemViewHolder) holder).mSwitchHub.setChecked(hub.getIs_electric());
        }
    }

    @Override
    public int getItemCount() {
        if (mData == null) {
            return 0;
        }
        return mData.size();
    }

    public HubBean getItem(final int position) {
        return mData.get(position);
    }

    public void setOnItemClickListener(final OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(String action, View view, int position);
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.TextView_hub)
        TextView mTvHub;
        @BindView(R.id.Switch_hub)
        SwitchButton mSwitchHub;

        ItemViewHolder(final View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        @OnClick({R.id.Switch_hub, R.id.RelativeLayout_hub})
        public void onClick(final View view) {
            if (mOnItemClickListener == null) {
                return;
            }
            String action = null;
            final HubBean hub = getItem(getLayoutPosition());
            switch (view.getId()) {
                case R.id.Switch_hub:
                    if (hub.getConnected()) {
                        action = hub.getIs_electric() ? "off" : "on";
                    } else {
                        action = "noConnected";
                        // 插座不在线，点开关也没用
                        mSwitchHub.setChecked(!mSwitchHub.isChecked());
                    }
                    break;
                case R.id.RelativeLayout_hub:
                    action = "detail";
                    break;
                /*
                case R.id.Button_hub_edit:
                    action = "update";
                    break;
                case R.id.Button_hub_delete:
                    action = "delete";
                    break;
                    */
            }
            mOnItemClickListener.onItemClick(action, view, getLayoutPosition());
        }
    }
}
