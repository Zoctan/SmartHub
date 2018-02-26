package com.zoctan.smarthub.hubDetail.model;

import com.zoctan.smarthub.beans.DeviceBean;
import com.zoctan.smarthub.beans.MonthSpareBean;
import com.zoctan.smarthub.beans.OneNetDataStreamsBean;
import com.zoctan.smarthub.beans.TimerBean;
import com.zoctan.smarthub.beans.UserBean;

import java.util.List;

public interface HubDetailModel {
    void loadHubDevice(String oneNetId, String token, Listener listener);

    void resetHub(String oneNetId, String token, Listener listener);

    void loadHubNowList(String oneNetId, String dataStreamIds, Listener listener);

    void loadHubSpareList(String oneNetId, String token, Listener listener);

    void loadHubTimerList(String token, String hubOneNetId, Listener listener);

    void doDevice(DeviceBean deviceBean, String token, String action, Listener listener);

    void doHubTimer(String token, TimerBean timer, Listener listener);

    void uploadImg(DeviceBean deviceBean, String token, String qiNiuToken, String photoPath, UploadListener listener);

    void getQiNiuToken(UserBean userBean, DeviceBean deviceBean, UploadListener listener);

    interface UploadListener {
        void onSuccess(String msg);

        void onFailure(String msg);
    }

    interface Listener {
        void onDoDeviceSuccess(String msg);

        void onNowSuccess(String msg);

        void onTimerSuccess(String msg);

        void onNowSuccess(DeviceBean deviceBean);

        void onTimerListSuccess(List<TimerBean> timerBean);

        void onOneNetDataStreamSuccess(List<OneNetDataStreamsBean> oneNetDataStreamList);

        void onSpareSuccess(MonthSpareBean monthSpareBean);

        void onNowFailure(String msg);

        void onSpareFailure(String msg);

        void onTimerFailure(String msg);
    }
}
