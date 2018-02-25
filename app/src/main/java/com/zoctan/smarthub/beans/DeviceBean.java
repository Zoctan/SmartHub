package com.zoctan.smarthub.beans;

import java.io.Serializable;

public class DeviceBean implements Serializable {
    private String id;
    private String hub_id;
    private String name;
    private String img;
    private String eigenvalue;

    public String getId() {
        return this.id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getEigenvalue() {
        return this.eigenvalue;
    }

    public void setEigenvalue(final String eigenvalue) {
        this.eigenvalue = eigenvalue;
    }

    public String getImg() {
        return this.img;
    }

    public void setImg(final String img) {
        this.img = img;
    }

    public String getHub_id() {
        return hub_id;
    }

    public void setHub_id(final String hub_id) {
        this.hub_id = hub_id;
    }
}
