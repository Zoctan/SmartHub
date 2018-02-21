package com.zoctan.smarthub.hubDetail.widget;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zoctan.smarthub.App;
import com.zoctan.smarthub.R;
import com.zoctan.smarthub.base.BaseFragment;
import com.zoctan.smarthub.hubDetail.presenter.HubDetailNowPresenter;
import com.zoctan.smarthub.hubDetail.view.HubDetailNowView;
import com.zoctan.smarthub.utils.AlerterUtil;

import java.util.Map;

import butterknife.BindView;

public class HubDetailNowFragment extends BaseFragment implements HubDetailNowView {

    @BindView(R.id.ImageView_hub_detail_now_appliances)
    ImageView mIvAppliances;
    @BindView(R.id.TextView_hub_detail_now_appliances_name)
    TextView mTvAppliances;
    @BindView(R.id.TextView_hub_detail_now_voltage)
    TextView mTvVoltage;
    @BindView(R.id.TextView_hub_detail_now_ampere)
    TextView mTvAmpere;
    @BindView(R.id.TextView_hub_detail_now_power_factor)
    TextView mTvPowerFactor;
    @BindView(R.id.TextView_hub_detail_now_power)
    TextView mTvPower;
    private final Handler handler = new Handler();
    private final HubDetailNowPresenter mHubDetailNowPresenter = new HubDetailNowPresenter(this);

    public static HubDetailNowFragment newInstance() {
        return new HubDetailNowFragment();
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        if (mSPUtil.getBoolean("hub_online")) {
            handler.postDelayed(runnable, 1000);
        }
    }

    @Override
    protected int bindLayout() {
        return R.layout.fragment_hub_detail_now;
    }

    private final Runnable runnable = new Runnable() {
        public void run() {
            this.updateDetail();
            // 间隔5秒
            handler.postDelayed(this, 1000 * 5);
        }

        void updateDetail() {
            mHubDetailNowPresenter.loadHubNowList(
                    mSPUtil.getString("hub_onenet_id"),
                    "I,V,W,Q");
        }
    };

    @Override
    public void setData(Map<String, String> data) {
        mTvVoltage.setText(data.get("V"));
        mTvAmpere.setText(data.get("I"));
        mTvPowerFactor.setText(data.get("Q"));
        mTvPower.setText(data.get("W"));
    }

    @Override
    public void showFailedMsg(String msg) {
        AlerterUtil.showDanger(getHoldingActivity(), msg);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // 停止刷新
        handler.removeCallbacks(runnable);
    }
}
