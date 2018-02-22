package com.zoctan.smarthub.response;

import com.zoctan.smarthub.beans.HubBean;

public class ResponseHub {
    private String msg;
    private String error;
    private HubBean result;

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(final String msg) {
        this.msg = msg;
    }

    public HubBean getResult() {
        return this.result;
    }

    public void setResult(final HubBean result) {
        this.result = result;
    }

    public String getError() {
        return this.error;
    }

    public void setError(final String error) {
        this.error = error;
    }
}