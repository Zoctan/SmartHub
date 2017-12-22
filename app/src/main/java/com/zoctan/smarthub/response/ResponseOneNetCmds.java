package com.zoctan.smarthub.response;

import com.zoctan.smarthub.beans.OneNetDataCmdsBean;

public class ResponseOneNetCmds {
    private Integer errno;
    private String error;
    private OneNetDataCmdsBean data;

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

    public OneNetDataCmdsBean getData() {
        return data;
    }

    public void setData(OneNetDataCmdsBean data) {
        this.data = data;
    }
}

