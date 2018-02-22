package com.zoctan.smarthub.response;

import com.zoctan.smarthub.beans.HubBean;

import java.util.List;

public class ResponseHubList {
    private String msg;
    private String error;
    private List<HubBean> result;

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(final String msg) {
        this.msg = msg;
    }

    public List<HubBean> getResult() {
        return this.result;
    }

    public void setResult(final List<HubBean> result) {
        this.result = result;
    }

    public String getError() {
        return this.error;
    }

    public void setError(final String error) {
        this.error = error;
    }
}