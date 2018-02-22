package com.zoctan.smarthub.response;

import com.zoctan.smarthub.beans.OneNetDataStreamsBean;

import java.util.List;

public class ResponseOneNetDataStreams {
    private String errno;
    private String error;
    private List<OneNetDataStreamsBean> data;

    public List<OneNetDataStreamsBean> getData() {
        return this.data;
    }

    public void setData(final List<OneNetDataStreamsBean> data) {
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