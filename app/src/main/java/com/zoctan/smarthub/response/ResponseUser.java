package com.zoctan.smarthub.response;

import com.zoctan.smarthub.beans.UserBean;

import java.util.List;

public class ResponseUser {
    private String msg;
    private String error;
    private List<UserBean> result;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<UserBean> getResult() {
        return result;
    }

    public void setResult(List<UserBean> result) {
        this.result = result;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}