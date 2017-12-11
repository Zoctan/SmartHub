package com.zoctan.smarthub.beans;

import java.io.Serializable;

public class HubBean implements Serializable {
    private String id;
    private String name;
    private String mac;
    private String online;
    private String onenet_id;
    private String eigenvalue;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getMac() {
        return mac;
    }

    public String getOnline() {
        return online;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public void setOnline(String online) {
        this.online = online;
    }

    public String getEigenvalue() {
        return eigenvalue;
    }

    public void setEigenvalue(String eigenvalue) {
        this.eigenvalue = eigenvalue;
    }

    public String getOnenet_id() {
        return onenet_id;
    }

    public void setOnenet_id(String onenet_id) {
        this.onenet_id = onenet_id;
    }
}
