package com.zoctan.smarthub.hubDetail.presenter;

import com.zoctan.smarthub.beans.TimerBean;
import com.zoctan.smarthub.hubDetail.model.HubDetailTimerModel;
import com.zoctan.smarthub.hubDetail.model.impl.HubDetailTimerModelImpl;
import com.zoctan.smarthub.hubDetail.view.HubDetailTimerView;

import java.util.List;

public class HubDetailTimerPresenter {
    private final HubDetailTimerView mTimerView;
    private final HubDetailTimerModel mHubModel;

    public HubDetailTimerPresenter(final HubDetailTimerView view) {
        mTimerView = view;
        mHubModel = new HubDetailTimerModelImpl();
    }

    public void loadHubTimerList(final String token, final String hubOneNetId) {
        mTimerView.showLoading();
        mHubModel.loadHubTimerList(token, hubOneNetId, new HubDetailTimerPresenter.Listener());
    }

    public void doHubTimer(final String token, final TimerBean timer) {
        mTimerView.showLoading();
        mHubModel.doHubTimer(token, timer, new HubDetailTimerPresenter.Listener());
    }

    private class Listener implements HubDetailTimerModel.Listener {
        @Override
        public void onTimerSuccess(final String msg) {
            mTimerView.showSuccessMsg(msg);
            mTimerView.hideLoading();
        }

        @Override
        public void onTimerListSuccess(final List<TimerBean> timerBean) {
            mTimerView.loadTimerList(timerBean);
            mTimerView.hideLoading();
        }

        @Override
        public void onTimerFailure(final String msg) {
            mTimerView.showFailedMsg(msg);
            mTimerView.hideLoading();
        }
    }
}
