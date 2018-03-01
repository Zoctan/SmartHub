package com.zoctan.smarthub.hubDetail.model;

import com.zoctan.smarthub.beans.DeviceBean;
import com.zoctan.smarthub.beans.OneNetDataStreamsBean;
import com.zoctan.smarthub.beans.UserBean;

import java.util.List;

public interface HubDetailNowModel {
    void loadHubDevice(String oneNetId, String token, Listener listener);

    void sendOrder(String oneNetId, String token, String order, sendOrderListener listener);

    void loadHubNowList(String oneNetId, String dataStreamIds, Listener listener);

    void doDevice(DeviceBean deviceBean, String token, String action, Listener listener);

    void uploadImg(DeviceBean deviceBean, String token, String qiNiuToken, String photoPath, UploadListener listener);

    void getQiNiuToken(UserBean userBean, DeviceBean deviceBean, UploadListener listener);

    interface UploadListener {
        void onSuccess(String msg);

        void onFailure(String msg);
    }

    interface sendOrderListener {

        void onNowSuccess(String msg);

        void setHubStore(boolean flag);

        void setHubMatch(String list);

        void onNowFailure(String msg);
    }

    interface Listener {
        void onDoDeviceSuccess(String msg);

        void onNowSuccess(String msg);

        void onNowSuccess(DeviceBean deviceBean);

        void onOneNetDataStreamSuccess(List<OneNetDataStreamsBean> oneNetDataStreamList);

        void onNowFailure(String msg);
    }
}
