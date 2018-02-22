package com.zoctan.smarthub.hubDetail.presenter;

import com.zoctan.smarthub.beans.DeviceBean;
import com.zoctan.smarthub.beans.OneNetDataStreamsBean;
import com.zoctan.smarthub.hubDetail.model.HubDetailModel;
import com.zoctan.smarthub.hubDetail.model.HubDetailModelImpl;
import com.zoctan.smarthub.hubDetail.view.HubDetailNowView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HubDetailNowPresenter {
    private final HubDetailNowView mHubView;
    private final HubDetailModel mHubModel;

    public HubDetailNowPresenter(final HubDetailNowView hubDetailView) {
        this.mHubView = hubDetailView;
        this.mHubModel = new HubDetailModelImpl();
    }

    public void loadHubNowList(final String hubOneNetId, final String dataStreamIds) {
        this.mHubModel.loadHubNowList(hubOneNetId, dataStreamIds, new HubDetailNowPresenter.OnLoadHubDetailNowListener());
    }

    public void loadHubDevice(final String hubOneNetId, final String token) {
        this.mHubModel.loadHubDevice(hubOneNetId, token, new HubDetailNowPresenter.OnLoadHubDetailDeviceListener());
    }

    public void doDevice(final DeviceBean deviceBean, final String token, final String action) {
        this.mHubModel.doDevice(deviceBean, token, action, new HubDetailNowPresenter.OnDoDetailDevice());
    }

    public void resetHub(final String hubOneNetId, final String token) {
        this.mHubModel.resetHub(hubOneNetId, token, new HubDetailNowPresenter.OnListener());
    }

    private class OnListener implements HubDetailModel.OnListener {
        @Override
        public void onSuccess(final String msg) {
            HubDetailNowPresenter.this.mHubView.showSuccessMsg(msg);
        }

        @Override
        public void onFailure(final String msg) {
            HubDetailNowPresenter.this.mHubView.showFailedMsg(msg);
        }
    }

    private class OnDoDetailDevice implements HubDetailModel.OnListener {
        @Override
        public void onSuccess(final String msg) {
            HubDetailNowPresenter.this.mHubView.showDoDetailDeviceSuccessMsg(msg);
        }

        @Override
        public void onFailure(final String msg) {
            HubDetailNowPresenter.this.mHubView.showFailedMsg(msg);
        }
    }

    private class OnLoadHubDetailDeviceListener implements HubDetailModel.OnLoadHubDetailDeviceListener {
        @Override
        public void onSuccess(final DeviceBean deviceBean) {
            HubDetailNowPresenter.this.mHubView.setDevice(deviceBean);
        }

        @Override
        public void onFailure(final String msg) {
            HubDetailNowPresenter.this.mHubView.showFailedMsg(msg);
        }
    }

    private class OnLoadHubDetailNowListener implements HubDetailModel.OnLoadHubDetailNowListener {

        @Override
        public void onSuccess(final List<OneNetDataStreamsBean> oneNetDataStreamList) {
            final Map<String, String> streams = new HashMap<>();
            for (final OneNetDataStreamsBean bean : oneNetDataStreamList) {
                streams.put(bean.getId(), bean.getCurrent_value());
            }
            HubDetailNowPresenter.this.mHubView.setData(streams);
        }

        @Override
        public void onFailure(final String msg) {
            HubDetailNowPresenter.this.mHubView.showFailedMsg(msg);
        }
    }
}
