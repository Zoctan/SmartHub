package com.zoctan.smarthub.response;

import com.zoctan.smarthub.beans.OneNetDataStreamsBean;

import java.util.List;

public class ResponseOneNetDataStreams {
    private Integer errno;
    private String error;
    private List<OneNetDataStreamsBean> data;

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

    public List<OneNetDataStreamsBean> getData() {
        return data;
    }

    public void setData(List<OneNetDataStreamsBean> data) {
        this.data = data;
    }
}