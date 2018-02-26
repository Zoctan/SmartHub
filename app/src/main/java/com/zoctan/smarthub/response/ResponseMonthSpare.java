package com.zoctan.smarthub.response;

import com.zoctan.smarthub.beans.MonthSpareBean;

public class ResponseMonthSpare {
    private String msg;
    private String error;
    private MonthSpareBean result;

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(final String msg) {
        this.msg = msg;
    }

    public MonthSpareBean getResult() {
        return this.result;
    }

    public void setResult(final MonthSpareBean result) {
        this.result = result;
    }

    public String getError() {
        return this.error;
    }

    public void setError(final String error) {
        this.error = error;
    }
}