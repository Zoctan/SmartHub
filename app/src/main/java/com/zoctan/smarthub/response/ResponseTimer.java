package com.zoctan.smarthub.response;

import com.zoctan.smarthub.beans.TimerBean;

import java.util.List;

public class ResponseTimer {
    private String msg;
    private String error;
    private List<TimerBean> result;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<TimerBean> getResult() {
        return result;
    }

    public void setResult(List<TimerBean> result) {
        this.result = result;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}