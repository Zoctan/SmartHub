package com.zoctan.smarthub.hubDetail.model;

import com.zoctan.smarthub.beans.DeviceBean;
import com.zoctan.smarthub.beans.OneNetDataPointsBean;
import com.zoctan.smarthub.beans.OneNetDataStreamsBean;
import com.zoctan.smarthub.beans.TimerBean;

import java.util.List;
import java.util.Map;

public interface HubDetailModel {
    void loadHubDevice(String oneNetId, String token, Listener listener);

    void resetHub(String oneNetId, String token, Listener listener);

    void loadHubNowList(String oneNetId, String dataStreamIds, Listener listener);

    void loadHubSpareList(String oneNetId, String dataStreamIds, Map params, Listener listener);

    void loadHubTimerList(String token, String hubOneNetId, Listener listener);

    void doDevice(DeviceBean deviceBean, String token, String action, Listener listener);

    void doHubTimer(String token, TimerBean timer, Listener listener);

    interface Listener {
        void onSuccess(String msg);

        void onSuccess(DeviceBean deviceBean);

        void onTimerListSuccess(List<TimerBean> timerBean);

        void onOneNetDataStreamSuccess(List<OneNetDataStreamsBean> oneNetDataStreamList);

        void onSuccess(OneNetDataPointsBean oneNetDataPoints);

        void onFailure(String msg);
    }
}
