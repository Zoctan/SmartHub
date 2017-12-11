package com.zoctan.smarthub.beans;

import java.io.Serializable;

public class HubDetailBean implements Serializable {
    private String id;
    private String voltage;
    private String current;
    private String distinguish;
    private String spare;
    private String timer;

    public String getId() {
        return id;
    }

    public String getVoltage() {
        return voltage;
    }

    public String getCurrent() {
        return current;
    }

    public String getDistinguish() {
        return distinguish;
    }

    public String getSpare() {
        return spare;
    }

    public String getTimer() {
        return timer;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setVoltage(String voltage) {
        this.voltage = voltage;
    }

    public void setCurrent(String current) {
        this.current = current;
    }

    public void setDistinguish(String distinguish) {
        this.distinguish = distinguish;
    }

    public void setSpare(String spare) {
        this.spare = spare;
    }

    public void setTimer(String timer) {
        this.timer = timer;
    }
}
