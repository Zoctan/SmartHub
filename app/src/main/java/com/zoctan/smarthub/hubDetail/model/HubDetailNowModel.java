package com.zoctan.smarthub.hubDetail.model;

import com.zoctan.smarthub.beans.DeviceBean;
import com.zoctan.smarthub.beans.OneNetDataStreamsBean;

import java.util.List;

public interface HubDetailNowModel {
    void loadHubDevice(String oneNetId, String token, onLoadHubDeviceListener listener);

    void sendOrder(String oneNetId, String token, String order, sendOrderListener listener);

    void loadHubNowList(String oneNetId, String dataStreamIds, onLoadHubNowListListener listener);

    void doDevice(DeviceBean deviceBean, String token, onDoDeviceListener listener);

    interface sendOrderListener {
        void onSuccess(String msg);

        void setHubStore(boolean flag);

        void setHubMatch(String list);

        void onFailure(String msg);
    }

    interface onLoadHubDeviceListener {
        void onSuccess(DeviceBean deviceBean);

        void onFailure(String msg);
    }

    interface onLoadHubNowListListener {
        void onSuccess(List<OneNetDataStreamsBean> oneNetDataStreamList);

        void onFailure(String msg);
    }

    interface onDoDeviceListener {
        void onSuccess(String msg);

        void onFailure(String msg);
    }
}
