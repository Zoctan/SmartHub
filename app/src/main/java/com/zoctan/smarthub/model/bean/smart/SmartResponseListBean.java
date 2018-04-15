package com.zoctan.smarthub.model.bean.smart;

import com.google.gson.JsonArray;

public class SmartResponseListBean {
    private String msg;
    private int error;
    private JsonArray result;

    public String getMsg() {
        return msg;
    }

    public void setMsg(final String msg) {
        this.msg = msg;
    }

    public int getError() {
        return error;
    }

    public void setError(final int error) {
        this.error = error;
    }

    public JsonArray getResult() {
        return result;
    }

    public void setResult(final JsonArray result) {
        this.result = result;
    }
}