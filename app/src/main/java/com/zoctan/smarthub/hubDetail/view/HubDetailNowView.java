package com.zoctan.smarthub.hubDetail.view;

import com.zoctan.smarthub.beans.DeviceBean;

import java.util.Map;

public interface HubDetailNowView {
    void setDevice(DeviceBean device);

    void setData(Map<String, String> data);

    void showDoDetailDeviceSuccessMsg(String msg);

    void showSuccessMsg(String msg);

    void showFailedMsg(String msg);
}
