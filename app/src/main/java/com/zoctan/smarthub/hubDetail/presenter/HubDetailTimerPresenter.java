package com.zoctan.smarthub.hubDetail.presenter;

import com.zoctan.smarthub.beans.TimerBean;
import com.zoctan.smarthub.hubDetail.model.HubDetailModel;
import com.zoctan.smarthub.hubDetail.model.HubDetailModelImpl;
import com.zoctan.smarthub.hubDetail.view.HubDetailTimerView;

import java.util.List;

public class HubDetailTimerPresenter {
    private final HubDetailTimerView mHubView;
    private final HubDetailModel mHubModel;

    public HubDetailTimerPresenter(final HubDetailTimerView hubDetailView) {
        this.mHubView = hubDetailView;
        this.mHubModel = new HubDetailModelImpl();
    }

    public void loadHubTimerList(final String token, final String hubOneNetId) {
        mHubModel.loadHubTimerList(token, hubOneNetId, new HubDetailTimerPresenter.OnLoadHubTimerListListener());
    }

    private class OnLoadHubTimerListListener implements HubDetailModel.OnLoadHubDetailTimerListener {
        @Override
        public void onSuccess(final List<TimerBean> list) {
            mHubView.loadTimerList(list);
        }

        @Override
        public void onFailure(final String msg) {
            mHubView.showFailedMsg(msg);
        }
    }

    public void doHubTimer(final String token, final String hubOneNetId, final TimerBean timer) {
        mHubModel.doHubTimer(token, hubOneNetId, timer, new HubDetailTimerPresenter.OnDoHubTimerListener());
    }

    private class OnDoHubTimerListener implements HubDetailModel.OnListener {
        @Override
        public void onSuccess(final String msg) {
            mHubView.showSuccessMsg(msg);
        }

        @Override
        public void onFailure(final String msg) {
            mHubView.showFailedMsg(msg);
        }
    }
}
