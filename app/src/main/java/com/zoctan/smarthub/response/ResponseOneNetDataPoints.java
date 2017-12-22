package com.zoctan.smarthub.response;

import com.zoctan.smarthub.beans.OneNetDataPointsBean;

public class ResponseOneNetDataPoints {
    private Integer errno;
    private String error;
    private OneNetDataPointsBean data;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Integer getErrno() {
        return errno;
    }

    public void setErrno(Integer errno) {
        this.errno = errno;
    }

    public OneNetDataPointsBean getData() {
        return data;
    }

    public void setData(OneNetDataPointsBean data) {
        this.data = data;
    }
}