package com.zoctan.smarthub.beans;

import java.io.Serializable;

public class HubBean implements Serializable {
    private String onenet_id;
    private String name;
    private String mac;
    private Boolean connected;
    private Boolean is_electric;
    private String eigenvalue;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(final String mac) {
        this.mac = mac;
    }

    public String getEigenvalue() {
        return eigenvalue;
    }

    public void setEigenvalue(final String eigenvalue) {
        this.eigenvalue = eigenvalue;
    }

    public String getOnenet_id() {
        return onenet_id;
    }

    public void setOnenet_id(final String onenet_id) {
        this.onenet_id = onenet_id;
    }

    public Boolean getIs_electric() {
        return is_electric;
    }

    public void setIs_electric(final Boolean is_electric) {
        this.is_electric = is_electric;
    }

    public Boolean getConnected() {
        return connected;
    }

    public void setConnected(final Boolean connected) {
        this.connected = connected;
    }
}
