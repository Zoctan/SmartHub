package com.zoctan.smarthub.hubDetail.view;

import com.zoctan.smarthub.beans.DeviceBean;

import java.util.Map;

public interface HubDetailNowView {
    void setDevice(DeviceBean device);

    void setData(Map<String, String> data);

    void showLoading();

    void hideLoading();

    void showUpdateImgSuccessMsg(String url, String msg);

    void showDoDeviceSuccessMsg(String msg);

    void showSuccessMsg(String msg);

    void showFailedMsg(String msg);
}
