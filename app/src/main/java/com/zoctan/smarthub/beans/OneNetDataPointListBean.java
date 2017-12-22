package com.zoctan.smarthub.beans;

import java.io.Serializable;
import java.util.List;

public class OneNetDataPointListBean implements Serializable {
    private String id;
    private List<OneNetDataPointBean> datapoints;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<OneNetDataPointBean> getDatapoints() {
        return datapoints;
    }

    public void setDatapoints(List<OneNetDataPointBean> datapoints) {
        this.datapoints = datapoints;
    }
}