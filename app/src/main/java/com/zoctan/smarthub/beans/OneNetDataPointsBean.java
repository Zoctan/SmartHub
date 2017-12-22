package com.zoctan.smarthub.beans;

import java.io.Serializable;
import java.util.List;

public class OneNetDataPointsBean implements Serializable {
    private String cursor;
    private String count;
    private List<OneNetDataPointListBean> datastreams;

    public String getCursor() {
        return cursor;
    }

    public void setCursor(String cursor) {
        this.cursor = cursor;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public List<OneNetDataPointListBean> getDatastreams() {
        return datastreams;
    }

    public void setDatastreams(List<OneNetDataPointListBean> datastreams) {
        this.datastreams = datastreams;
    }
}
