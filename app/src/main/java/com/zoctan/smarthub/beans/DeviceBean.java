package com.zoctan.smarthub.beans;

import java.io.Serializable;

public class DeviceBean implements Serializable {
    private String id;
    private String onenet_id;
    private String name;
    private String img;
    private String eigenvalue;

    public String getId() {
        return this.id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getOnenet_id() {
        return this.onenet_id;
    }

    public void setOnenet_id(final String onenet_id) {
        this.onenet_id = onenet_id;
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
}
