package com.zoctan.smarthub.response;

import com.zoctan.smarthub.beans.TokenBean;

import java.util.List;

public class ResponseToken {
    private String msg;
    private String error;
    private List<TokenBean> result;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<TokenBean> getResult() {
        return result;
    }

    public void setResult(List<TokenBean> result) {
        this.result = result;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}