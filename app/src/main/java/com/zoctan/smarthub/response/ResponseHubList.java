package com.zoctan.smarthub.response;

import com.zoctan.smarthub.beans.HubBean;

import java.util.List;

public class ResponseHubList {
    private String msg;
    private String error;
    private List<HubBean> result;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<HubBean> getResult() {
        return result;
    }

    public void setResult(List<HubBean> result) {
        this.result = result;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}