package com.zoctan.smarthub.response;

import com.zoctan.smarthub.beans.OneNetDataPointsBean;

public class ResponseOneNetDataPoints {
    private String errno;
    private String error;
    private OneNetDataPointsBean data;

    public OneNetDataPointsBean getData() {
        return this.data;
    }

    public void setData(final OneNetDataPointsBean data) {
        this.data = data;
    }

    public String getError() {
        return this.error;
    }

    public void setError(final String error) {
        this.error = error;
    }

    public String getErrno() {
        return errno;
    }

    public void setErrno(final String errno) {
        this.errno = errno;
    }
}