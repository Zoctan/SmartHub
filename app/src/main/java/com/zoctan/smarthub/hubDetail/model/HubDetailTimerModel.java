package com.zoctan.smarthub.hubDetail.model;

import com.zoctan.smarthub.beans.TimerBean;

import java.util.List;

public interface HubDetailTimerModel {
    void loadHubTimerList(String token, String hubOneNetId, onLoadHubTimerListListener listener);

    void doHubTimer(String token, TimerBean timer, onDoHubTimerListListener listener);

    interface onLoadHubTimerListListener {
        void onSuccess(List<TimerBean> timerBean);

        void onFailure(String msg);
    }

    interface onDoHubTimerListListener {
        void onSuccess(String msg);

        void onFailure(String msg);
    }
}
