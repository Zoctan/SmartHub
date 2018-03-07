package com.zoctan.smarthub.hubDetail.view;

import com.zoctan.smarthub.beans.DeviceBean;

import java.util.Map;

public interface HubDetailNowView {
    void setHubStore(boolean flag);

    void setHubMatch(String list);

    void setDevice(DeviceBean device);

    void setData(Map<String, String> data);

    void showStoreOrMatchLoading();

    void hideStoreOrMatchLoading();

    void showLoadDeviceLoading();

    void hideLoadDeviceLoading();

    void showLoading();

    void hideLoading();

    void showUploadSuccessMsg(String msg);

    void showDoDeviceSuccessMsg(String msg);

    void showSuccessMsg(String msg);

    void showFailedMsg(String msg);
}
