package com.zoctan.smarthub.hub.presenter;

import com.zoctan.smarthub.beans.HubDetailBean;
import com.zoctan.smarthub.hub.model.HubModel;
import com.zoctan.smarthub.hub.model.HubModelImpl;
import com.zoctan.smarthub.hub.view.HubDetailView;

public class HubDetailPresenter {
    private HubDetailView mHubView;
    private HubModel mHubModel;

    public HubDetailPresenter(HubDetailView hubView) {
        this.mHubView = hubView;
        this.mHubModel = new HubModelImpl();
    }

    public void loadHubDetail(final int type) {
        mHubModel.loadHubDetail("", "", new OnLoadHubDetailListener());
    }

    private class OnLoadHubDetailListener implements HubModel.OnLoadHubDetailListener {
        @Override
        public void onSuccess(HubDetailBean hubDetail) {
            mHubView.hideLoading();

        }

        @Override
        public void onFailure(String msg, Exception e) {
            mHubView.hideLoading();

        }
    }
}
