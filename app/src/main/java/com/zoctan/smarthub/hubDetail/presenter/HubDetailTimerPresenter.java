package com.zoctan.smarthub.hubDetail.presenter;

import com.zoctan.smarthub.beans.TimerBean;
import com.zoctan.smarthub.hubDetail.model.HubDetailModel;
import com.zoctan.smarthub.hubDetail.model.HubDetailModelImpl;
import com.zoctan.smarthub.hubDetail.view.HubDetailTimerView;

import java.util.List;

public class HubDetailTimerPresenter {
    private final HubDetailTimerView mHubView;
    private final HubDetailModel mHubModel;

    public HubDetailTimerPresenter(HubDetailTimerView hubDetailView) {
        this.mHubView = hubDetailView;
        this.mHubModel = new HubDetailModelImpl();
    }

    public void loadHubTimerList(final String token, final String hubOneNetId) {
        mHubModel.loadHubTimerList(token, hubOneNetId, new HubDetailTimerPresenter.OnLoadHubTimerListListener());
    }

    private class OnLoadHubTimerListListener implements HubDetailModel.OnLoadHubDetailTimerListener {
        @Override
        public void onSuccess(List<TimerBean> list) {
            mHubView.loadTimerList(list);
        }

        @Override
        public void onFailure(String msg) {
            mHubView.showFailedMsg(msg);
        }
    }

    public void doHubTimer(final String token, final String hubOneNetId, final TimerBean timer) {
        mHubModel.doHubTimer(token, hubOneNetId, timer, new HubDetailTimerPresenter.OnDoHubTimerListener());
    }

    private class OnDoHubTimerListener implements HubDetailModel.OnDoHubTimerListener {
        @Override
        public void onSuccess(String msg) {
            mHubView.showSuccessMsg(msg);
        }

        @Override
        public void onFailure(String msg) {
            mHubView.showFailedMsg(msg);
        }
    }
}
