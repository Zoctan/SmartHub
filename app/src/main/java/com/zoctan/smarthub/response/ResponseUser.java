package com.zoctan.smarthub.response;

import com.zoctan.smarthub.beans.UserBean;

public class ResponseUser {
    private String msg;
    private String error;
    private UserBean result;

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(final String msg) {
        this.msg = msg;
    }

    public UserBean getResult() {
        return this.result;
    }

    public void setResult(final UserBean result) {
        this.result = result;
    }

    public String getError() {
        return this.error;
    }

    public void setError(final String error) {
        this.error = error;
    }
}