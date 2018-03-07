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

    public void loadHubTimerList(final String token, final String hubOneNetId, final boolean isShowLoading) {
        if (isShowLoading) {
            mTimerView.showLoading();
        }
        mHubModel.loadHubTimerList(token, hubOneNetId, new HubDetailTimerModel.onLoadHubTimerListListener() {
            @Override
            public void onSuccess(final List<TimerBean> timerBean) {
                mTimerView.loadTimerList(timerBean);
                mTimerView.hideLoading();
            }

            @Override
            public void onFailure(final String msg) {
                mTimerView.showFailedMsg(msg);
                mTimerView.hideLoading();
            }
        });
    }

    public void doHubTimer(final String token, final TimerBean timer) {
        mTimerView.showLoading();
        mHubModel.doHubTimer(token, timer, new HubDetailTimerModel.onDoHubTimerListListener() {
            @Override
            public void onSuccess(final String msg) {
                mTimerView.showSuccessMsg(msg);
                mTimerView.hideLoading();
            }

            @Override
            public void onFailure(final String msg) {
                mTimerView.showFailedMsg(msg);
                mTimerView.hideLoading();
            }
        });
    }
}
