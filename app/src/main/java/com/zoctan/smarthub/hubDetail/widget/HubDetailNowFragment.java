package com.zoctan.smarthub.hubDetail.widget;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TextInputEditText;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zoctan.smarthub.R;
import com.zoctan.smarthub.base.BaseFragment;
import com.zoctan.smarthub.beans.DeviceBean;
import com.zoctan.smarthub.hubDetail.presenter.HubDetailNowPresenter;
import com.zoctan.smarthub.hubDetail.view.HubDetailNowView;
import com.zoctan.smarthub.utils.AlerterUtil;
import com.zoctan.smarthub.utils.NiftyDialog;
import com.zoctan.smarthub.utils.NiftyDialogUtil;

import java.util.Map;

import butterknife.BindView;
import io.github.yavski.fabspeeddial.FabSpeedDial;
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter;

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
    @BindView(R.id.FabSpeedDial_hub_detail)
    FabSpeedDial mFabSpeedDial;
    TextInputEditText mEtDeviceInfo;
    private final Handler handler = new Handler();
    private final HubDetailNowPresenter mHubDetailNowPresenter = new HubDetailNowPresenter(this);

    public static HubDetailNowFragment newInstance() {
        return new HubDetailNowFragment();
    }

    @Override
    protected void initView(final View view, final Bundle savedInstanceState) {
        if (mSPUtil.getBoolean("hub_connected")) {
            handler.postDelayed(runnable, 1000);
        }
        if (!mSPUtil.getBoolean("hub_is_electric")) {
            mFabSpeedDial.setVisibility(View.GONE);
        } else {
            mHubDetailNowPresenter.loadHubDevice(
                    mSPUtil.getString("hub_onenet_id"),
                    mSPUtil.getString("user_password")
            );

            mFabSpeedDial.setMenuListener(new SimpleMenuListenerAdapter() {
                @Override
                public boolean onMenuItemSelected(final MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.action_add_device:
                            addDevice();
                            break;
                        case R.id.action_update_device:
                            updateDevice();
                            break;
                        case R.id.action_reset_hub:
                            resetHub();
                            break;
                    }
                    return false;
                }
            });
        }
    }

    @Override
    protected int bindLayout() {
        return R.layout.fragment_hub_detail_now;
    }

    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            updateDetail();
            // 间隔5秒
            handler.postDelayed(this, 1000 * 5);
        }

        void updateDetail() {
            mHubDetailNowPresenter.loadHubNowList(
                    mSPUtil.getString("hub_onenet_id"), "I,V,W,Q");
        }
    };

    public void resetHub() {
        final NiftyDialog dialog = new NiftyDialogUtil(getHoldingActivity())
                .init(R.string.nav_clear,
                        "确定清除存储的所有用电器特征值吗？",
                        R.drawable.ic_clear,
                        R.string.all_ensure);
        dialog
                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        mHubDetailNowPresenter.resetHub(mSPUtil.getString("hub_onenet_id"), mSPUtil.getString("user_password"));
                    }
                })
                .show();
    }

    public void updateDevice() {
        @SuppressLint("InflateParams") final View view = getLayoutInflater().inflate(R.layout.dialog_update_device, null);
        mEtDeviceInfo = view.findViewById(R.id.EditText_device_name);

        final NiftyDialog dialog = new NiftyDialogUtil(getHoldingActivity())
                .init(R.string.hub_detail_update_device,
                        null,
                        R.drawable.ic_update,
                        R.string.all_edit);
        dialog
                .setCustomView(view, getHoldingActivity())
                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        if (mEtDeviceInfo.getText().length() > 0) {
                            final String name = mEtDeviceInfo.getText().toString();
                            final DeviceBean deviceBean = new DeviceBean();
                            deviceBean.setName(name);
                            deviceBean.setOnenet_id(mSPUtil.getString("hub_onenet_id"));
                            mHubDetailNowPresenter.doDevice(deviceBean, mSPUtil.getString("user_password"), "update");
                        }
                        dialog.dismiss();
                    }
                })
                .show();
    }


    public void addDevice() {
        @SuppressLint("InflateParams") final View view = getLayoutInflater().inflate(R.layout.dialog_new_device, null);
        mEtDeviceInfo = view.findViewById(R.id.EditText_device_name);

        final NiftyDialog dialog = new NiftyDialogUtil(getHoldingActivity())
                .init(R.string.hub_detail_add_device,
                        null,
                        R.drawable.ic_update,
                        R.string.all_add);
        dialog
                .setCustomView(view, getHoldingActivity())
                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        if (mEtDeviceInfo.getText().length() > 0) {
                            final String name = mEtDeviceInfo.getText().toString();
                            final DeviceBean deviceBean = new DeviceBean();
                            deviceBean.setName(name);
                            deviceBean.setOnenet_id(mSPUtil.getString("hub_onenet_id"));
                            mHubDetailNowPresenter.doDevice(deviceBean, mSPUtil.getString("user_password"), "add");
                        }
                        dialog.dismiss();
                    }
                })
                .show();
    }

    @Override
    public void setDevice(final DeviceBean device) {
        //mIvAppliances.setImageURI(device.getImg());
        mTvAppliances.setText(device.getName());
        mSPUtil.put("device", device.getName());
    }

    @Override
    public void setData(final Map<String, String> data) {
        mTvVoltage.setText(data.get("V"));
        mTvAmpere.setText(data.get("I"));
        mTvPowerFactor.setText(data.get("Q"));
        mTvPower.setText(data.get("W"));
    }

    @Override
    public void showDoDetailDeviceSuccessMsg(final String msg) {
        AlerterUtil.showInfo(getHoldingActivity(), msg);
        mHubDetailNowPresenter.loadHubDevice(
                mSPUtil.getString("hub_onenet_id"),
                mSPUtil.getString("user_password")
        );
    }

    @Override
    public void showSuccessMsg(final String msg) {
        AlerterUtil.showInfo(getHoldingActivity(), msg);
    }

    @Override
    public void showFailedMsg(final String msg) {
        AlerterUtil.showDanger(getHoldingActivity(), msg);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // 停止刷新
        handler.removeCallbacks(runnable);
    }
}
