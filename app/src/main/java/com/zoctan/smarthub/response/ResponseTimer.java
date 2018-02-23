package com.zoctan.smarthub.response;

import com.zoctan.smarthub.beans.TimerBean;

public class ResponseTimer {
    private String msg;
    private String error;
    private TimerBean result;

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(final String msg) {
        this.msg = msg;
    }

    public TimerBean getResult() {
        return this.result;
    }

    public void setResult(final TimerBean result) {
        this.result = result;
    }

    public String getError() {
        return this.error;
    }

    public void setError(final String error) {
        this.error = error;
    }
}