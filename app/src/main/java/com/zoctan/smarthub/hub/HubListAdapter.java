package com.zoctan.smarthub.hub;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zoctan.smarthub.R;
import com.zoctan.smarthub.beans.HubBean;

import java.util.List;

public class HubListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<HubBean> mData;
    private Context mContext;

    private HubListAdapter.OnItemClickListener mOnItemClickListener;

    public HubListAdapter(Context context) {
        this.mContext = context;
    }

    public void setData(List<HubBean> data) {
        this.mData = data;
        this.notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hub, parent, false);
        return new ItemViewHolder(v);
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mTvName;
        TextView mTvMac;
        TextView mTvOnline;

        ItemViewHolder(View v) {
            super(v);
            mTvName = v.findViewById(R.id.mTvName);
            mTvMac = v.findViewById(R.id.mTvMac);
            mTvOnline = v.findViewById(R.id.mTvOnline);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(v, this.getLayoutPosition());
            }
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            HubBean hub = mData.get(position);
            if (hub == null) {
                return;
            }
            ((ItemViewHolder) holder).mTvName.setText((hub.getName()));
            ((ItemViewHolder) holder).mTvMac.setText((hub.getMac()));
            String text;
            if (hub.getOnline().equals("false")) {
                text = "离线";
            } else {
                text = "在线";
            }
            ((ItemViewHolder) holder).mTvOnline.setText(text);
        }
    }

    public int getItemCount() {
        if (mData == null) {
            return 0;
        }
        return mData.size();
    }

    public HubBean getItem(int position) {
        return mData.get(position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
}
