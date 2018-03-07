package com.zoctan.smarthub.response;

import com.zoctan.smarthub.beans.DeviceBean;

import lombok.Data;

@Data
public class ResponseDevice {
    private String msg;
    private String error;
    private DeviceBean result;
}