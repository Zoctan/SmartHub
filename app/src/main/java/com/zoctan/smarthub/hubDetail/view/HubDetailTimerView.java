package com.zoctan.smarthub.hubDetail.view;

import com.zoctan.smarthub.beans.TimerBean;

import java.util.List;

public interface HubDetailTimerView {
    void loadTimerList(List<TimerBean> timerList);

    void showLoading();

    void hideLoading();

    void showSuccessMsg(String msg);

    void showFailedMsg(String msg);
}
