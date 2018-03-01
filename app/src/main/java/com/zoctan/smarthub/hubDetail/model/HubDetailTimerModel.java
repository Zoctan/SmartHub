package com.zoctan.smarthub.hubDetail.model;

import com.zoctan.smarthub.beans.TimerBean;

import java.util.List;

public interface HubDetailTimerModel {
    void loadHubTimerList(String token, String hubOneNetId, Listener listener);

    void doHubTimer(String token, TimerBean timer, Listener listener);

    interface Listener {
        void onTimerSuccess(String msg);

        void onTimerListSuccess(List<TimerBean> timerBean);

        void onTimerFailure(String msg);
    }
}
