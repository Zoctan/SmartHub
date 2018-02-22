package com.zoctan.smarthub.response;

import com.zoctan.smarthub.beans.TimerBean;

import java.util.List;

public class ResponseTimerList {
    private String msg;
    private String error;
    private List<TimerBean> result;

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(final String msg) {
        this.msg = msg;
    }

    public List<TimerBean> getResult() {
        return this.result;
    }

    public void setResult(final List<TimerBean> result) {
        this.result = result;
    }

    public String getError() {
        return this.error;
    }

    public void setError(final String error) {
        this.error = error;
    }
}