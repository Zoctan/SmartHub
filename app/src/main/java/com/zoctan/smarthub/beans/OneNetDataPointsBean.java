package com.zoctan.smarthub.beans;

import java.io.Serializable;
import java.util.List;

public class OneNetDataPointsBean implements Serializable {
    private String cursor;
    private List<OneNetDataPointListBean> datastreams;

    public String getCursor() {
        return cursor;
    }

    public void setCursor(final String cursor) {
        this.cursor = cursor;
    }

    public List<OneNetDataPointListBean> getDatastreams() {
        return datastreams;
    }

    public void setDatastreams(final List<OneNetDataPointListBean> datastreams) {
        this.datastreams = datastreams;
    }
}
