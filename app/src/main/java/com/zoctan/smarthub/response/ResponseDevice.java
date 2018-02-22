package com.zoctan.smarthub.response;

import com.zoctan.smarthub.beans.DeviceBean;

public class ResponseDevice {
    private String msg;
    private String error;
    private DeviceBean result;

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(final String msg) {
        this.msg = msg;
    }

    public DeviceBean getResult() {
        return this.result;
    }

    public void setResult(final DeviceBean result) {
        this.result = result;
    }

    public String getError() {
        return this.error;
    }

    public void setError(final String error) {
        this.error = error;
    }
}