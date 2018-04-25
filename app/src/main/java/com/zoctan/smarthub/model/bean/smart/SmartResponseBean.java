package com.zoctan.smarthub.model.bean.smart;

import com.google.gson.JsonObject;

public class SmartResponseBean {
    private String msg;
    private int error;
    private JsonObject result;

    public String getMsg() {
        return msg;
    }

    public void setMsg(final String msg) {
        this.msg = msg;
    }

    public JsonObject getResult() {
        return result;
    }

    public void setResult(final JsonObject result) {
        this.result = result;
    }

    public int getError() {
        return error;
    }

    public void setError(final int error) {
        this.error = error;
    }
}