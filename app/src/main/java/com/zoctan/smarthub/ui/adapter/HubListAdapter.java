package com.zoctan.smarthub.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nightonke.jellytogglebutton.JellyToggleButton;
import com.zoctan.smarthub.R;
import com.zoctan.smarthub.model.bean.smart.HubBean;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HubListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<HubBean> mData;
    private List<Integer> roomResId;
    private OnItemClickListener mOnItemClickListener;

    public void setData(final List<HubBean> data, final List<Integer> roomResId) {
        this.mData = data;
        this.roomResId = roomResId;
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
            ((ItemViewHolder) holder).mIvRoom.setImageResource(roomResId.get(position));
            ((ItemViewHolder) holder).mTvHub.setText(hub.getName());
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
        @BindView(R.id.ImageView_hub_room)
        ImageView mIvRoom;
        @BindView(R.id.Switch_hub)
        JellyToggleButton mSwitchHub;

        ItemViewHolder(final View view) {
            super(view);
            ButterKnife.bind(this, view);
            mSwitchHub.setOnStateChangeListener((process, state, jtb) -> {
                // process - 当前动画进度，在[0, 1]之间
                // state   - JTB的当前状态，其值为State.LEFT，State.LEFT_TO_RIGHT，State.RIGHT和State.RIGHT_TO_LEFT其中之一
                // jtb     - JTB自身
            });
        }

        @OnClick({R.id.Switch_hub, R.id.CardView_hub, R.id.Button_hub_edit, R.id.Button_hub_delete})
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
                        mSwitchHub.setChecked(false);
                    }
                    break;
                case R.id.CardView_hub:
                    action = "detail";
                    break;
                case R.id.Button_hub_edit:
                    action = "update";
                    break;
                case R.id.Button_hub_delete:
                    action = "delete";
                    break;
            }
            mOnItemClickListener.onItemClick(action, view, getLayoutPosition());
        }
    }
}
