package com.zoctan.smarthub.hubDetail.model;

import com.zoctan.smarthub.beans.OneNetDataPointsBean;
import com.zoctan.smarthub.beans.OneNetDataStreamsBean;
import com.zoctan.smarthub.beans.TimerBean;

import java.util.List;
import java.util.Map;

public interface HubDetailModel {
    void loadHubNowList(String oneNetId, String dataStreamIds, OnLoadHubDetailNowListener listener);

    void loadHubSpareList(String oneNetId, String dataStreamIds, Map params, OnLoadHubSpareListListener listener);

    void loadHubTimerList(String token, String hubOneNetId, OnLoadHubDetailTimerListener listener);

    void doHubTimer(String token, String hubOneNetId,
                    TimerBean timer,
                    OnDoHubTimerListener listener);

    interface OnLoadHubDetailNowListener {
        void onSuccess(List<OneNetDataStreamsBean> oneNetDataStreamList);

        void onFailure(String msg);
    }

    interface OnLoadHubSpareListListener {
        void onSuccess(OneNetDataPointsBean oneNetDataPoints);

        void onFailure(String msg);
    }

    interface OnLoadHubDetailTimerListener {
        void onSuccess(List<TimerBean> timerBean);

        void onFailure(String msg);
    }

    interface OnDoHubTimerListener {
        void onSuccess(String msg);

        void onFailure(String msg);
    }
}