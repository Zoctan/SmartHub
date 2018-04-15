package com.zoctan.smarthub.model.bean.onenet;

import com.google.gson.JsonArray;

public class OneNetResponseListBean {
    private int errno;
    private String error;
    private JsonArray data;

    public String getError() {
        return error;
    }

    public void setError(final String error) {
        this.error = error;
    }

    public int getErrno() {
        return errno;
    }

    public void setErrno(final int errno) {
        this.errno = errno;
    }

    public JsonArray getData() {
        return data;
    }

    public void setData(final JsonArray data) {
        this.data = data;
    }
}